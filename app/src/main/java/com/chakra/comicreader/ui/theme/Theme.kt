package com.chakra.comicreader.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Clicks keep their behavior without scheduling ripple frames.
private object StaticIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        object : Modifier.Node(), DrawModifierNode {
            override fun ContentDrawScope.draw() = drawContent()
        }
    override fun equals(other: Any?) = other === this
    override fun hashCode() = javaClass.hashCode()
}

private val KuroColorScheme = lightColorScheme(
    primary = Color.Black, onPrimary = Color.White,
    primaryContainer = Color.White, onPrimaryContainer = Color.Black,
    secondary = Color.Black, onSecondary = Color.White,
    secondaryContainer = Color.White, onSecondaryContainer = Color.Black,
    tertiary = Color.Black, onTertiary = Color.White,
    tertiaryContainer = Color.White, onTertiaryContainer = Color.Black,
    background = Color.White, onBackground = Color.Black,
    surface = Color.White, onSurface = Color.Black,
    surfaceVariant = Color.White, onSurfaceVariant = Color.Black,
    surfaceTint = Color.White,
    surfaceDim = Color.White, surfaceBright = Color.White,
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White,
    surfaceContainer = Color.White, surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    outline = Color.Black, outlineVariant = Color.Black,
    inverseSurface = Color.Black, inverseOnSurface = Color.White, inversePrimary = Color.White,
    error = Color.Black, onError = Color.White,
    errorContainer = Color.White, onErrorContainer = Color.Black,
    scrim = Color.Black,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicReaderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KuroColorScheme,
        typography = KuroTypography,
    ) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides null,
            LocalIndication provides StaticIndication,
            content = content,
        )
    }
}
