package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBluePrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = DarkMintSecondary,
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = DarkAmberTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    secondary = MintSecondary,
    onSecondary = Color.White,
    secondaryContainer = MintSecondaryContainer,
    onSecondaryContainer = MintOnSecondaryContainer,
    tertiary = AmberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = AmberOnTertiaryContainer,
    background = SensoryBackground,
    surface = SensorySurface,
    surfaceVariant = SensorySurfaceVariant,
    onBackground = SensoryOnSurface,
    onSurface = SensoryOnSurface,
    onSurfaceVariant = SensoryOnSurfaceVariant
)

private val CalmLightColorScheme = lightColorScheme(
    primary = CalmSoftBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = CalmSoftSage,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFDF5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = CalmSoftPeach,
    background = CalmBackground,
    surface = CalmSurface,
    surfaceVariant = Color(0xFFF4F4F1),
    onBackground = Color(0xFF1C1917),
    onSurface = Color(0xFF1C1917),
    onSurfaceVariant = Color(0xFF57534E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isCalmMode: Boolean = false,
    dynamicColor: Boolean = false, // Set false to prioritize sensory-friendly curated colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        isCalmMode -> CalmLightColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
