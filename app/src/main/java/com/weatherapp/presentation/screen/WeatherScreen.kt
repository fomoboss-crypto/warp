package com.weatherapp.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.weatherapp.presentation.components.WeatherCard
import com.weatherapp.presentation.viewmodel.WeatherViewModel
import com.weatherapp.ui.components.AutocompleteTextField
import com.weatherapp.ui.constants.AppStrings
import com.weatherapp.ui.theme.*

/**
 * Main weather screen composable with modern animations and styling
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
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.semantics { 
                    contentDescription = "Error message" 
                }
            ) 
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkGradientStart,
                            DarkGradientMid,
                            DarkGradientEnd
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated app title
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800)) + 
                           slideInVertically(
                               animationSpec = tween(800),
                               initialOffsetY = { -it / 2 }
                           )
                ) {
                    Text(
                        text = "Weather App",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = TextOnDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
                
                // Animated city input section
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(600, delayMillis = 200)
                    ) + slideInVertically(
                        animationSpec = tween(600, delayMillis = 200),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    CityInputSection(
                        cityInput = uiState.cityInput,
                        citySuggestions = uiState.citySuggestions,
                        onCityInputChange = viewModel::updateCityInput,
                        onCitySuggestionSelect = viewModel::selectCitySuggestion,
                        onSearchClick = {
                            keyboardController?.hide()
                            viewModel.getWeather()
                        },
                        isLoadingSuggestions = uiState.isLoadingSuggestions,
                        hasSearchedCities = uiState.hasSearchedCities,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Animated content section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Loading animation
                    AnimatedVisibility(
                        visible = uiState.isLoading,
                        enter = fadeIn(animationSpec = tween(400)) + 
                               scaleIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(300)) + 
                              scaleOut(animationSpec = tween(300))
                    ) {
                        LoadingContent()
                    }
                    
                    // Weather card animation
                    AnimatedVisibility(
                        visible = uiState.hasWeatherData,
                        enter = fadeIn(
                            animationSpec = tween(600, delayMillis = 100)
                        ) + slideInVertically(
                            animationSpec = tween(600, delayMillis = 100),
                            initialOffsetY = { it / 4 }
                        ),
                        exit = fadeOut(animationSpec = tween(300)) + 
                              slideOutVertically(animationSpec = tween(300))
                    ) {
                        uiState.weatherData?.let { weatherData ->
                            WeatherCard(
                                weatherData = weatherData,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Idle content animation
                    AnimatedVisibility(
                        visible = uiState.isIdle,
                        enter = fadeIn(
                            animationSpec = tween(500, delayMillis = 300)
                        ) + slideInVertically(
                            animationSpec = tween(500, delayMillis = 300),
                            initialOffsetY = { it / 6 }
                        ),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        IdleContent()
                    }
                }
            }
        }
    }
}

/**
 * City input section with autocomplete text field
 */
@Composable
private fun CityInputSection(
    cityInput: String,
    citySuggestions: List<String>,
    onCityInputChange: (String) -> Unit,
    onCitySuggestionSelect: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoadingSuggestions: Boolean,
    hasSearchedCities: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AutocompleteTextField(
            value = cityInput,
            onValueChange = onCityInputChange,
            onSuggestionSelected = onCitySuggestionSelect,
            suggestions = citySuggestions,
            placeholder = AppStrings.ENTER_CITY_NAME,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClick() }
            ),
            isLoadingSuggestions = isLoadingSuggestions,
            hasSearchedCities = hasSearchedCities,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Modern loading content with animated progress indicator
 */
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        CircularProgressIndicator(
            color = PrimaryBlue,
            strokeWidth = 4.dp,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Loading weather data" }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Loading weather data...",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = TextOnDark,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Modern idle content when no data is displayed
 */
@Composable
private fun IdleContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(40.dp)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600)) + 
                   slideInVertically(
                       animationSpec = tween(600),
                       initialOffsetY = { it / 6 }
                   )
        ) {
            Text(
                text = "Enter a city name to get weather information",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextOnDark.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}