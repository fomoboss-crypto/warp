package com.weatherapp.data.api

import com.weatherapp.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * API client configuration for Retrofit
 */
object ApiClient {
    
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val GEOCODE_BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB cache
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    private fun createCache(): Cache? {
        return try {
            val cacheDir = File(System.getProperty("java.io.tmpdir"), "weather_cache")
            Cache(cacheDir, CACHE_SIZE)
        } catch (e: Exception) {
            null // Fallback to no cache if creation fails
        }
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .apply {
            // Only add logging interceptor in debug builds
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .cache(createCache())
        .build()
    
    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val geocodeRetrofit = Retrofit.Builder()
        .baseUrl(GEOCODE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val weatherApiService: WeatherApiService = weatherRetrofit.create(WeatherApiService::class.java)
    val geocodeApiService: GeocodeApiService = geocodeRetrofit.create(GeocodeApiService::class.java)
}