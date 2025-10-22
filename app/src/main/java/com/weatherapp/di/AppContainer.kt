package com.weatherapp.di

import com.weatherapp.data.api.ApiClient
import com.weatherapp.data.api.WeatherApiService
import com.weatherapp.data.local.CityDataSource
import com.weatherapp.data.repository.WeatherRepositoryImpl
import com.weatherapp.data.service.GeocodeService
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.presentation.viewmodel.WeatherViewModel

/**
 * Application dependency container for manual dependency injection
 */
class AppContainer {
    
    // API Services
    private val weatherApiService: WeatherApiService by lazy {
        ApiClient.weatherApiService
    }
    
    private val geocodeService: GeocodeService by lazy {
        GeocodeService(ApiClient.geocodeApiService)
    }
    
    // Data Sources
    private val cityDataSource: CityDataSource by lazy {
        CityDataSource(geocodeService)
    }
    
    // Repository
    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(weatherApiService)
    }
    
    // ViewModel Factory
    fun createWeatherViewModel(): WeatherViewModel {
        return WeatherViewModel(weatherRepository, cityDataSource)
    }
}