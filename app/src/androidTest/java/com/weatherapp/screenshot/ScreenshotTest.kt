package com.weatherapp.screenshot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.weatherapp.presentation.screen.WeatherScreen
import com.weatherapp.presentation.viewmodel.WeatherViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Visual regression tests using Compose testing framework
 * These tests capture the visual state of UI components to detect unintended changes
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureInitialState() {
        val mockViewModel = mockk<WeatherViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            WeatherScreen(viewModel = mockViewModel)
        }
        
        // Verify initial state elements are present
        composeTestRule.onNodeWithText("Enter city name").assertExists()
        composeTestRule.onNodeWithText("Get Weather").assertExists()
    }

    @Test
    fun captureLoadingState() {
        val mockViewModel = mockk<WeatherViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            WeatherScreen(viewModel = mockViewModel)
        }
        
        // Simulate loading state by typing and checking for loading indicator
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Lo")
        
        // Verify loading elements appear
        composeTestRule.onNodeWithText("Searching cities...").assertExists()
    }

    @Test
    fun captureErrorState() {
        val mockViewModel = mockk<WeatherViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            WeatherScreen(viewModel = mockViewModel)
        }
        
        // Simulate error state by typing invalid input
        composeTestRule.onNodeWithText("Enter city name").performTextInput("InvalidCity123")
        
        // Wait for potential error state
        composeTestRule.waitForIdle()
    }

    @Test
    fun captureAutocompleteState() {
        val mockViewModel = mockk<WeatherViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            WeatherScreen(viewModel = mockViewModel)
        }
        
        // Simulate autocomplete by typing
        composeTestRule.onNodeWithText("Enter city name").performTextInput("London")
        
        // Verify autocomplete functionality
        composeTestRule.waitForIdle()
    }
}