package com.weatherapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.data.local.CityDataSource
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.model.Result
import com.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for managing weather data and UI state
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val cityDataSource: CityDataSource
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private var citySearchJob: Job? = null
    
    /**
     * Updates the city input and triggers autocomplete suggestions
     */
    fun updateCityInput(input: String) {
        _uiState.value = _uiState.value.copy(cityInput = input)
        
        // Cancel previous search job
        citySearchJob?.cancel()
        
        // Start new search with debounce
        if (input.isNotBlank() && input.length >= 2) {
            // Show loading immediately when user starts typing
            _uiState.value = _uiState.value.copy(isLoadingSuggestions = true)
            
            citySearchJob = viewModelScope.launch {
                delay(300) // Debounce delay
                searchCities(input)
            }
        } else {
            _uiState.value = _uiState.value.copy(
                citySuggestions = emptyList(),
                hasSearchedCities = false,
                isLoadingSuggestions = false // Reset loading state when input is too short
            )
        }
    }

    private suspend fun searchCities(input: String) {
        // Loading state is already set in updateCityInput, so we don't need to set it again here
        
        try {
            // Add a minimum loading time to show the loader before results
            delay(500) // Show loading for at least 500ms
            
            val suggestions = cityDataSource.searchCities(input, maxResults = 10)
            _uiState.value = _uiState.value.copy(
                citySuggestions = suggestions,
                isLoadingSuggestions = false,
                hasSearchedCities = true
            )
        } catch (e: Exception) {
            // Add delay before showing "no cities found" to ensure user sees the loading state
            delay(300)
            _uiState.value = _uiState.value.copy(
                citySuggestions = emptyList(),
                isLoadingSuggestions = false,
                hasSearchedCities = true
            )
        }
    }
    
    /**
     * Fetches weather data for the current city input
     */
    fun getWeather() {
        val cityName = _uiState.value.cityInput.trim()
        if (cityName.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a city name"
            )
            return
        }
        
        getWeatherForCity(cityName)
    }
    
    /**
     * Fetches weather data for a specific city
     */
    fun getWeatherForCity(cityName: String) {
        val fullCityName = _uiState.value.selectedFullCityName
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(cityName, fullCityName)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${exception.message}"
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                errorMessage = null,
                                weatherData = null
                            )
                        }
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                weatherData = result.data,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> {
                            val errorMessage = when (result.exception) {
                                is WeatherException.CityNotFoundException -> 
                                    "City not found. Please check the city name and try again."
                                is WeatherException.NetworkException -> 
                                    "Network error. Please check your internet connection."
                                is WeatherException.InvalidApiKeyException -> 
                                    "API configuration error. Please contact support."
                                is WeatherException.TimeoutException -> 
                                    "Request timed out. Please try again."
                                else -> "Something went wrong. Please try again."
                            }
                            
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = errorMessage,
                                weatherData = null
                            )
                        }
                    }
                }
        }
    }
    
    /**
     * Selects a city from autocomplete suggestions and automatically fetches weather data
     */
    fun selectCitySuggestion(cityName: String) {
        _uiState.value = _uiState.value.copy(
            cityInput = cityName,
            selectedFullCityName = cityName, // Preserve the full city name for display
            citySuggestions = emptyList(), // Clear suggestions after selection
            errorMessage = null,
            hasSearchedCities = false, // Reset search state after selection
            isLoadingSuggestions = false // Reset loading state after selection
        )
        
        // Automatically trigger weather search for the selected city
        getWeatherForCity(cityName)
    }
    
    /**
     * Clears autocomplete suggestions
     */
    fun clearSuggestions() {
        _uiState.value = _uiState.value.copy(
            citySuggestions = emptyList()
        )
    }
    
    /**
     * Clears the current error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clears all data and resets to initial state
     */
    fun clearData() {
        _uiState.value = WeatherUiState()
    }
}