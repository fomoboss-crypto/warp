package com.weatherapp.data.api

import com.weatherapp.data.model.GeocodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface for OpenWeatherMap Geocoding API
 */
interface GeocodeApiService {
    
    @GET("direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<GeocodeResponse>>
}