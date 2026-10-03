package com.chakra.comicreader.eink

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver

internal enum class EinkReadingMode { REGAL, GU }
internal data class EinkCapabilities(
    val isBoox: Boolean = false,
    val readingMode: EinkReadingMode? = null,
    val fullRefresh: Boolean = false,
)

/** Semantic reader bridge. A View is leased only while the reader is foregrounded. */
internal interface EinkController {
    val capabilities: EinkCapabilities
    fun enterReader(view: View)
    fun leaveReader()
    fun pause()
    fun resume()
    fun imageDrawn(page: Int, image: Any)
    fun interactionStarted()
    fun interactionEnded()
    fun requestFullRefresh()
    fun dispose()
}

internal class NoOpEinkController(override val capabilities: EinkCapabilities = EinkCapabilities()) : EinkController {
    override fun enterReader(view: View) = Unit
    override fun leaveReader() = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun imageDrawn(page: Int, image: Any) = Unit
    override fun interactionStarted() = Unit
    override fun interactionEnded() = Unit
    override fun requestFullRefresh() = Unit
    override fun dispose() = Unit
}

internal class BooxEinkController(private val api: OnyxViewApi) : EinkController {
    private var view: View? = null
    private var session: EinkSession? = null
    private var paused = false
    private var failed = false
    private var pending: EinkRefresh? = null
    private var callback: Runnable? = null
    private var observer: ViewTreeObserver? = null
    private var registration: Runnable? = null
    private var preDraw: ViewTreeObserver.OnPreDrawListener? = null
    private var lastDrawn: Pair<Int, Any>? = null

    override val capabilities: EinkCapabilities
        get() = EinkCapabilities(true, api.readingMode.takeUnless { failed }, api.supportsFullRefresh && !failed)

    override fun enterReader(view: View) {
        leaveReader()
        if (failed) return
        this.view = view
        session = EinkSession(api.backend(view), EinkRefreshPolicy(supportsFullRefresh = api.supportsFullRefresh)) {
            failed = true
            cancelRefresh()
            Log.w(TAG, "Firmware API failed; using normal Android rendering", it)
        }
        if (!paused) {
            Log.d(TAG, "Enter reader: ${api.readingMode}, host=${view.javaClass.simpleName}")
            session?.enter()
        }
    }

    override fun leaveReader() {
        cancelRefresh()
        if (view != null) Log.d(TAG, "Leave reader; restore View mode")
        session?.leave()
        session = null
        view = null
        lastDrawn = null
    }

    override fun pause() {
        paused = true
        cancelRefresh()
        session?.leave()
        if (view != null) Log.d(TAG, "Pause reader; restore View mode")
    }

    override fun resume() {
        if (!paused) return
        paused = false
        if (view != null && !failed) Log.d(TAG, "Resume reader: ${api.readingMode}")
        session?.enter()
        queueCurrentImage()
    }

    override fun imageDrawn(page: Int, image: Any) {
        if (paused || failed) return
        lastDrawn = page to image
        val refresh = session?.imageDrawn(page, image) ?: return
        scheduleAfterDraw(refresh)
    }

    override fun interactionStarted() {
        if (pending == EinkRefresh.FULL) session?.requestFullRefresh()
        cancelRefresh()
        session?.interactionStarted()
    }

    override fun interactionEnded() {
        session?.interactionEnded()
        queueCurrentImage()
    }

    override fun requestFullRefresh() {
        if (paused || !capabilities.fullRefresh) return
        session?.requestFullRefresh()
        queueCurrentImage()
    }

    override fun dispose() = leaveReader()

    private fun queueCurrentImage() {
        if (paused || failed) return
        val (page, image) = lastDrawn ?: return
        val refresh = session?.imageDrawn(page, image) ?: return
        // Compose may reuse cached display lists on a native View redraw. Schedule explicitly
        // for gesture/manual/lifecycle requests rather than requiring its Canvas lambda to rerun.
        scheduleAfterDraw(refresh)
    }

    /** Coalesce within the drawn frame, then request one waveform on the actual Compose host. */
    private fun scheduleAfterDraw(refresh: EinkRefresh) {
        if (pending != EinkRefresh.FULL) pending = refresh
        if (callback != null) return
        val host = view ?: return
        val currentSession = session ?: return
        lateinit var task: Runnable
        task = Runnable {
            if (callback !== task) return@Runnable
            callback = null
            observer = null
            val request = pending
            pending = null
            if (request != null && !paused && !failed && view === host && session === currentSession) {
                Log.d(TAG, "${if (request == EinkRefresh.FULL) "GC full" else "${api.readingMode} partial"} refresh")
                currentSession.refresh(request)
            }
        }
        callback = task
        // Frame-commit callbacks are captured before Canvas drawing. Register before the
        // next traversal, never from inside the Compose draw that signalled this event.
        val register = Runnable {
            registration = null
            if (callback !== task) return@Runnable
            val tree = host.viewTreeObserver
            observer = tree
            val listener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (tree.isAlive) tree.removeOnPreDrawListener(this)
                    preDraw = null
                    if (callback === task) {
                        if (Build.VERSION.SDK_INT >= 29 && host.isHardwareAccelerated) {
                            tree.registerFrameCommitCallback(task)
                        } else {
                            // Posted from pre-draw: runs after this traversal on Android 8/9.
                            host.post(task)
                        }
                    }
                    return true
                }
            }
            preDraw = listener
            tree.addOnPreDrawListener(listener)
            host.invalidate()
        }
        registration = register
        host.post(register)
    }

    private fun cancelRefresh() {
        registration?.let { view?.removeCallbacks(it) }
        registration = null
        preDraw?.let { listener -> observer?.takeIf { it.isAlive }?.removeOnPreDrawListener(listener) }
        preDraw = null
        callback?.let { task ->
            if (Build.VERSION.SDK_INT >= 29) observer?.takeIf { it.isAlive }?.unregisterFrameCommitCallback(task)
            view?.removeCallbacks(task)
        }
        callback = null
        observer = null
        pending = null
    }
}

internal fun createEinkController(): EinkController {
    if (!isBooxDevice(Build.MANUFACTURER, Build.BRAND)) {
        Log.d(TAG, "Controller: normal Android")
        return NoOpEinkController()
    }
    Log.d(TAG, "Detected BOOX: ${Build.MANUFACTURER}/${Build.BRAND}/${Build.MODEL}")
    return try {
        val controller = BooxEinkController(OnyxViewApi.discover())
        Log.d(TAG, "Controller: BOOX ${controller.capabilities.readingMode}, GC=${controller.capabilities.fullRefresh}")
        controller
    } catch (failure: Exception) {
        Log.w(TAG, "BOOX firmware API unavailable; using normal Android rendering", failure)
        NoOpEinkController(EinkCapabilities(isBoox = true))
    } catch (failure: LinkageError) {
        Log.w(TAG, "BOOX firmware linkage unavailable; using normal Android rendering", failure)
        NoOpEinkController(EinkCapabilities(isBoox = true))
    }
}

private const val TAG = "ChikaEink"
