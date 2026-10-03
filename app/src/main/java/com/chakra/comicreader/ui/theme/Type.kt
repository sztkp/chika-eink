package com.chakra.comicreader.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.chakra.comicreader.R

/** Libron v0.25: unmodified desktop TTFs, bundled for offline UI rendering. */
val Libron = FontFamily(
    Font(R.font.libron_regular, FontWeight.Normal),
    Font(R.font.libron_bold, FontWeight.Bold),
    Font(R.font.libron_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.libron_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

/** Use Libron throughout, with the family's actual regular and bold weights. */
val KuroTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        displayMedium = displayMedium.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        displaySmall = displaySmall.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        headlineLarge = headlineLarge.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        titleSmall = titleSmall.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        bodyLarge = bodyLarge.copy(fontFamily = Libron),
        bodyMedium = bodyMedium.copy(fontFamily = Libron),
        bodySmall = bodySmall.copy(fontFamily = Libron),
        labelLarge = labelLarge.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        labelMedium = labelMedium.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
        labelSmall = labelSmall.copy(fontFamily = Libron, fontWeight = FontWeight.Bold),
    )
}
