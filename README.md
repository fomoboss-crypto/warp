# 🌦️ WeatherApp

A modern Android weather application built with Kotlin and Jetpack Compose, following MVVM architecture and Clean Architecture principles.

## 📋 Requirements

- **Minimum Android SDK**: API level 24 (Android 7.0)
- **Target SDK**: API level 34 (Android 14)
- **Compile SDK**: API level 34
- **Java**: Version 11 or higher
- **Android Studio**: Arctic Fox or newer

## 🚀 Build and Run Instructions

### Prerequisites
1. Install Android Studio with Android SDK
2. Ensure Java 11+ is installed
3. Get a free API key from [OpenWeatherMap](https://openweathermap.org/api)

### Setup Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/fomoboss-crypto/warp.git
   cd warp
   ```

2. **Configure API Key**:
   Create a `local.properties` file in the project root and add:
   ```properties
   WEATHER_API_KEY=your_actual_api_key_here
   ```

3. **Build and Install**:
   ```bash
   # Install debug build on connected device/emulator
   ./gradlew installDebug
   
   # Or build APK file
   ./gradlew assembleDebug
   
   # Run tests
   ./gradlew test
   ```

4. **Alternative**: Open the project in Android Studio and click the "Run" button.

### Build Variants
- **Debug**: Development build with logging enabled
- **Release**: Production build with ProGuard optimization

## 🏗️ Architecture and Design Decisions

This application follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)** pattern to ensure maintainability, testability, and separation of concerns.

### Architecture Overview

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  ┌─────────────┐  ┌─────────────┐   │
│  │   Compose   │  │  ViewModel  │   │
│  │     UI      │  │   + State   │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│            Domain Layer             │
│  ┌─────────────┐  ┌─────────────┐   │
│  │ Repository  │  │ Exceptions  │   │
│  │ Interface   │  │ & Models    │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│             Data Layer              │
│  ┌─────────────┐  ┌─────────────┐   │
│  │ Repository  │  │ API Services│   │
│  │Implementation│  │ & Models    │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│        Dependency Injection        │
│  ┌─────────────┐  ┌─────────────┐   │
│  │ AppContainer│  │ Application │   │
│  │             │  │   Context   │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
```

### Key Architectural Decisions

#### 1. **MVVM with Clean Architecture**
- **Presentation Layer**: Jetpack Compose UI + ViewModels + UI State Management
- **Domain Layer**: Repository interfaces, domain models, and custom exceptions
- **Data Layer**: Repository implementations, API services (Weather & Geocoding), and data models
- **Dependency Injection**: AppContainer for managing dependencies
- **Benefits**: Clear separation of concerns, improved testability, maintainability

#### 2. **Jetpack Compose for UI**
- **Why**: Modern declarative UI toolkit
- **Benefits**: Less boilerplate, better performance, easier state management
- **Implementation**: Single-activity architecture with composable screens

#### 3. **Repository Pattern**
- **Purpose**: Abstract data sources from business logic
- **Implementation**: Interface in domain layer, implementation in data layer
- **Benefits**: Easy to mock for testing, flexible data source switching

#### 4. **Dependency Injection Pattern**
- **Structure**: Manual dependency injection with AppContainer
- **Implementation**: Constructor injection pattern throughout the app
- **Benefits**: Improved testability, loose coupling, centralized dependency management
- **Future**: Ready for Hilt/Dagger integration when needed

#### 5. **Error Handling Strategy**
- **Approach**: Custom exception types with user-friendly messages
- **Implementation**: Sealed classes for different error states
- **Benefits**: Consistent error handling across the app

### Project Structure

```
app/src/main/java/com/weatherapp/
├── data/                             # Data Layer
│   ├── api/
│   │   ├── ApiClient.kt              # HTTP client configuration
│   │   ├── WeatherApiService.kt      # Weather API service definitions
│   │   └── GeocodeApiService.kt      # Geocoding API service definitions
│   ├── local/
│   │   └── CityDataSource.kt         # Local city data and suggestions
│   ├── model/
│   │   ├── WeatherData.kt            # Domain models
│   │   ├── WeatherResponse.kt        # Weather API response models
│   │   └── GeocodeResponse.kt        # Geocoding API response models
│   ├── repository/
│   │   └── WeatherRepositoryImpl.kt  # Repository implementation
│   └── service/
│       └── GeocodeService.kt         # Geocoding service logic
├── di/                               # Dependency Injection
│   └── AppContainer.kt               # Manual DI container
├── domain/                           # Domain Layer
│   ├── exception/
│   │   └── WeatherException.kt       # Custom exceptions
│   ├── model/
│   │   └── Result.kt                 # Result wrapper for success/error states
│   └── repository/
│       └── WeatherRepository.kt      # Repository interface
├── presentation/                     # Presentation Layer
│   ├── components/
│   │   ├── WeatherCard.kt            # Weather display components
│   │   └── WeatherIcon.kt            # Weather icon component
│   ├── screen/
│   │   └── WeatherScreen.kt          # Main weather screen composable
│   └── viewmodel/
│       ├── WeatherViewModel.kt       # UI state management
│       └── WeatherUiState.kt         # UI state definitions
├── ui/                               # UI Components & Theme
│   ├── components/
│   │   └── AutocompleteTextField.kt  # Reusable autocomplete input
│   ├── constants/
│   │   └── AppStrings.kt             # String constants
│   └── theme/                        # App theming
│       ├── Color.kt                  # Color definitions
│       ├── Theme.kt                  # Theme configuration
│       └── Type.kt                   # Typography definitions
├── MainActivity.kt                   # Single activity entry point
└── WeatherApplication.kt             # Application class with DI setup
```

### Technology Stack

#### Core Technologies
- **Kotlin 1.9.20**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Material 3**: Design system implementation
- **Coroutines**: Asynchronous programming

#### Networking & Data
- **Retrofit**: HTTP client for API calls
- **OkHttp**: HTTP client implementation
- **Gson**: JSON serialization/deserialization
- **OpenWeatherMap API**: Weather data provider

#### Testing
- **JUnit**: Unit testing framework
- **Espresso**: UI testing framework
- **MockK**: Mocking framework for Kotlin

#### Build & Development
- **Gradle Kotlin DSL**: Build configuration
- **ProGuard**: Code obfuscation and optimization
- **Git**: Version control

### Design Patterns Used

1. **MVVM**: Separation of UI and business logic
2. **Repository Pattern**: Data access abstraction
3. **Observer Pattern**: UI state observation with StateFlow
4. **Factory Pattern**: Object creation (API client)
5. **Singleton Pattern**: Single instance management (API client)

### Performance Considerations

- **Lazy Loading**: Components loaded on demand
- **State Management**: Efficient state updates with Compose
- **Memory Management**: Proper lifecycle handling
- **Network Optimization**: Request caching and error retry logic
- **Build Optimization**: ProGuard for release builds

## ✨ Features

- 🌤️ **Real-time Weather**: Get current temperature, weather conditions, humidity, and wind speed for any city
- 🔍 **Smart City Search**: Autocomplete city search with real-time suggestions
- ⚡ **Lightning Fast**: Smooth loading animations and responsive UI
- 🚨 **Friendly Error Messages**: User-friendly error handling and messaging
- 🎨 **Beautiful Design**: Clean, modern interface following Material Design 3 principles
- 🌙 **Dark Mode Ready**: Automatic theme switching based on system preferences
- 📱 **Responsive Design**: Optimized for phones, tablets, and different screen sizes

## 🔧 API Configuration

The app talks to OpenWeatherMap to get all that juicy weather data. Here's what you need to know:

### Getting Your API Key
1. Head over to [OpenWeatherMap](https://openweathermap.org/api) and create a free account
2. Once you're signed up, they'll give you an API key (it's like a special password for your app)
3. The free plan gives you 1,000 API calls per day - that's plenty for testing and personal use!

### Setting It Up
Add your API key to the `local.properties` file (as mentioned in the setup steps above):

**Pro tip**: Keep this key secret! The `local.properties` file is already set up to be ignored by Git, so you won't accidentally share it with the world.

### What the App Fetches
- **Current weather** for any city you search for
- **Temperature, humidity, wind speed** - all the good stuff
- **Weather conditions** with matching icons (because who doesn't love a good weather icon?)
- **Coordinates-based weather** if you want to get fancy with GPS

The app is smart about it too - it caches responses so you're not constantly hitting the API for the same data.

## 🧪 Testing

The application includes comprehensive unit and UI tests to ensure reliability and maintainability.

### What's Tested
- **Unit Tests**: Making sure all the individual pieces work correctly
- **Repository Tests**: Verifying that data flows properly between the API and your screen
- **ViewModel Tests**: Ensuring the app's logic handles different scenarios gracefully

### Running the Tests
Want to make sure everything's working? Run the tests:
```bash
./gradlew test
```

The tests cover things like:
- API response handling (what happens when the weather service is having a bad day?)
- Error scenarios (network issues, invalid cities, etc.)
- Data transformation (making sure temperatures convert correctly)

### Test Coverage
Comprehensive test coverage ensures reliable business logic and data handling functionality.

**UI Tests**
```bash
./gradlew connectedAndroidTest
```

### Test Structure
```
app/src/test/                          # Unit tests
├── presentation/viewmodel/
│   └── WeatherViewModelTest.kt
└── data/repository/
    └── WeatherRepositoryImplTest.kt

app/src/androidTest/                   # UI tests
└── presentation/screen/
    └── WeatherScreenTest.kt
```

## 🛡️ Error Handling & Security

The application implements comprehensive error handling to provide a smooth user experience even when things go wrong.

### Error Scenarios Handled
- **Network failures**: Graceful handling of connectivity issues
- **Invalid API responses**: Robust parsing and validation
- **City not found**: Clear user feedback for invalid locations
- **API rate limits**: Appropriate retry mechanisms
- **Malformed data**: Safe fallbacks and error states

### Security Measures
- **API key protection**: Secure storage and transmission
- **Input validation**: Sanitization of user inputs
- **Network security**: HTTPS-only communication
- **Data privacy**: No personal data collection or storage

### Error Types
- **NetworkException**: Connection or timeout issues
- **ApiException**: Invalid API responses or server errors
- **ValidationException**: Invalid input data or parameters

## ⚡ Performance Optimizations

The application is optimized for performance and efficiency:

### API Optimization
- **Efficient requests**: Minimal API calls with comprehensive data retrieval
- **Request debouncing**: Prevents excessive API calls during user input
- **Error retry logic**: Smart retry mechanisms for failed requests

### Network Optimization
- **Connection pooling**: Efficient HTTP client configuration
- **Timeout management**: Appropriate timeouts for different operations
- **Background processing**: Non-blocking network operations

### Code Efficiency
- **Jetpack Compose**: Modern UI toolkit for optimal rendering performance
- **State management**: Efficient UI state handling with minimal recomposition
- **Memory optimization**: Proper resource management and lifecycle awareness

### Performance Benefits
- **Fast startup**: Quick application initialization and loading
- **Smooth animations**: Responsive UI with minimal lag
- **Low memory usage**: Efficient resource utilization
- **Battery optimization**: Minimal background processing

## 🚀 Future Enhancements

### Planned Features
- **Extended Forecasts**: 7-day and hourly weather predictions
- **Multiple Locations**: Save and manage favorite cities
- **Weather Alerts**: Severe weather notifications
- **Location Services**: GPS-based automatic weather detection
- **Offline Support**: Basic functionality without internet connection

### Technical Improvements
- **Hilt/Dagger Integration**: Replace manual DI with automated framework
- **Room Database**: Local data persistence and caching
- **Wear OS Support**: Companion app for smartwatches
- **Widget Support**: Home screen weather widgets
- **Accessibility Enhancements**: Improved screen reader support

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**What does this mean?** You're free to use, modify, and distribute this code however you'd like! Just keep the original license notice if you share it.
