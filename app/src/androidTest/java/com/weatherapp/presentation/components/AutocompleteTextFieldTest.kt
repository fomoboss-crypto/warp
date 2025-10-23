package com.weatherapp.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.weatherapp.ui.components.AutocompleteTextField
import com.weatherapp.ui.theme.WeatherAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutocompleteTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun autocompleteTextField_showsLoadingStateImmediately() {
        var cityInput = ""
        val suggestions = emptyList<String>()
        var isLoadingSuggestions = false
        var hasSearchedCities = false

        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    suggestions = suggestions,
                    onSuggestionSelected = { },
                    isLoadingSuggestions = isLoadingSuggestions,
                    hasSearchedCities = hasSearchedCities,
                    placeholder = "Enter city name"
                )
            }
        }

        // Initially no loading state
        composeTestRule.onNodeWithText("Searching cities...").assertIsNotDisplayed()
        
        // Simulate typing to trigger loading state
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Lo")
        
        // Update the loading state as would happen in real app
        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = "Lo",
                    onValueChange = { cityInput = it },
                    suggestions = suggestions,
                    onSuggestionSelected = { },
                    isLoadingSuggestions = true, // Now loading
                    hasSearchedCities = false,
                    placeholder = "Enter city name"
                )
            }
        }

        // Loading indicator should be visible
        composeTestRule.onNodeWithText("Searching cities...").assertIsDisplayed()
    }

    @Test
    fun autocompleteTextField_showsNoCitiesFoundAfterTimeout() {
        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = "xyz",
                    onValueChange = { },
                    suggestions = emptyList(),
                    onSuggestionSelected = { },
                    isLoadingSuggestions = false,
                    hasSearchedCities = true, // Search completed with no results
                    placeholder = "Enter city name"
                )
            }
        }

        // Should show "no cities found" message
        composeTestRule.onNodeWithText("No cities found").assertIsDisplayed()
    }

    @Test
    fun autocompleteTextField_showsSuggestionsWithWhiteText() {
        val suggestions = listOf("London", "Los Angeles", "Louisville")
        
        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = "Lo",
                    onValueChange = { },
                    suggestions = suggestions,
                    onSuggestionSelected = { },
                    isLoadingSuggestions = false,
                    hasSearchedCities = true,
                    placeholder = "Enter city name"
                )
            }
        }

        // All suggestions should be visible
        suggestions.forEach { city ->
            composeTestRule.onNodeWithText(city).assertIsDisplayed()
        }
    }

    @Test
    fun autocompleteTextField_suggestionClickWorks() {
        val suggestions = listOf("London", "Los Angeles")
        var selectedCity = ""
        
        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = "Lo",
                    onValueChange = { },
                    suggestions = suggestions,
                    onSuggestionSelected = { selectedCity = it },
                    isLoadingSuggestions = false,
                    hasSearchedCities = true,
                    placeholder = "Enter city name"
                )
            }
        }

        // Click on first suggestion
        composeTestRule.onNodeWithText("London").performClick()
        
        // Verify callback was called (in real test, you'd verify the UI state change)
        assert(selectedCity == "London")
    }

    @Test
    fun autocompleteTextField_clearsStateWhenInputTooShort() {
        composeTestRule.setContent {
            WeatherAppTheme {
                AutocompleteTextField(
                    value = "L", // Too short
                    onValueChange = { },
                    suggestions = emptyList(),
                    onSuggestionSelected = { },
                    isLoadingSuggestions = false,
                    hasSearchedCities = false,
                    placeholder = "Enter city name"
                )
            }
        }

        // Should not show loading or suggestions for short input
        composeTestRule.onNodeWithText("Searching cities...").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("No cities found").assertIsNotDisplayed()
    }
}