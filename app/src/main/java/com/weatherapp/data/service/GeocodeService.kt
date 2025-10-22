package com.weatherapp.data.service

import com.weatherapp.BuildConfig
import com.weatherapp.data.api.GeocodeApiService
import com.weatherapp.data.model.GeocodeResponse
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.model.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Service for geocoding operations using OpenWeatherMap Geocoding API
 */
class GeocodeService(
    private val geocodeApiService: GeocodeApiService
) {
    
    /**
     * Search for cities using the geocoding API
     * @param query The city name to search for
     * @param limit Maximum number of results to return (default: 5)
     * @return Result containing list of GeocodeResponse objects
     */
    suspend fun searchCities(query: String, limit: Int = 5): Result<List<GeocodeResponse>> {
        return try {
            if (query.isBlank()) {
                return Result.Success(emptyList())
            }
            
            val response = geocodeApiService.searchCities(
                query = query,
                limit = limit,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            
            if (response.isSuccessful) {
                val cities = response.body() ?: emptyList()
                Result.Success(cities)
            } else {
                Result.Error(mapHttpException(response.code()))
            }
        } catch (exception: Exception) {
            Result.Error(mapException(exception))
        }
    }
    
    /**
     * Maps HTTP response codes to specific WeatherExceptions
     */
    private fun mapHttpException(code: Int): WeatherException {
        return when (code) {
            401 -> WeatherException.InvalidApiKeyException()
            404 -> WeatherException.CityNotFoundException("No cities found")
            429 -> WeatherException.ApiException("Rate limit exceeded", null)
            else -> WeatherException.ApiException("HTTP error: $code", null)
        }
    }
    
    /**
     * Maps generic exceptions to specific WeatherExceptions
     */
    private fun mapException(exception: Exception): WeatherException {
        return when (exception) {
            is HttpException -> mapHttpException(exception.code())
            is UnknownHostException -> WeatherException.NetworkException("No internet connection", exception)
            is SocketTimeoutException -> WeatherException.TimeoutException()
            is IOException -> WeatherException.NetworkException("Network error", exception)
            else -> WeatherException.UnknownException("Unexpected error: ${exception.message}", exception)
        }
    }
}