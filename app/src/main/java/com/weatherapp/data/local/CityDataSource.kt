package com.weatherapp.data.local

import com.weatherapp.data.service.GeocodeService
import com.weatherapp.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data source for city suggestions used in autocomplete functionality
 * Now uses OpenWeatherMap Geocoding API for dynamic city search
 */
class CityDataSource(
    private val geocodeService: GeocodeService
) {
    
    /**
     * Search for cities using the geocoding API
     * @param query The search query (city name)
     * @param maxResults Maximum number of results to return
     * @return List of city display names
     */
    suspend fun searchCities(query: String, maxResults: Int = 10): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (query.isBlank()) {
                    return@withContext emptyList()
                }
                
                when (val result = geocodeService.searchCities(query, limit = maxResults)) {
                    is Result.Success -> {
                        val displayNames = result.data.map { geocodeResponse ->
                            val displayName = if (geocodeResponse.state != null) {
                                "${geocodeResponse.name}, ${geocodeResponse.state}, ${geocodeResponse.country}"
                            } else {
                                "${geocodeResponse.name}, ${geocodeResponse.country}"
                            }
                            displayName
                        }
                        displayNames
                    }
                    is Result.Error -> {
                        emptyList()
                    }
                    is Result.Loading -> {
                        emptyList()
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}