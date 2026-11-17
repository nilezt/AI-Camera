package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PoseCraftDarkColorScheme = darkColorScheme(
    primary = AmberShutter,
    onPrimary = Color(0xFF1C1300),
    primaryContainer = AmberGlowContainer,
    onPrimaryContainer = AmberGlowOnContainer,
    secondary = SageFilm,
    onSecondary = Color(0xFF002117),
    secondaryContainer = SageFilmContainer,
    onSecondaryContainer = Color(0xFFB5F5DC),
    tertiary = Color(0xFF9FB8FF),
    onTertiary = Color(0xFF0A1D4D),
    background = StudioObsidian,
    onBackground = IvoryText,
    surface = StudioCharcoal,
    onSurface = IvoryText,
    surfaceVariant = StudioSlate,
    onSurfaceVariant = MutedSlateText,
    surfaceContainerLowest = StudioObsidian,
    surfaceContainerLow = StudioCharcoal,
    surfaceContainer = StudioSlate,
    surfaceContainerHigh = StudioElevated,
    outline = HairlineOutline,
    outlineVariant = Color(0xFF282D38),
    error = CoralAlert,
    onError = Color(0xFF2C0004),
    errorContainer = CoralAlertContainer,
    onErrorContainer = Color(0xFFFFDADA)
)

private val PoseCraftLightColorScheme = lightColorScheme(
    primary = AmberLightPrimary,
    onPrimary = Color.White,
    primaryContainer = AmberLightContainer,
    onPrimaryContainer = AmberLightOnContainer,
    secondary = Color(0xFF147D5C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFF6E6),
    onSecondaryContainer = Color(0xFF002117),
    background = PaperCream,
    onBackground = InkCharcoal,
    surface = PaperCard,
    onSurface = InkCharcoal,
    surfaceVariant = PaperElevated,
    onSurfaceVariant = InkMuted,
    surfaceContainerLowest = PaperCard,
    surfaceContainerLow = PaperCream,
    surfaceContainer = PaperElevated,
    surfaceContainerHigh = Color(0xFFE5DFD3),
    outline = PaperOutline,
    outlineVariant = Color(0xFFE6E0D5),
    error = Color(0xFFC62828),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val PoseCraftShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun PoseCraftTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PoseCraftDarkColorScheme else PoseCraftLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoseCraftTypography,
        shapes = PoseCraftShapes,
        content = content
    )
}
