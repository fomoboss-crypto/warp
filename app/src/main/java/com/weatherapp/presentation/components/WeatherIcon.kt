package com.weatherapp.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.weatherapp.R

/**
 * Weather icon component that displays SVG icons based on weather conditions
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
    
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = "Weather icon for $iconCode",
        modifier = modifier.size(size.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}