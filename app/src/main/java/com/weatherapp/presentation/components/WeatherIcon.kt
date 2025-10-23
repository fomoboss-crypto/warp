package com.weatherapp.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.weatherapp.R
import com.weatherapp.ui.theme.*

/**
 * Modern weather icon component with dynamic coloring
 */
@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier,
    size: Int = 64
) {
    val iconRes = when (iconCode.take(2)) {
        "01" -> R.drawable.ic_weather_sunny // Clear sky
        "02" -> R.drawable.ic_weather_partly_cloudy // Few clouds
        "03", "04" -> R.drawable.ic_weather_cloudy // Scattered/broken clouds
        "09", "10" -> R.drawable.ic_weather_rainy // Rain
        "11" -> R.drawable.ic_weather_thunderstorm // Thunderstorm
        "13" -> R.drawable.ic_weather_snowy // Snow
        "50" -> R.drawable.ic_weather_misty // Mist/fog
        else -> R.drawable.ic_weather_default // Default icon
    }
    
    // Dynamic icon color based on weather condition
    val iconColor = getWeatherIconColor(iconCode)
    
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = "Weather icon for $iconCode",
        modifier = modifier.size(size.dp),
        tint = iconColor
    )
}

/**
 * Get dynamic color for weather icons
 */
private fun getWeatherIconColor(iconCode: String): Color {
    return when (iconCode.take(2)) {
        "01" -> SunnyYellow // Clear sky - bright yellow
        "02" -> SunnyYellow.copy(alpha = 0.8f) // Few clouds - slightly muted yellow
        "03", "04" -> TextOnDark.copy(alpha = 0.9f) // Clouds - white/light gray
        "09", "10" -> RainyBlue // Rain - blue
        "11" -> StormyPurple // Thunderstorm - purple
        "13" -> SnowyWhite // Snow - white
        "50" -> CloudyGray // Mist/fog - gray
        else -> TextOnDark // Default - white
    }
}