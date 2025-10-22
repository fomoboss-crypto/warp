package com.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex

/**
 * Autocomplete text field component with dropdown suggestions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null
        )
    },
    placeholder: @Composable (() -> Unit)? = null,
    maxSuggestions: Int = 10,
    isLoadingSuggestions: Boolean = false,
    hasSearchedCities: Boolean = false,
    onSearchSubmit: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Show dropdown when field has focus, value is not empty, and either:
    // - Loading suggestions, OR
    // - Has suggestions, OR  
    // - Has searched and no suggestions found (to show "No cities found" message)
    val showDropdown = expanded && hasFocus && value.isNotBlank() && value.length >= 2 &&
                      (isLoadingSuggestions || suggestions.isNotEmpty() || 
                       (hasSearchedCities && !isLoadingSuggestions && suggestions.isEmpty()))
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                expanded = newValue.isNotBlank()
            },
            label = { Text(label) },
            leadingIcon = leadingIcon,
            placeholder = placeholder,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    hasFocus = focusState.isFocused
                    if (focusState.isFocused && value.isNotBlank()) {
                        expanded = true
                    } else if (!focusState.isFocused) {
                        // Delay hiding to allow for suggestion selection
                        expanded = false
                    }
                }
        )
        
        // Dropdown with suggestions
        if (showDropdown) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .zIndex(1f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    if (isLoadingSuggestions) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Searching cities...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else if (suggestions.isEmpty() && value.isNotBlank() && hasSearchedCities) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No cities found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        itemsIndexed(
                            items = suggestions.take(maxSuggestions)
                        ) { index, suggestion ->
                            SuggestionItem(
                                suggestion = suggestion,
                                query = value,
                                onClick = {
                                    onSuggestionSelected(suggestion)
                                    onValueChange(suggestion)
                                    expanded = false
                                    focusManager.clearFocus()
                                    // Auto-submit when a city is selected
                                    onSearchSubmit?.invoke()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual suggestion item in the dropdown
 */
@Composable
private fun SuggestionItem(
    suggestion: String,
    query: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Highlight matching part of the suggestion
            val queryLowerCase = query.lowercase()
            val suggestionLowerCase = suggestion.lowercase()
            val startIndex = suggestionLowerCase.indexOf(queryLowerCase)
            
            if (startIndex >= 0) {
                val endIndex = startIndex + query.length
                
                // Text before match
                if (startIndex > 0) {
                    Text(
                        text = suggestion.substring(0, startIndex),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Highlighted match
                Text(
                    text = suggestion.substring(startIndex, endIndex),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Text after match
                if (endIndex < suggestion.length) {
                    Text(
                        text = suggestion.substring(endIndex),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                // No match found, display normal text
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}