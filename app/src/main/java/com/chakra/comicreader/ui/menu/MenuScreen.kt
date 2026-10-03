package com.chakra.comicreader.ui.menu

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.compose.ui.res.stringResource
import com.chakra.comicreader.R

private const val FORK_URL = "https://github.com/sztkp/chika-eink"
private const val UPSTREAM_URL = "https://github.com/batunii/chika"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "—"
    }
    val openUrl: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        runCatching { context.startActivity(intent) }.onFailure {
            Toast.makeText(context, "No app available to open this link", Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() })
                Text("Version $version", style = MaterialTheme.typography.bodyMedium)
                Text("An independent Android fork of Chika by Chakra, optimized for e-ink reading.",
                    style = MaterialTheme.typography.bodyLarge)
                Text("Detects comic panels on-device and guides you through each page, panel by panel.",
                    style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider()
            AboutLink("Fork repository", "Kuro source code") { openUrl(FORK_URL) }
            AboutLink("Upstream repository", "github.com/batunii/chika") { openUrl(UPSTREAM_URL) }
            HorizontalDivider()
            AboutLink("Privacy policy", "Kuro collects no data") { openUrl("$FORK_URL/blob/main/PRIVACY.md") }
            AboutLink("Licenses and notices", "Open-source dependencies and artwork") {
                openUrl("$FORK_URL/blob/main/THIRD_PARTY_NOTICES.md")
            }
        }
    }
}

@Composable
private fun AboutLink(title: String, subtitle: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
            .clickable(role = Role.Button, onClickLabel = "Open $title in browser", onClick = onClick),
    )
}
