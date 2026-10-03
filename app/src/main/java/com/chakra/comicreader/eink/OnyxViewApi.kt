package com.chakra.comicreader.eink

import android.os.Build
import android.util.Log
import android.view.View
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Optional firmware API, not an SDK dependency. All vendor names live here.
 * The View methods are undocumented and may be absent or blocked by Android hidden-API rules.
 * A verified firmware profile supplies identifiers when constant reflection is blocked.
 * No hidden-API bypass or global/application mode changes.
 */
internal class OnyxViewApi private constructor(
    private val getMode: Method,
    private val setMode: Method,
    private val invalidate: Method,
    val readingMode: EinkReadingMode,
    private val partial: Int,
    private val full: Int?,
) {
    val supportsFullRefresh: Boolean get() = full != null

    fun backend(view: View): EinkBackend = object : EinkBackend {
        private var original: Int? = null

        override fun enter() {
            val previous = (getMode.invoke(view) as Number).toInt()
            if (previous != partial) {
                // Save before calling: firmware can change state even when reflection reports failure.
                original = previous
                checkedInvoke(setMode, view, partial)
            }
        }

        override fun restore() {
            original?.let {
                checkedInvoke(setMode, view, it)
                original = null
            }
        }

        override fun refresh(refresh: EinkRefresh) {
            val mode = if (refresh == EinkRefresh.FULL) checkNotNull(full) else partial
            checkedInvoke(invalidate, view, mode)
        }
    }

    companion object {
        fun discover(): OnyxViewApi {
            val helper = Class.forName("android.onyx.ViewUpdateHelper", false, View::class.java.classLoader)
            fun mode(name: String): Int? = try {
                helper.getField(name).getInt(null).takeIf { it > 0 }
            } catch (_: NoSuchFieldException) {
                null
            }
            val profile = verifiedPalma2Modes(Build.MODEL, Build.VERSION.SDK_INT, Build.FINGERPRINT, Build.DISPLAY)
            // This firmware publishes View hooks but blocks access to its constant fields.
            // The identifiers are scoped to its exact build, not assumed across BOOX devices.
            val regal = mode("UI_REGAL_MODE") ?: profile?.regal
            val gu = mode("UI_GU_MODE") ?: profile?.gu
            if (profile != null) Log.d("ChikaEink", "Verified Palma 2 firmware profile")
            val partial = regal ?: gu ?: error("No REGAL/GU firmware constant")
            val getMode = View::class.java.getMethod("getDefaultUpdateMode")
            val setMode = View::class.java.getMethod("setDefaultUpdateMode", Int::class.javaPrimitiveType)
            val invalidate = if (profile != null) {
                View::class.java.getMethod("refreshScreen", Int::class.javaPrimitiveType)
            } else try {
                View::class.java.getMethod("invalidate", Int::class.javaPrimitiveType)
            } catch (_: NoSuchMethodException) {
                View::class.java.getMethod("invalidateWithUpdateMode", Int::class.javaPrimitiveType)
            }
            check(listOf(getMode, setMode, invalidate).none { Modifier.isStatic(it.modifiers) })
            return OnyxViewApi(getMode, setMode, invalidate,
                if (regal != null) EinkReadingMode.REGAL else EinkReadingMode.GU,
                partial, mode("UI_GC_MODE") ?: profile?.gc)
        }

        private fun checkedInvoke(method: Method, view: View, mode: Int) {
            check(method.invoke(view, mode) != false) { "Firmware rejected ${method.name}" }
        }
    }
}

internal data class OnyxModeIdentifiers(val regal: Int, val gu: Int, val gc: Int)

/** Identifiers/signatures verified against the connected device's framework, not SDK code. */
internal fun verifiedPalma2Modes(model: String, sdk: Int, fingerprint: String, display: String): OnyxModeIdentifiers? =
    if (model == "Palma2" && sdk == 33 &&
        fingerprint == "ONYX/TabBoox/TabBoox:13/TKQ1.230615.001/GV2.027.SQ83A:user/release-keys" &&
        display == "2026-05-13_18-43_4.2-rel_05132_72a2c1b9e") {
        OnyxModeIdentifiers(regal = 6, gu = 2, gc = 98)
    } else null
