package com.weatherapp.domain.exception

/**
 * Base exception class for weather-related errors
 */
sealed class WeatherException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * Exception thrown when the city is not found
     */
    class CityNotFoundException(cityName: String) : WeatherException("City '$cityName' not found")
    
    /**
     * Exception thrown when there's a network connectivity issue
     */
    class NetworkException(message: String = "Network error occurred", cause: Throwable? = null) : 
        WeatherException(message, cause)
    
    /**
     * Exception thrown when the API returns an unexpected response
     */
    class ApiException(message: String, cause: Throwable? = null) : WeatherException(message, cause)
    
    /**
     * Exception thrown when there's an unknown error
     */
    class UnknownException(message: String = "An unknown error occurred", cause: Throwable? = null) : 
        WeatherException(message, cause)
    
    /**
     * Exception thrown when the API key is invalid or missing
     */
    class InvalidApiKeyException : WeatherException("Invalid API key")
    
    /**
     * Exception thrown when the request times out
     */
    class TimeoutException : WeatherException("Request timed out")
}