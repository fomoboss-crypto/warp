package com.weatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for OpenWeatherMap Geocoding API response
 */
data class GeocodeResponse(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("lat")
    val latitude: Double,
    
    @SerializedName("lon")
    val longitude: Double,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("state")
    val state: String? = null,
    
    @SerializedName("local_names")
    val localNames: Map<String, String>? = null
) {
    /**
     * Returns a formatted display name for the city
     * Format: "City, State, Country" or "City, Country" if no state
     */
    fun getDisplayName(): String {
        return if (state != null) {
            "$name, $state, $country"
        } else {
            "$name, $country"
        }
    }
}