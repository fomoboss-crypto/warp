package com.weatherapp.presentation.viewmodel

import com.weatherapp.data.model.WeatherData

/**
 * Represents the UI state for the weather screen
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null,
    val cityInput: String = "",
    val selectedFullCityName: String? = null, // Stores the full city name from autocomplete selection
    val citySuggestions: List<String> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val hasSearchedCities: Boolean = false
) {
    /**
     * Indicates if there's an error state
     */
    val hasError: Boolean get() = errorMessage != null
    
    /**
     * Indicates if weather data is available
     */
    val hasWeatherData: Boolean get() = weatherData != null
    
    /**
     * Indicates if the screen is in idle state (no loading, no data, no error)
     */
    val isIdle: Boolean get() = !isLoading && weatherData == null && errorMessage == null
}