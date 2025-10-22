package com.weatherapp.ui.constants

/**
 * String constants for the Weather App
 * Replaces XML string resources for 100% Kotlin & Jetpack Compose compliance
 */
object AppStrings {
    const val ENTER_CITY_NAME = "Enter city name"
    const val GET_WEATHER = "Get Weather"
    const val LOADING = "Loading..."
    const val ERROR_INVALID_CITY = "City not found. Please check the city name and try again."
    const val ERROR_NETWORK = "Network error. Please check your internet connection."
    const val ERROR_GENERAL = "Something went wrong. Please try again."
    
    fun temperatureCelsius(temp: Int): String = "${temp}°C"
    fun feelsLike(temp: Int): String = "Feels like ${temp}°C"
    fun humidity(humidity: Int): String = "Humidity: ${humidity}%"
    fun windSpeed(speed: Double): String = "Wind: ${String.format("%.1f", speed)} m/s"
}