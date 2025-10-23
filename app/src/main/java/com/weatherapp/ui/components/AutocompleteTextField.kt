package com.weatherapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.weatherapp.ui.theme.*

/**
 * Modern autocomplete text field component with glass-morphism and animations
 */
@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Search
    ),
    isLoadingSuggestions: Boolean = false,
    hasSearchedCities: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Animation states
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 4.dp,
        animationSpec = tween(300),
        label = "elevation"
    )
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 12.dp,
        animationSpec = tween(300),
        label = "cornerRadius"
    )

    Column(modifier = modifier) {
        // Modern Search Input with Glass-morphism
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(cornerRadius),
                    ambientColor = PrimaryBlue.copy(alpha = 0.1f),
                    spotColor = PrimaryBlue.copy(alpha = 0.25f)
                ),
            shape = RoundedCornerShape(cornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SurfaceLight.copy(alpha = 0.95f),
                                SurfaceLight.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryBlue
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        }
                        .semantics { contentDescription = "City input field" },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextOnDark,
                        unfocusedTextColor = TextOnDark,
                        cursorColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
            }
        }

        // Modern Dropdown with Animations
        AnimatedVisibility(
            visible = (suggestions.isNotEmpty() || isLoadingSuggestions || (hasSearchedCities && suggestions.isEmpty() && value.isNotBlank())) && isFocused,
            enter = slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = PrimaryBlue.copy(alpha = 0.1f),
                            spotColor = PrimaryBlue.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SurfaceVariantLight,
                                        SurfaceVariantDark
                                    )
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            when {
                                // Show loading state when searching for suggestions
                                isLoadingSuggestions && value.isNotBlank() -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = PrimaryBlue,
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Searching cities...",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextOnDark
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                // Show suggestions when available
                                suggestions.isNotEmpty() -> {
                                    // Show the search text as the first item if user has typed something and there are suggestions
                                    if (value.isNotBlank() && isFocused) {
                                        item {
                                            ModernSuggestionItem(
                                                suggestion = value,
                                                onClick = {
                                                    onSuggestionSelected(value)
                                                    focusManager.clearFocus()
                                                },
                                                isSearchText = true
                                            )
                                        }
                                    }
                                    
                                    // Show city suggestions
                                    items(suggestions) { suggestion ->
                                        ModernSuggestionItem(
                                            suggestion = suggestion,
                                            onClick = {
                                                onSuggestionSelected(suggestion)
                                                focusManager.clearFocus()
                                            }
                                        )
                                    }
                                }
                                
                                // Show "No cities found" only when search is complete and no results
                                hasSearchedCities && suggestions.isEmpty() && value.isNotBlank() && !isLoadingSuggestions -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = com.weatherapp.ui.constants.AppStrings.NO_CITIES_FOUND,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextOnDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernSuggestionItem(
    suggestion: String,
    onClick: () -> Unit,
    isSearchText: Boolean = false
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(200),
        label = "backgroundColor"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = suggestion,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSearchText) FontWeight.Bold else FontWeight.Medium,
                color = if (isSearchText) PrimaryBlue else TextOnDark
            )
        )
    }
}