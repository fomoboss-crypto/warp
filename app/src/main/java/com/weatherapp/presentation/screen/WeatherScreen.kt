package com.weatherapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weatherapp.presentation.components.WeatherCard
import com.weatherapp.presentation.viewmodel.WeatherViewModel
import com.weatherapp.ui.components.AutocompleteTextField
import com.weatherapp.ui.constants.AppStrings

/**
 * Main weather screen composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Show error message in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App title
            Text(
                text = "Weather App",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // City input section
            CityInputSection(
                cityInput = uiState.cityInput,
                citySuggestions = uiState.citySuggestions,
                onCityInputChange = viewModel::updateCityInput,
                onCitySuggestionSelect = viewModel::selectCitySuggestion,
                onSearchClick = {
                    keyboardController?.hide()
                    viewModel.getWeather()
                },
                isLoading = uiState.isLoading,
                isLoadingSuggestions = uiState.isLoadingSuggestions,
                hasSearchedCities = uiState.hasSearchedCities,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Content section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingContent()
                    }
                    uiState.hasWeatherData -> {
                        uiState.weatherData?.let { weatherData ->
                            WeatherCard(
                                weatherData = weatherData,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    uiState.isIdle -> {
                        IdleContent()
                    }
                }
            }
        }
    }
}

/**
 * City input section with text field and search button
 */
@Composable
private fun CityInputSection(
    cityInput: String,
    citySuggestions: List<String>,
    onCityInputChange: (String) -> Unit,
    onCitySuggestionSelect: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean,
    isLoadingSuggestions: Boolean,
    hasSearchedCities: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AutocompleteTextField(
            value = cityInput,
            onValueChange = onCityInputChange,
            onSuggestionSelected = onCitySuggestionSelect,
            suggestions = citySuggestions,
            label = AppStrings.ENTER_CITY_NAME,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClick() }
            ),
            enabled = !isLoading,
            isLoadingSuggestions = isLoadingSuggestions,
            hasSearchedCities = hasSearchedCities,
            onSearchSubmit = onSearchClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Loading content with progress indicator
 */
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = AppStrings.LOADING,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Idle content when no data is displayed
 */
@Composable
private fun IdleContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Enter a city name to get weather information",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}