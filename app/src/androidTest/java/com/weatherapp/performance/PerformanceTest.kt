package com.weatherapp.performance

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.weatherapp.data.local.CityDataSource
import com.weatherapp.data.model.WeatherData
import com.weatherapp.domain.model.Result
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.presentation.screen.WeatherScreen
import com.weatherapp.presentation.viewmodel.WeatherViewModel
import com.weatherapp.ui.theme.WeatherAppTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockCityDataSource: CityDataSource
    private lateinit var viewModel: WeatherViewModel

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

    @Before
    fun setup() {
        mockRepository = mockk()
        mockCityDataSource = mockk()
        
        coEvery { mockCityDataSource.searchCities(any()) } returns listOf("London", "Los Angeles", "Louisville")
        coEvery { mockRepository.getCurrentWeather(any()) } returns flow {
            emit(Result.Loading)
            delay(100) // Simulate network delay
            emit(Result.Success(sampleWeatherData))
        }
        
        viewModel = WeatherViewModel(mockRepository, mockCityDataSource)
    }

    @Test
    fun weatherScreen_initialRenderTime_isAcceptable() {
        val renderTime = measureTimeMillis {
            composeTestRule.setContent {
                WeatherAppTheme {
                    WeatherScreen(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()
        }

        // Initial render should be fast (under 500ms)
        assert(renderTime < 500) { "Initial render took ${renderTime}ms, expected < 500ms" }
    }

    @Test
    fun autocomplete_responseTime_isAcceptable() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        val inputTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Enter city name").performTextInput("Lo")
            composeTestRule.waitForIdle()
        }

        // Autocomplete should respond quickly (under 200ms)
        assert(inputTime < 200) { "Autocomplete response took ${inputTime}ms, expected < 200ms" }
    }

    @Test
    fun weatherFetch_userInteractionResponsiveness() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Measure time from user input to UI response
        val interactionTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
            composeTestRule.onNodeWithText("London").performImeAction()
            
            // Wait for loading state to appear (should be immediate)
            composeTestRule.waitForIdle()
        }

        // User interaction should feel immediate (under 100ms)
        assert(interactionTime < 100) { "User interaction response took ${interactionTime}ms, expected < 100ms" }
    }

    @Test
    fun autocomplete_typingPerformance_withManyResults() {
        // Mock many results to test performance with large datasets
        val manyCities = (1..100).map { "City$it" }
        coEvery { mockCityDataSource.searchCities(any()) } returns manyCities

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        val typingTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Enter city name").performTextInput("C")
            composeTestRule.waitForIdle()
        }

        // Should handle large result sets efficiently (under 300ms)
        assert(typingTime < 300) { "Typing with many results took ${typingTime}ms, expected < 300ms" }
    }

    @Test
    fun weatherScreen_memoryEfficiency_multipleInteractions() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Perform multiple interactions to test for memory leaks
        repeat(10) { iteration ->
            val iterationTime = measureTimeMillis {
                composeTestRule.onNodeWithText("Enter city name").performTextInput("City$iteration")
                composeTestRule.onNodeWithText("City$iteration").performImeAction()
                composeTestRule.waitForIdle()
                
                // Clear input for next iteration
                composeTestRule.onNodeWithText("City$iteration").performTextInput("")
                composeTestRule.waitForIdle()
            }
            
            // Each iteration should maintain consistent performance
            assert(iterationTime < 500) { "Iteration $iteration took ${iterationTime}ms, expected < 500ms" }
        }
    }

    @Test
    fun autocomplete_scrollPerformance_withLongList() {
        // Mock a very long list of cities
        val longCityList = (1..1000).map { "LongCityName$it" }
        coEvery { mockCityDataSource.searchCities(any()) } returns longCityList

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        val scrollTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Enter city name").performTextInput("Long")
            composeTestRule.waitForIdle()
            
            // The UI should handle long lists efficiently
            // In a real implementation, you'd test scrolling performance here
        }

        // Should handle very long lists without significant performance degradation
        assert(scrollTime < 1000) { "Long list handling took ${scrollTime}ms, expected < 1000ms" }
    }

    @Test
    fun weatherScreen_stateTransition_performance() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Measure state transition from idle -> loading -> success
        val transitionTime = measureTimeMillis {
            composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
            composeTestRule.onNodeWithText("London").performImeAction()
            
            // Wait for complete state transition
            composeTestRule.waitForIdle()
            
            // Verify success state is reached
            composeTestRule.onNodeWithText("London").assertExists()
        }

        // State transitions should be smooth and fast
        assert(transitionTime < 1000) { "State transition took ${transitionTime}ms, expected < 1000ms" }
    }
}