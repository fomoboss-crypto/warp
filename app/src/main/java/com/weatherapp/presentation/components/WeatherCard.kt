package com.weatherapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.data.model.WeatherData
import com.weatherapp.ui.constants.AppStrings
import com.weatherapp.ui.theme.*

/**
 * Modern weather card component with gradients, shadows, and smooth entrance animation
 */
@Composable
fun WeatherCard(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    // Dynamic gradient based on weather condition
    val gradientColors = getWeatherGradient(weatherData.condition)
    
    Card(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // City name with modern styling
                    Text(
                        text = weatherData.fullCityName,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextOnDark,
                            fontSize = 32.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Weather icon and temperature section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        // Weather icon with enhanced size
                        WeatherIcon(
                            iconCode = weatherData.iconCode,
                            size = 100,
                            modifier = Modifier.padding(end = 24.dp)
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Main temperature with larger, bolder styling
                            Text(
                                text = AppStrings.temperatureCelsius(weatherData.temperature),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextOnDark,
                                    fontSize = 80.sp
                                )
                            )
                            
                            // Feels like temperature
                            Text(
                                text = AppStrings.feelsLike(weatherData.feelsLike),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextOnDark.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    
                    // Weather condition and description
                    Text(
                        text = weatherData.condition,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextOnDark,
                            fontSize = 26.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = weatherData.description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextOnDark.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    // Modern weather details in glass-morphism cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModernWeatherDetailCard(
                            label = "Humidity",
                            value = AppStrings.humidity(weatherData.humidity),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        ModernWeatherDetailCard(
                            label = "Wind Speed",
                            value = AppStrings.windSpeed(weatherData.windSpeed),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

/**
 * Modern glass-morphism style detail card
 */
@Composable
private fun ModernWeatherDetailCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextOnDark.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextOnDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Get gradient colors based on weather condition
 */
private fun getWeatherGradient(condition: String): List<Color> {
    return when (condition.lowercase()) {
        "clear" -> listOf(SunnyGradientStart, SunnyGradientEnd)
        "clouds", "cloudy" -> listOf(CloudyGray, SecondaryBlue)
        "rain", "drizzle", "rainy" -> listOf(RainyGradientStart, RainyGradientEnd)
        "snow", "snowy" -> listOf(SnowyWhite, TertiaryBlue)
        "thunderstorm", "storm" -> listOf(StormyPurple, PrimaryBlueDark)
        "mist", "fog", "haze", "misty" -> listOf(CloudyGray.copy(alpha = 0.8f), SurfaceVariantLight)
        else -> listOf(GradientStart, GradientEnd)
    }
}