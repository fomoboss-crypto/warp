package com.weatherapp.presentation.viewmodel

import com.weatherapp.data.model.WeatherData
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.domain.model.Result
import com.weatherapp.data.local.CityDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var cityDataSource: CityDataSource
    private lateinit var viewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleWeatherData = WeatherData(
        cityName = "London",
        fullCityName = "London, GB",
        temperature = 20,
        feelsLike = 22,
        condition = "Clear",
        description = "clear sky",
        iconCode = "01d",
        humidity = 65,
        windSpeed = 3.5
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherRepository = mockk()
        cityDataSource = mockk()
        viewModel = WeatherViewModel(weatherRepository, cityDataSource)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        val initialState = viewModel.uiState.value
        
        assertEquals("", initialState.cityInput)
        assertFalse(initialState.isLoading)
        assertNull(initialState.weatherData)
        assertNull(initialState.errorMessage)
        assertTrue(initialState.isIdle)
        assertFalse(initialState.hasError)
        assertFalse(initialState.hasWeatherData)
    }

    @Test
    fun `updateCityInput should update city input in state`() {
        val cityName = "London"
        
        viewModel.updateCityInput(cityName)
        
        assertEquals(cityName, viewModel.uiState.value.cityInput)
    }

    @Test
    fun `getWeather should update state correctly on success`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherData)
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertEquals(sampleWeatherData, finalState.weatherData)
        assertNull(finalState.errorMessage)
        assertTrue(finalState.hasWeatherData)
        assertFalse(finalState.hasError)
        
        coVerify { weatherRepository.getCurrentWeather(cityName) }
    }

    @Test
    fun `getWeather should handle loading state correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(Result.Loading)
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `getWeather should handle CityNotFoundException correctly`() = runTest {
        val cityName = "InvalidCity"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.CityNotFoundException(cityName))
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNull(finalState.weatherData)
        assertEquals("City not found. Please check the city name and try again.", finalState.errorMessage)
        assertTrue(finalState.hasError)
        assertFalse(finalState.hasWeatherData)
    }

    @Test
    fun `getWeather should handle NetworkException correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.NetworkException())
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals("Network error. Please check your internet connection.", finalState.errorMessage)
        assertTrue(finalState.hasError)
    }

    @Test
    fun `getWeather should handle InvalidApiKeyException correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.InvalidApiKeyException())
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals("Invalid API key. Please contact support.", finalState.errorMessage)
        assertTrue(finalState.hasError)
    }

    @Test
    fun `getWeather should handle TimeoutException correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.TimeoutException())
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals("Request timeout. Please try again.", finalState.errorMessage)
        assertTrue(finalState.hasError)
    }

    @Test
    fun `getWeather should handle ApiException correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.ApiException("Internal Server Error"))
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals("Internal Server Error", finalState.errorMessage)
        assertTrue(finalState.hasError)
    }

    @Test
    fun `getWeather should handle UnknownException correctly`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.UnknownException("Unknown error"))
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals("Unknown error", finalState.errorMessage)
        assertTrue(finalState.hasError)
    }

    @Test
    fun `clearError should clear error message`() = runTest {
        val cityName = "InvalidCity"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Error(WeatherException.CityNotFoundException(cityName))
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        // Verify error is present
        assertTrue(viewModel.uiState.value.hasError)
        
        viewModel.clearError()
        
        // Verify error is cleared
        assertNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.hasError)
    }

    @Test
    fun `clearData should reset all state to initial values`() = runTest {
        val cityName = "London"
        viewModel.updateCityInput(cityName)
        
        coEvery { weatherRepository.getCurrentWeather(cityName) } returns flowOf(
            Result.Success(sampleWeatherData)
        )
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        // Verify state has data
        assertTrue(viewModel.uiState.value.hasWeatherData)
        assertEquals(cityName, viewModel.uiState.value.cityInput)
        
        viewModel.clearData()
        
        // Verify state is reset
        val resetState = viewModel.uiState.value
        assertEquals("", resetState.cityInput)
        assertFalse(resetState.isLoading)
        assertNull(resetState.weatherData)
        assertNull(resetState.errorMessage)
        assertTrue(resetState.isIdle)
    }

    @Test
    fun `getWeather should not make API call when city input is blank`() = runTest {
        viewModel.updateCityInput("")
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        coVerify(exactly = 0) { weatherRepository.getCurrentWeather(any()) }
        assertTrue(viewModel.uiState.value.isIdle)
    }

    @Test
    fun `getWeather should not make API call when city input is only whitespace`() = runTest {
        viewModel.updateCityInput("   ")
        
        viewModel.getWeather()
        advanceUntilIdle()
        
        coVerify(exactly = 0) { weatherRepository.getCurrentWeather(any()) }
        assertTrue(viewModel.uiState.value.isIdle)
    }

    @Test
    fun `selectCitySuggestion should update state and automatically trigger weather search`() = runTest {
        val cityName = "London"
        
        coEvery { weatherRepository.getCurrentWeather(cityName, cityName) } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherData)
        )
        
        viewModel.selectCitySuggestion(cityName)
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals(cityName, finalState.cityInput)
        assertEquals(cityName, finalState.selectedFullCityName)
        assertTrue(finalState.citySuggestions.isEmpty())
        assertNull(finalState.errorMessage)
        assertFalse(finalState.hasSearchedCities)
        assertFalse(finalState.isLoadingSuggestions)
        
        // Verify weather data was fetched automatically
        assertFalse(finalState.isLoading)
        assertEquals(sampleWeatherData, finalState.weatherData)
        assertTrue(finalState.hasWeatherData)
        
        coVerify { weatherRepository.getCurrentWeather(cityName, cityName) }
    }

    @Test
    fun `selectCitySuggestion should handle weather fetch error correctly`() = runTest {
        val cityName = "InvalidCity"
        
        coEvery { weatherRepository.getCurrentWeather(cityName, cityName) } returns flowOf(
            Result.Error(WeatherException.CityNotFoundException(cityName))
        )
        
        viewModel.selectCitySuggestion(cityName)
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals(cityName, finalState.cityInput)
        assertEquals(cityName, finalState.selectedFullCityName)
        assertTrue(finalState.citySuggestions.isEmpty())
        assertFalse(finalState.hasSearchedCities)
        assertFalse(finalState.isLoadingSuggestions)
        
        // Verify error handling
        assertFalse(finalState.isLoading)
        assertNull(finalState.weatherData)
        assertEquals("City not found. Please check the city name and try again.", finalState.errorMessage)
        assertTrue(finalState.hasError)
        
        coVerify { weatherRepository.getCurrentWeather(cityName, cityName) }
    }

    @Test
    fun `selectCitySuggestion should preserve full city name for display`() = runTest {
        val fullCityName = "London, United Kingdom"
        
        coEvery { weatherRepository.getCurrentWeather(fullCityName, fullCityName) } returns flowOf(
            Result.Success(sampleWeatherData.copy(fullCityName = fullCityName))
        )
        
        viewModel.selectCitySuggestion(fullCityName)
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals(fullCityName, finalState.cityInput)
        assertEquals(fullCityName, finalState.selectedFullCityName)
        
        // Verify the full city name is passed to the repository
        coVerify { weatherRepository.getCurrentWeather(fullCityName, fullCityName) }
    }
}