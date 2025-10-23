package com.weatherapp.accessibility

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertHasClickAction
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
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessibilityTest {

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
        
        coEvery { mockCityDataSource.searchCities(any()) } returns listOf("London", "Los Angeles")
        coEvery { mockRepository.getCurrentWeather(any()) } returns flowOf(
            Result.Success(sampleWeatherData)
        )
        
        viewModel = WeatherViewModel(mockRepository, mockCityDataSource)
    }

    @Test
    fun weatherScreen_hasProperContentDescriptions() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Check that input field has content description
        composeTestRule.onNodeWithContentDescription("City input field").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_inputFieldHasProperSemantics() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Input field should be accessible
        val inputField = composeTestRule.onNodeWithText("Enter city name")
        inputField.assertIsDisplayed()
        
        // Should be able to input text
        inputField.performTextInput("London")
    }

    @Test
    fun weatherScreen_inputFieldSupportsKeyboardActions() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Input field should be accessible and support keyboard actions
        val inputField = composeTestRule.onNodeWithText("Enter city name")
        inputField.assertIsDisplayed()
        
        // Should be able to input text and trigger search via IME action
        inputField.performTextInput("London")
        inputField.performImeAction()
    }

    @Test
    fun weatherScreen_loadingStateHasProperAnnouncement() {
        // Mock loading state
        coEvery { mockRepository.getCurrentWeather(any()) } returns flowOf(
            Result.Loading
        )
        
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Trigger loading state
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("London").performImeAction()
        
        // Loading indicator should be accessible
        composeTestRule.onNodeWithContentDescription("Loading weather data").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_errorStateHasProperAnnouncement() {
        // Mock error state
        coEvery { mockRepository.getCurrentWeather(any()) } returns flowOf(
            Result.Error(Exception("Network error"))
        )
        
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Trigger error state
        composeTestRule.onNodeWithText("Enter city name").performTextInput("InvalidCity")
        composeTestRule.onNodeWithText("InvalidCity").performImeAction()
        
        // Error message should be accessible
        composeTestRule.onNode(hasText("Network error") or hasContentDescription("Error message"))
            .assertIsDisplayed()
    }

    @Test
    fun autocomplete_suggestionsAreAccessible() {
        coEvery { mockRepository.getCurrentWeather("London", "London") } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherData)
        )

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type to show suggestions
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Lo")
        
        // Wait for suggestions to appear and verify they're accessible
        composeTestRule.waitForIdle()
        
        // Each suggestion should be clickable and have proper semantics
        composeTestRule.onNodeWithText("London").assertHasClickAction()
        composeTestRule.onNodeWithText("Los Angeles").assertHasClickAction()
        
        // Click on London suggestion to test automatic weather search
        composeTestRule.onNodeWithText("London").performClick()
        
        // Wait for automatic weather search to complete
        composeTestRule.waitForIdle()
        
        // Verify weather data is automatically displayed and accessible
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
    }

    @Test
    fun weatherCard_hasProperSemanticStructure() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Get weather data
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("London").performImeAction()
        
        composeTestRule.waitForIdle()
        
        // Weather information should be properly structured for screen readers
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
        
        // Additional weather details should be accessible
        composeTestRule.onNodeWithText("Feels like 22°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity: 65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wind: 3.5 m/s").assertIsDisplayed()
    }
}