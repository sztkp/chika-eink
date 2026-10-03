package com.chakra.comicreader.ui.menu

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chakra.comicreader.ui.brand.ChikaWordmark
import com.chakra.comicreader.ui.brand.OchreBadge
import com.chakra.comicreader.ui.theme.Libron
import com.chakra.comicreader.ui.theme.Cream
import com.chakra.comicreader.ui.theme.CreamMuted
import com.chakra.comicreader.ui.theme.Crimson
import com.chakra.comicreader.ui.theme.Ink
import com.chakra.comicreader.ui.theme.InkSoft
import com.chakra.comicreader.ui.theme.Ochre

private const val DONATION_URL = "https://github.com/batunii/chika"
private const val PRIVACY_URL = "https://github.com/sztkp/chika-eink/blob/main/PRIVACY.md"
private const val LICENSES_URL = "https://github.com/sztkp/chika-eink/blob/main/THIRD_PARTY_NOTICES.md"

@Composable
fun MenuScreen(
    amoledTheme: Boolean,
    onSetAmoledTheme: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "—"
    }
    val openDonation = {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DONATION_URL)))
    }

    Box(Modifier.fillMaxSize().background(Ink)) {

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 28.dp),
        ) {
            // Header: back + title.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Ink)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = Cream, modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.size(14.dp))
                OchreBadge("MENU")
            }

            Spacer(Modifier.size(28.dp))

            // ---- Theme ---------------------------------------------------------------
            SectionLabel("THEME")
            Spacer(Modifier.size(10.dp))
            ToggleRow(
                title = "Black reader background",
                subtitle = "Use a black canvas around comic pages.",
                checked = amoledTheme,
                onCheckedChange = onSetAmoledTheme,
            )

            Spacer(Modifier.size(32.dp))

            // ---- About ---------------------------------------------------------------
            SectionLabel("ABOUT")
            Spacer(Modifier.size(16.dp))
            ChikaWordmark()
            Spacer(Modifier.size(12.dp))
            OchreBadge("VERSION $version")
            Spacer(Modifier.size(14.dp))
            Text(
                "Independent fork of Chika by Chakra",
                fontFamily = Libron,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Cream,
            )
            Spacer(Modifier.size(18.dp))
            Text(
                "Chika-eInk is a fork of a comic reader that detects panels on-device and guides you through each " +
                    "page, panel by panel.",
                fontFamily = Libron,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Cream,
            )

            Spacer(Modifier.size(18.dp))
            LinkRow("Privacy policy — Chika collects no data.") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
            }
            Spacer(Modifier.size(8.dp))
            LinkRow("Open-source licenses & notices.") {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LICENSES_URL)))
            }

            Spacer(Modifier.size(28.dp))

            // ---- Support -------------------------------------------------------------
            Text(
                "Chika is free and made with care. If it brings you joy, you can support its making.",
                fontFamily = Libron,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = CreamMuted,
            )
            Spacer(Modifier.size(14.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Crimson)
                    .clickable(onClick = openDonation)
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Ink, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(10.dp))
                Text(
                    "SUPPORT UPSTREAM",
                    fontFamily = Libron, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = Ink,
                )
            }
            Spacer(Modifier.size(6.dp))
            Text(
                "github.com/batunii/chika",
                fontFamily = Libron,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Ochre,
                modifier = Modifier.clickable(onClick = openDonation).padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun LinkRow(text: String, onClick: () -> Unit) {
    Text(
        text,
        fontFamily = Libron,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Ochre,
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 2.dp),
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontFamily = Libron,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        letterSpacing = 2.5.sp,
        color = Ochre,
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(InkSoft)
            .border(1.dp, Cream, RoundedCornerShape(6.dp))
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(start = 16.dp, end = 10.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                title,
                fontFamily = Libron,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Cream,
            )
            Text(
                subtitle,
                fontFamily = Libron,
                fontWeight = FontWeight.Normal,
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                color = CreamMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Text(
            if (checked) "ON" else "OFF",
            fontFamily = Libron,
            fontWeight = FontWeight.Bold,
            color = Cream,
            modifier = Modifier.border(1.dp, Cream).padding(8.dp),
        )
    }
}
