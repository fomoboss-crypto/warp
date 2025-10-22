package com.weatherapp.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertCountEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.weatherapp.data.model.WeatherData
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.domain.model.Result
import com.weatherapp.presentation.viewmodel.WeatherViewModel
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.data.local.CityDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockCityDataSource: CityDataSource
    private lateinit var viewModel: WeatherViewModel

    private val sampleWeatherData = WeatherData(
        cityName = "London",
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
        viewModel = WeatherViewModel(mockRepository, mockCityDataSource)
    }

    @Test
    fun weatherScreen_initialState_displaysCorrectly() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Check if app title is displayed
        composeTestRule.onNodeWithText("WeatherApp").assertIsDisplayed()
        
        // Check if city input field is displayed
        composeTestRule.onNodeWithText("Enter city name").assertIsDisplayed()
        
        // Check if search button is displayed but disabled (empty input)
        composeTestRule.onNodeWithText("Get Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Weather").assertIsNotEnabled()
        
        // Check if idle message is displayed
        composeTestRule.onNodeWithText("Enter a city name to get weather information").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_cityInputUpdatesButton() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Initially button should be disabled
        composeTestRule.onNodeWithText("Get Weather").assertIsNotEnabled()

        // Type in city name
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")

        // Button should now be enabled
        composeTestRule.onNodeWithText("Get Weather").assertIsEnabled()
    }

    @Test
    fun weatherScreen_successfulWeatherFetch_displaysWeatherData() {
        coEvery { mockRepository.getCurrentWeather("London") } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherData)
        )

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Enter city name and click search
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Wait for the weather data to be displayed
        composeTestRule.waitForIdle()

        // Check if weather data is displayed
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
        composeTestRule.onNodeWithText("clear sky").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("3.5 m/s").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_loadingState_displaysLoadingIndicator() {
        coEvery { mockRepository.getCurrentWeather("London") } returns flowOf(Result.Loading)

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Enter city name and click search
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Check if loading indicator is displayed
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
        
        // Check if button shows loading state
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_errorState_displaysErrorMessage() {
        coEvery { mockRepository.getCurrentWeather("InvalidCity") } returns flowOf(
            Result.Error(WeatherException.CityNotFoundException("City not found"))
        )

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Enter invalid city name and click search
        composeTestRule.onNodeWithText("Enter city name").performTextInput("InvalidCity")
        composeTestRule.onNodeWithText("Get Weather").performClick()

        // Wait for error to be processed
        composeTestRule.waitForIdle()

        // Check if error message is displayed
        composeTestRule.onNodeWithText("City not found").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_emptyInput_buttonDisabled() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Button should be disabled with empty input
        composeTestRule.onNodeWithText("Get Weather").assertIsNotEnabled()

        // Type something then clear it
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("Get Weather").assertIsEnabled()

        // Clear the input
        composeTestRule.onNodeWithText("Enter city name").performTextInput("")
        composeTestRule.onNodeWithText("Get Weather").assertIsNotEnabled()
    }

    @Test
    fun weatherScreen_whitespaceInput_buttonDisabled() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type only whitespace
        composeTestRule.onNodeWithText("Enter city name").performTextInput("   ")

        // Button should still be disabled
        composeTestRule.onNodeWithText("Get Weather").assertIsNotEnabled()
    }

    @Test
    fun weatherScreen_autocompleteShowsSuggestions() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type partial city name to trigger autocomplete
        composeTestRule.onNodeWithText("Enter city name").performTextInput("New")

        // Wait for suggestions to appear and verify some expected cities
        composeTestRule.waitForIdle()
        
        // Check that New York appears in suggestions
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_autocompleteSuggestionSelection() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type partial city name
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Lon")
        composeTestRule.waitForIdle()

        // Click on London suggestion
        composeTestRule.onNodeWithText("London").performClick()

        // Verify the input field now contains "London"
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        
        // Verify button is now enabled
        composeTestRule.onNodeWithText("Get Weather").assertIsEnabled()
    }

    @Test
    fun weatherScreen_autocompleteFiltersCorrectly() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type specific prefix
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Par")
        composeTestRule.waitForIdle()

        // Should show Paris
        composeTestRule.onNodeWithText("Paris").assertIsDisplayed()
        
        // Should not show unrelated cities like London
        composeTestRule.onAllNodesWithText("London").assertCountEquals(0)
    }
}