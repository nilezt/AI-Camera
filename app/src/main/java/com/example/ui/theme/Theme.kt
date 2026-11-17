package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PoseCraftPrecisionLightScheme = lightColorScheme(
    primary = CarbonInk,
    onPrimary = Color.White,
    primaryContainer = StudioInsetHigh,
    onPrimaryContainer = CarbonInk,
    secondary = OpticalAmber,
    onSecondary = Color.White,
    secondaryContainer = OpticalAmberSoft,
    onSecondaryContainer = OpticalAmberInk,
    tertiary = StudioCobalt,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1E3A8A),
    background = StudioCanvas,
    onBackground = CarbonInk,
    surface = StudioPaper,
    onSurface = CarbonInk,
    surfaceVariant = StudioInset,
    onSurfaceVariant = SlateInk,
    surfaceContainerLowest = StudioPaper,
    surfaceContainerLow = StudioCanvas,
    surfaceContainer = StudioInset,
    surfaceContainerHigh = StudioInsetHigh,
    outline = StudioHairline,
    outlineVariant = StudioHairline,
    error = StudioCrimson,
    onError = Color.White,
    errorContainer = StudioCrimsonSoft,
    onErrorContainer = StudioCrimsonInk
)

val PoseCraftShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun PoseCraftTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PoseCraftPrecisionLightScheme,
        typography = PoseCraftTypography,
        shapes = PoseCraftShapes,
        content = content
    )
}
