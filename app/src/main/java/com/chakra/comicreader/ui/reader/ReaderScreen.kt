package com.chakra.comicreader.ui.reader

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chakra.comicreader.ui.theme.Libron
import com.chakra.comicreader.ui.theme.Cream
import com.chakra.comicreader.ui.theme.CreamMuted
import com.chakra.comicreader.ui.theme.Ink
import com.chakra.comicreader.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var chromeVisible by remember { mutableStateOf(true) }

    // Keep the reader immersive, including when its compact controls are visible.
    // System bars remain available by edge swipe and are restored on leaving the reader.
    val view = LocalView.current
    val activity = view.context as? MainActivity
    DisposableEffect(activity, viewModel) {
        val handler = ReaderKeyHandler(viewModel::next, viewModel::previous)
        activity?.readerKeyHandler = handler
        onDispose {
            if (activity != null && activity.readerKeyHandler === handler) activity.readerKeyHandler = null
        }
    }
    val window = (view.context as? Activity)?.window
    LaunchedEffect(window) {
        window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, view)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
    DisposableEffect(window) {
        onDispose {
            window ?: return@onDispose
            WindowCompat.getInsetsController(window, view).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().navigationBarsPadding(),
    ) {
        if (chromeVisible) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(Ink)
                    .padding(horizontal = 4.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Cream)
                }
                Text(
                    state.title, fontFamily = Libron, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, color = Cream, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = viewModel::showFullPage) {
                    Icon(Icons.Default.ZoomOutMap, contentDescription = "Show whole page", tint = Cream)
                }
                DirectionChip(rightToLeft = state.rightToLeft, onClick = viewModel::toggleReadingDirection)
            }
        }
        // Frame into the available artwork area rather than drawing under either bar.
        Box(Modifier.weight(1f).fillMaxWidth().clipToBounds()) {
            when {
                state.error != null -> ErrorView(state.error!!)
                state.page == null -> LoadingView()
                else -> PageViewer(
                    state = state,
                    onNext = viewModel::next,
                    onPrev = viewModel::previous,
                    onNextPage = viewModel::nextPage,
                    onPrevPage = viewModel::previousPage,
                    onToggleChrome = { chromeVisible = !chromeVisible },
                    onShowFullPage = viewModel::showFullPage,
                )
            }
        }
        if (chromeVisible && state.pageCount > 0) {
            ReaderCounts(
                pageIndex = state.pageIndex,
                pageCount = state.pageCount,
                panelNumber = state.panelLabel,
                panelCount = state.panels.size,
            )
        }
    }
}

@Composable
private fun ReaderCounts(
    pageIndex: Int,
    pageCount: Int,
    panelNumber: Int?,
    panelCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ink)
            .padding(horizontal = 12.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Panel ${panelNumber ?: 0}/$panelCount",
                fontFamily = Libron, fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CreamMuted,
            )
            Text(
                "${pageIndex + 1}/$pageCount",
                fontFamily = Libron, fontWeight = FontWeight.Bold,
                fontSize = 12.sp, color = Cream,
            )
        }
    }
}

@Composable
private fun PageViewer(
    state: ReaderUiState,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit,
    onToggleChrome: () -> Unit,
    onShowFullPage: () -> Unit,
) {
    val bitmap = state.page ?: return
    val image = remember(bitmap) { bitmap.asImageBitmap() }
    val rtl = state.rightToLeft
    val scope = rememberCoroutineScope()
    // The framed view counts as "full page" at the intro/outro slots; a flick only turns pages there.
    val isFullPage by rememberUpdatedState(state.isFullPageView)

    // Use the existing selected panel directly; no interpolated camera or page fade.
    val camera by rememberUpdatedState(state.currentCamera)

    // Free pinch-to-zoom / pan, reset whenever the framed view changes. The comic floats over the
    // background: it can be moved freely in any direction (even past the framed region), bounded only
    // so a grabbable sliver always stays on screen.
    var userScale by remember(state.pageIndex, state.slot, state.currentCamera) { mutableFloatStateOf(1f) }
    var userPanX by remember(state.pageIndex, state.slot, state.currentCamera) { mutableFloatStateOf(0f) }
    var userPanY by remember(state.pageIndex, state.slot, state.currentCamera) { mutableFloatStateOf(0f) }
    val resetView: () -> Unit = {
        userScale = 1f
        userPanX = 0f
        userPanY = 0f
    }

    // A single pointer handler so taps, double-taps, and pan/zoom never fight over the same touch.
    // Tap zones drive panel navigation (which crosses page boundaries) and chrome; one-finger drag
    // pans the floating comic; pinch zooms; double-tap jumps back to the whole-page view. A single
    // tap is deferred briefly so a following tap can be recognised as a double-tap.
    var pendingTap by remember { mutableStateOf<Job?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(rtl, state.pageIndex, state.slot, state.currentCamera) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var pan = Offset.Zero
                    var zoom = 1f
                    var maxPointers = 1
                    var pastSlop = false
                    val slop = viewConfiguration.touchSlop
                    val velocity = VelocityTracker()

                    do {
                        val event = awaitPointerEvent()
                        maxPointers = maxOf(maxPointers, event.changes.count { it.pressed })
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()
                        pan += panChange
                        zoom *= zoomChange
                        event.changes.firstOrNull { it.id == down.id }?.let {
                            velocity.addPosition(it.uptimeMillis, it.position)
                        }
                        if (!pastSlop && (abs(zoom - 1f) > 0.02f || pan.getDistance() > slop)) {
                            pastSlop = true
                        }
                        if (pastSlop) {
                            val newScale = (userScale * zoomChange).coerceIn(1f, 5f)
                            userScale = newScale
                            // Horizontal stays covered (no background at the sides); vertical floats
                            // over the background, bounded so a sliver always stays grabbable.
                            val draw = computePageDraw(
                                camera, image.width, image.height,
                                size.width.toFloat(), size.height.toFloat(),
                            )
                            val cw = size.width.toFloat()
                            val ch = size.height.toFloat()
                            val baseLeft = cw / 2f + (draw.left - cw / 2f) * newScale
                            val baseTop = ch / 2f + (draw.top - ch / 2f) * newScale
                            userPanX = clampPanHorizontal(userPanX + panChange.x, baseLeft, draw.scaledWidth * newScale, cw)
                            userPanY = clampPanVertical(userPanY + panChange.y, baseTop, draw.scaledHeight * newScale, ch)
                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        }
                    } while (event.changes.any { it.pressed })

                    if (!pastSlop) { // a tap (not a drag/pinch)
                        val w = size.width
                        val tapX = down.position.x
                        val inFlight = pendingTap
                        if (inFlight != null && inFlight.isActive) {
                            // Second quick tap → double-tap: back to the whole-page view from any
                            // panel (mirrors the ZoomOutMap button); resetView immediately resets pinch
                            // zoom, and covers the already-on-full-page case where the slot
                            // doesn't change.
                            inFlight.cancel()
                            pendingTap = null
                            onShowFullPage()
                            resetView()
                        } else {
                            pendingTap = scope.launch {
                                delay(DOUBLE_TAP_WINDOW_MS)
                                when {
                                    tapX < w / 3f -> onPrev()
                                    tapX > w * 2f / 3f -> onNext()
                                    else -> onToggleChrome()
                                }
                                pendingTap = null
                            }
                        }
                    } else if (maxPointers == 1 && userScale <= 1.01f && isFullPage) {
                        // A quick, mostly-horizontal one-finger flick turns the page (a slow drag just
                        // floats the comic over the background and stays put). Velocity, not distance,
                        // is what separates a deliberate swipe from a reposition.
                        val v = velocity.calculateVelocity()
                        if (abs(v.x) > FLICK_VELOCITY_PX_S && abs(v.x) > abs(v.y) * SWIPE_HORIZONTAL_BIAS) {
                            if (v.x < 0f) { if (rtl) onPrevPage() else onNextPage() }
                            else { if (rtl) onNextPage() else onPrevPage() }
                        }
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val draw = computePageDraw(
                camera = camera,
                bitmapW = image.width,
                bitmapH = image.height,
                containerW = size.width,
                containerH = size.height,
            )
            val cx = size.width / 2f
            val cy = size.height / 2f
            val left = cx + (draw.left - cx) * userScale + userPanX
            val top = cy + (draw.top - cy) * userScale + userPanY
            val w = draw.scaledWidth * userScale
            val h = draw.scaledHeight * userScale

            drawImage(
                image = image,
                dstOffset = IntOffset(left.roundToInt(), top.roundToInt()),
                dstSize = IntSize(w.roundToInt(), h.roundToInt()),
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Loading…", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

/**
 * Horizontal clamp ("cover"): the comic never exposes background to its left or right. When wider
 * than the screen (zoomed) it pans only until an edge meets the screen edge; when it fits, it's
 * centered. This keeps sideways drags clean and reserves horizontal flicks for page turns. [base] is
 * the image's left at zero pan, [size] its scaled width, [container] the screen width.
 */
private fun clampPanHorizontal(pan: Float, base: Float, size: Float, container: Float): Float {
    if (size <= container) return (container - size) / 2f - base
    return pan.coerceIn(container - size - base, -base)
}

/**
 * Vertical clamp ("float over background"): the comic may move freely up/down — even past the top or
 * bottom edge, revealing background — but is bounded so at least [MIN_VISIBLE_FRACTION] of it always
 * stays on screen and grabbable. [base] is the image's top at zero pan, [size] its scaled height,
 * [container] the screen height.
 */
private fun clampPanVertical(pan: Float, base: Float, size: Float, container: Float): Float {
    val keep = MIN_VISIBLE_FRACTION * minOf(size, container)
    return pan.coerceIn(keep - size - base, container - keep - base)
}

/** Fraction of the comic kept on screen when it's dragged off the top/bottom, so it can't be lost. */
private const val MIN_VISIBLE_FRACTION = 0.15f

/** A single tap waits this long for a possible second tap before acting, enabling double-tap. */
private const val DOUBLE_TAP_WINDOW_MS = 220L

/** Min horizontal release speed (px/s) for a one-finger drag to count as a page-turn flick. */
private const val FLICK_VELOCITY_PX_S = 700f

/** A flick must be this many times more horizontal than vertical to turn the page. */
private const val SWIPE_HORIZONTAL_BIAS = 1.2f

/** Reading-direction control that shows its current state (LTR/RTL) so its purpose is obvious. */
@Composable
private fun DirectionChip(rightToLeft: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Cream, modifier = Modifier.size(15.dp))
        Text(
            if (rightToLeft) "RTL" else "LTR",
            fontFamily = Libron,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = Cream,
        )
    }
}
