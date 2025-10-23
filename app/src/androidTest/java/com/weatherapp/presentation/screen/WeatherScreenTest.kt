package com.weatherapp.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performClick
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
        
        // Check if idle message is displayed
        composeTestRule.onNodeWithText("Enter a city name to get weather information").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_inputFieldAcceptsText() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Input field should accept text input
        val inputField = composeTestRule.onNodeWithText("Enter city name")
        inputField.performTextInput("London")

        // Verify text was entered
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
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

        // Enter city name and trigger search via IME action
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("London").performImeAction()

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

        // Enter city name and trigger search via IME action
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("London").performImeAction()

        // Check if loading indicator is displayed
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

        // Enter invalid city name and trigger search via IME action
        composeTestRule.onNodeWithText("Enter city name").performTextInput("InvalidCity")
        composeTestRule.onNodeWithText("InvalidCity").performImeAction()

        // Wait for error to be processed
        composeTestRule.waitForIdle()

        // Check if error message is displayed
        composeTestRule.onNodeWithText("City not found").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_inputValidation_handlesEmptyInput() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Input field should be present and ready for input
        composeTestRule.onNodeWithText("Enter city name").assertIsDisplayed()

        // Type something then clear it
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        composeTestRule.onNodeWithText("London").assertIsDisplayed()

        // Clear the input by replacing with empty string
        composeTestRule.onNodeWithText("Enter city name").performTextInput("")
        composeTestRule.onNodeWithText("Enter city name").assertIsDisplayed()
    }

    @Test
    fun weatherScreen_inputValidation_handlesWhitespaceInput() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type only whitespace
        composeTestRule.onNodeWithText("Enter city name").performTextInput("   ")

        // Input field should display the whitespace text
        composeTestRule.onNodeWithText("   ").assertIsDisplayed()
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
        coEvery { mockRepository.getCurrentWeather("London", "London") } returns flowOf(
            Result.Loading,
            Result.Success(sampleWeatherData)
        )

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Type partial city name
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Lon")
        composeTestRule.waitForIdle()

        // Select London suggestion by clicking on it
        composeTestRule.onNodeWithText("London").performClick()

        // Wait for automatic weather search to complete
        composeTestRule.waitForIdle()

        // Verify the input field now contains "London"
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        
        // Verify weather data is automatically displayed after selection
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
        composeTestRule.onNodeWithText("clear sky").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("3.5 m/s").assertIsDisplayed()
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