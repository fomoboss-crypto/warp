package com.weatherapp.data.repository

import com.weatherapp.BuildConfig
import com.weatherapp.data.api.WeatherApiService
import com.weatherapp.data.model.WeatherData
import com.weatherapp.data.model.toWeatherData
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.model.Result
import com.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Implementation of WeatherRepository that handles API calls and error mapping
 */
class WeatherRepositoryImpl(
    private val apiService: WeatherApiService
) : WeatherRepository {
    
    private val apiKey = BuildConfig.WEATHER_API_KEY
    
    override suspend fun getCurrentWeather(cityName: String, fullCityNameOverride: String?): Flow<Result<WeatherData>> = flow {
        emit(Result.Loading)
        
        try {
            val response = apiService.getCurrentWeather(
                cityName = cityName.trim(),
                apiKey = apiKey
            )
            
            val result = processWeatherResponse(response, cityName, fullCityNameOverride)
            emit(result)
        } catch (exception: Exception) {
            val weatherException = mapException(exception)
            emit(Result.Error(weatherException))
        }
    }
    
    override suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Flow<Result<WeatherData>> = flow {
        emit(Result.Loading)
        
        try {
            val response = apiService.getCurrentWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            
            val locationName = "Location at ($latitude, $longitude)"
            val result = processWeatherResponse(response, locationName)
            emit(result)
        } catch (exception: Exception) {
            val weatherException = mapException(exception)
            emit(Result.Error(weatherException))
        }
    }
    
    /**
     * Processes the weather API response and handles common error cases
     */
    private fun processWeatherResponse(
        response: retrofit2.Response<com.weatherapp.data.model.WeatherResponse>,
        locationIdentifier: String,
        fullCityNameOverride: String? = null
    ): Result<WeatherData> {
        return if (response.isSuccessful) {
            response.body()?.let { weatherResponse ->
                val weatherData = weatherResponse.toWeatherData(fullCityNameOverride)
                Result.Success(weatherData)
            } ?: Result.Error(WeatherException.ApiException("Empty response body"))
        } else {
            val error = when (response.code()) {
                404 -> WeatherException.CityNotFoundException(locationIdentifier)
                401 -> WeatherException.InvalidApiKeyException()
                else -> WeatherException.ApiException("API error: ${response.code()} ${response.message()}")
            }
            Result.Error(error)
        }
    }
    
    /**
     * Maps generic exceptions to specific WeatherExceptions
     */
    private fun mapException(exception: Exception): WeatherException {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    404 -> WeatherException.CityNotFoundException("City")
                    401 -> WeatherException.InvalidApiKeyException()
                    else -> WeatherException.ApiException("HTTP error: ${exception.code()}", exception)
                }
            }
            is UnknownHostException -> WeatherException.NetworkException("No internet connection", exception)
            is SocketTimeoutException -> WeatherException.TimeoutException()
            is IOException -> WeatherException.NetworkException("Network error", exception)
            else -> WeatherException.UnknownException("Unexpected error: ${exception.message}", exception)
        }
    }
}