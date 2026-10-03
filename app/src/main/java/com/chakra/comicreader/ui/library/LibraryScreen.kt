package com.chakra.comicreader.ui.library

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chakra.comicreader.data.db.ComicEntity
import com.chakra.comicreader.ui.brand.ChikaWordmark
import com.chakra.comicreader.ui.brand.OchreBadge
import com.chakra.comicreader.ui.theme.Libron
import com.chakra.comicreader.ui.theme.Cream
import com.chakra.comicreader.ui.theme.CreamMuted
import com.chakra.comicreader.ui.theme.Crimson
import com.chakra.comicreader.ui.theme.Ink
import com.chakra.comicreader.ui.theme.InkSoft
import com.chakra.comicreader.ui.theme.Ochre
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenComic: (Long) -> Unit,
    onOpenMenu: () -> Unit,
) {
    val comics by viewModel.comics.collectAsStateWithLifecycle()
    val importing by viewModel.importing.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val defaultRtl by viewModel.defaultRightToLeft.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by remember { mutableStateOf<ComicEntity?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importComic(uri)
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Box(Modifier.fillMaxSize().background(Ink)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    ChikaWordmark()
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Ink)
                            .clickable(onClick = onOpenMenu),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "About",
                            tint = Cream,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OchreBadge("YOUR LIBRARY")
                    // Global default direction for new comics; each comic remembers its own once opened.
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "NEW COMICS OPEN",
                            fontFamily = Libron,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.sp,
                            color = CreamMuted,
                        )
                        Text(
                            if (defaultRtl) "RTL" else "LTR",
                            fontFamily = Libron,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Ink,
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Ochre)
                                .clickable { viewModel.toggleDefaultDirection() }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }
            if (comics.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "No comics yet. Tap LOAD COMIC to add a CBZ or CBR.",
                        fontFamily = Libron,
                        fontSize = 13.sp,
                        color = CreamMuted,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }
            items(comics, key = { it.id }) { comic ->
                ComicCard(
                    comic = comic,
                    onClick = { onOpenComic(comic.id) },
                    onLongClick = { pendingDelete = comic },
                )
            }
            item { AddTile(importing = importing, onClick = { picker.launch(arrayOf("*/*")) }) }
        }

        // Preserve snackbar timing and accessibility without the host's fade/scale transition.
        val snackbar = snackbarHostState.currentSnackbarData
        val accessibility = LocalAccessibilityManager.current
        LaunchedEffect(snackbar, accessibility) {
            if (snackbar != null) {
                val timeout = accessibility?.calculateRecommendedTimeoutMillis(
                    originalTimeoutMillis = 4_000L,
                    containsIcons = true,
                    containsText = true,
                    containsControls = false,
                ) ?: 4_000L
                delay(timeout)
                snackbar.dismiss()
            }
        }
        if (snackbar != null) {
            Surface(
                color = Ink,
                contentColor = Cream,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .navigationBarsPadding().padding(12.dp)
                    .border(1.dp, Cream)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            ) {
                Text(snackbar.visuals.message, modifier = Modifier.padding(16.dp))
            }
        }
    }

    pendingDelete?.let { comic ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Remove comic?", fontFamily = Libron, fontWeight = FontWeight.Bold) },
            text = { Text("This removes the imported copy. The original file is untouched.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteComic(comic.id); pendingDelete = null }) {
                    Text("DELETE", color = Crimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("CANCEL") } },
            containerColor = InkSoft,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ComicCard(comic: ComicEntity, onClick: () -> Unit, onLongClick: () -> Unit) {
    val pct = if (comic.pageCount > 0) ((comic.lastPage + 1f) / comic.pageCount).coerceIn(0f, 1f) else 0f
    val started = comic.lastPage > 0
    Column(Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(4.dp))
                .background(InkSoft)
                .border(3.dp, Cream, RoundedCornerShape(4.dp)),
        ) {
            val cover = rememberCover(comic.coverPath)
            if (cover != null) {
                Image(cover, comic.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                GeneratedCover(comic.title)
            }
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(7.dp).background(Ink).border(1.dp, Cream)) {
                Box(Modifier.fillMaxWidth(pct).fillMaxHeight().background(Ochre))
            }
        }
        Text(
            comic.title.uppercase(),
            fontFamily = Libron,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Cream,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 9.dp),
        )
        Text(
            if (started) "${(pct * 100).toInt()}% · pg ${comic.lastPage + 1}/${comic.pageCount}"
            else "${comic.pageCount} pages",
            fontFamily = Libron,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            color = CreamMuted,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun GeneratedCover(title: String) {
    Box(Modifier.fillMaxSize().background(InkSoft)) {
        Text(
            title.uppercase(),
            fontFamily = Libron, fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Cream,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddTile(importing: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(4.dp))
            .combinedClickable(onClick = onClick, onLongClick = {})
            .drawBehind {
                drawRoundRect(
                    color = CreamMuted,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = null,
                    ),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(4.dp)).background(Crimson),
                contentAlignment = Alignment.Center,
            ) {
                if (importing) Text("…", color = Ink)
                else Icon(Icons.Default.Add, contentDescription = "Load comic", tint = Ink)
            }
            Text(if (importing) "IMPORTING…" else "LOAD COMIC", fontFamily = Libron, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Cream)
        }
    }
}

@Composable
private fun rememberCover(path: String?): ImageBitmap? {
    val state by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = if (path == null) null else withContext(Dispatchers.IO) {
            runCatching {
                val file = File(path)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() else null
            }.getOrNull()
        }
    }
    return state
}
