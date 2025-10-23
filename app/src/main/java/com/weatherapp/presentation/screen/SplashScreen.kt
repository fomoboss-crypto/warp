package com.weatherapp.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.R
import com.weatherapp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Animated splash screen with dark theme and weather-related animations
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animation values
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "logoScale"
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "textAlpha"
    )
    
    // Floating animation for weather icons
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )
    
    // Rotation animation for weather icons
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )
    
    // Start animations when composable is first composed
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(4500) // Show splash for 4.5 seconds
        onSplashFinished()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkGradientStart,
                        DarkGradientMid,
                        DarkGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated weather icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Background circle with gradient
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryBlue.copy(alpha = 0.3f),
                                    SecondaryBlue.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // Main weather icon using custom drawable
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_weather_cloudy),
                    contentDescription = "Weather App Logo",
                    modifier = Modifier
                        .size(60.dp)
                        .offset(y = floatingOffset.dp),
                    tint = PrimaryBlue
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App title with fade-in animation
            Text(
                text = "Welcome to Chris Breedt's Weather App",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnDark,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle with fade-in animation
            Text(
                text = "Your Personal Weather Companion",
                fontSize = 16.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(textAlpha)
            )
        }
        
        // Floating weather elements in background
        if (startAnimation) {
            WeatherBackgroundElements(
                rotationAngle = rotationAngle,
                floatingOffset = floatingOffset
            )
        }
    }
}

/**
 * Background weather elements for visual enhancement
 */
@Composable
private fun WeatherBackgroundElements(
    rotationAngle: Float,
    floatingOffset: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left floating cloud
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_weather_cloudy),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .offset(
                    x = 60.dp,
                    y = (120 + floatingOffset * 0.5f).dp
                )
                .alpha(0.3f),
            tint = SecondaryBlue
        )
        
        // Top-right floating element
        Box(
            modifier = Modifier
                .size(20.dp)
                .offset(
                    x = 300.dp,
                    y = (200 - floatingOffset * 0.8f).dp
                )
                .background(
                    color = TertiaryBlue.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        )
        
        // Bottom-left floating element
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(
                    x = 80.dp,
                    y = (600 + floatingOffset * 0.6f).dp
                )
                .background(
                    color = GradientStart.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        // Bottom-right floating cloud
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_weather_sunny),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .offset(
                    x = 280.dp,
                    y = (550 - floatingOffset * 0.4f).dp
                )
                .alpha(0.2f),
            tint = PrimaryBlueDark
        )
    }
}