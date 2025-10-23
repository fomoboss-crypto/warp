package com.weatherapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnDark,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = TextOnDark,
    secondary = SecondaryBlue,
    onSecondary = TextOnDark,
    secondaryContainer = TertiaryBlue,
    onSecondaryContainer = TextPrimary,
    tertiary = GradientStart,
    onTertiary = TextOnDark,
    background = SurfaceDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextOnDark,
    error = ErrorRed,
    onError = TextOnDark,
    outline = TextSecondary,
    outlineVariant = DarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnDark,
    primaryContainer = TertiaryBlue,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryBlue,
    onSecondary = TextOnDark,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = TextPrimary,
    tertiary = GradientStart,
    onTertiary = TextOnDark,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextPrimary,
    error = ErrorRed,
    onError = TextOnDark,
    outline = TextSecondary,
    outlineVariant = CloudyGray
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to maintain custom branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}