package com.weatherapp

import android.app.Application
import com.weatherapp.di.AppContainer

/**
 * Application class for the Weather App
 */
class WeatherApplication : Application() {
    
    // AppContainer instance used by the rest of classes to obtain dependencies
    val appContainer = AppContainer()
}