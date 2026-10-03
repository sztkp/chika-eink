package com.chakra.comicreader.ui.library

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.stateDescription
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chika-eInk", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                actions = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Outlined.Info, contentDescription = "About")
                    }
                },
            )
        },
    ) { padding ->
      Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).background(Ink)) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your library", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() })
                    OutlinedButton(
                        onClick = viewModel::toggleDefaultDirection,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics {
                            stateDescription = if (defaultRtl) "Right to left" else "Left to right"
                        },
                    ) {
                        Text("New comics: " + if (defaultRtl) "right to left (RTL)" else "left to right (LTR)")
                    }
                    OutlinedButton(
                        enabled = !importing,
                        onClick = { picker.launch(arrayOf("*/*")) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(if (importing) "Importing…" else "Add comic", Modifier.padding(start = 8.dp))
                    }
                }
            }
            if (comics.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "No comics yet. Choose Add comic to import a CBZ or CBR.",
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
    Column(Modifier.combinedClickable(
        role = Role.Button,
        onClickLabel = "Read ${comic.title}",
        onLongClickLabel = "Remove ${comic.title}",
        onClick = onClick,
        onLongClick = onLongClick,
    )) {
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
            comic.title,
            style = MaterialTheme.typography.titleMedium,
            color = Cream,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 9.dp),
        )
        Text(
            if (started) "${(pct * 100).toInt()}% · pg ${comic.lastPage + 1}/${comic.pageCount}"
            else "${comic.pageCount} pages",
            style = MaterialTheme.typography.bodyMedium,
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
