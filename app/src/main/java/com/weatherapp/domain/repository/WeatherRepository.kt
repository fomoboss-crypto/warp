package com.weatherapp.domain.repository

import com.weatherapp.data.model.WeatherData
import com.weatherapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weather data operations
 */
interface WeatherRepository {
    
    /**
     * Get current weather data for a given city name
     * @param cityName The name of the city
     * @param fullCityNameOverride Optional full city name to display instead of API response name
     * @return Flow of Result containing WeatherData or error
     */
    suspend fun getCurrentWeather(cityName: String, fullCityNameOverride: String? = null): Flow<Result<WeatherData>>
    
    /**
     * Get current weather data for given coordinates
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return Flow of Result containing WeatherData or error
     */
    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double, 
        longitude: Double
    ): Flow<Result<WeatherData>>
}