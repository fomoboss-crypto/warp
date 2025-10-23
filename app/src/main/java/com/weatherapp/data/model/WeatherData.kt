package com.weatherapp.data.model

/**
 * Simplified weather data model for UI consumption
 */
data class WeatherData(
    val cityName: String,
    val fullCityName: String, // City name with country (e.g., "London, GB")
    val temperature: Int, // in Celsius
    val feelsLike: Int, // in Celsius
    val condition: String, // e.g., "Sunny", "Cloudy", "Rainy"
    val description: String, // detailed description
    val humidity: Int, // percentage
    val windSpeed: Double, // m/s
    val iconCode: String // for weather icon
)

/**
 * Extension function to convert WeatherResponse to WeatherData
 */
fun WeatherResponse.toWeatherData(fullCityNameOverride: String? = null): WeatherData {
    return WeatherData(
        cityName = name,
        fullCityName = fullCityNameOverride ?: "$name, ${sys.country}",
        temperature = main.temperature.toInt(), // Already in Celsius from API
        feelsLike = main.feelsLike.toInt(), // Already in Celsius from API
        condition = weather.firstOrNull()?.main ?: "Unknown",
        description = weather.firstOrNull()?.description?.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        } ?: "No description available",
        humidity = main.humidity,
        windSpeed = wind.speed,
        iconCode = weather.firstOrNull()?.icon ?: "01d"
    )
}