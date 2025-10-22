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
│  │     UI      │  │             │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│            Domain Layer             │
│  ┌─────────────┐  ┌─────────────┐   │
│  │ Use Cases   │  │ Repository  │   │
│  │             │  │ Interface   │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│             Data Layer              │
│  ┌─────────────┐  ┌─────────────┐   │
│  │ Repository  │  │ API Service │   │
│  │    Impl     │  │             │   │
│  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────┘
```

### Key Architectural Decisions

#### 1. **MVVM with Clean Architecture**
- **Presentation Layer**: Jetpack Compose UI + ViewModels
- **Domain Layer**: Use cases and repository interfaces
- **Data Layer**: Repository implementations and API services
- **Benefits**: Clear separation of concerns, improved testability, maintainability

#### 2. **Jetpack Compose for UI**
- **Why**: Modern declarative UI toolkit
- **Benefits**: Less boilerplate, better performance, easier state management
- **Implementation**: Single-activity architecture with composable screens

#### 3. **Repository Pattern**
- **Purpose**: Abstract data sources from business logic
- **Implementation**: Interface in domain layer, implementation in data layer
- **Benefits**: Easy to mock for testing, flexible data source switching

#### 4. **Dependency Injection Ready**
- **Structure**: Constructor injection pattern
- **Benefits**: Improved testability, loose coupling
- **Future**: Ready for Hilt/Dagger integration

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
│   │   └── WeatherApiService.kt      # API service definitions
│   ├── model/
│   │   ├── WeatherData.kt            # Domain models
│   │   └── WeatherResponse.kt        # API response models
│   └── repository/
│       └── WeatherRepositoryImpl.kt  # Repository implementation
├── domain/                           # Domain Layer
│   ├── exception/
│   │   └── WeatherException.kt       # Custom exceptions
│   ├── model/
│   │   └── Weather.kt                # Domain entities
│   ├── repository/
│   │   └── WeatherRepository.kt      # Repository interface
│   └── usecase/
│       └── GetWeatherUseCase.kt      # Business logic
├── presentation/                     # Presentation Layer
│   ├── ui/
│   │   ├── components/               # Reusable UI components
│   │   ├── screen/                   # Screen composables
│   │   └── theme/                    # App theming
│   └── viewmodel/
│       ├── WeatherViewModel.kt       # UI state management
│       └── WeatherUiState.kt         # UI state definitions
└── MainActivity.kt                   # Single activity entry point
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
- **Mockito**: Mocking framework (ready for implementation)

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

We believe in building reliable software, so this app comes with a solid testing foundation:

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
We aim for comprehensive coverage of business logic and data handling. The tests help ensure that when you search for "London," you get London's weather, not London, Ontario by mistake!

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

Nobody likes apps that crash, so we've built this one to handle problems gracefully:

### When Things Go Wrong
The app is prepared for real-world scenarios:
- **No internet?** You'll get a friendly message instead of a crash
- **City not found?** The app will let you know and suggest trying a different spelling
- **API having issues?** We'll show you what's happening and suggest trying again later
- **Slow connection?** Loading indicators keep you informed while we fetch your data

### Keeping Your Data Safe
- **API keys are secure**: Your OpenWeatherMap key stays in local configuration files, never in the code
- **No personal data stored**: We don't collect or store any of your personal information
- **Network security**: All API calls use HTTPS encryption
- **Input validation**: The app checks what you type to prevent any funny business

### Error Types You Might See
- **Network errors**: When your internet is playing hide and seek
- **API errors**: When the weather service needs a coffee break
- **Validation errors**: When a city name doesn't quite match what we're expecting

The app handles all of these gracefully, so you always know what's happening and what to do next.

## ⚡ Performance Optimizations

We've fine-tuned this app to be fast and efficient, because nobody has time to wait for weather updates:

### Smart API Usage
- **Direct metric units**: We request temperatures in Celsius directly from the API instead of converting from Kelvin (one less calculation!)
- **Efficient caching**: The app remembers recent weather data so it doesn't need to ask the internet every time
- **Debounced search**: When you're typing a city name, we wait until you pause before searching (saves unnecessary API calls)

### Network Optimizations
- **10MB response cache**: Recent weather data is stored locally for faster access
- **Optimized timeouts**: Connection settings are tuned for the best balance of speed and reliability
- **Smart logging**: Debug information is only collected during development, keeping the production app lean

### Code Efficiency
- **Clean architecture**: Well-organized code means faster compilation and easier maintenance
- **Removed duplicate code**: Less code means faster execution and smaller app size
- **Optimized UI updates**: The interface only refreshes when it actually needs to

### What This Means for You
- **Faster searches**: City suggestions appear quickly as you type
- **Less data usage**: Cached responses mean fewer internet requests
- **Smoother experience**: The app feels responsive even on slower connections
- **Longer battery life**: Efficient code means your phone works less hard

## 🚀 Future Enhancements

Got ideas? So do we! Here are some cool features we're thinking about adding:

### Weather Features
- **7-day forecast**: Because sometimes you need to plan that weekend trip
- **Hourly predictions**: Perfect for "should I bring an umbrella to lunch?" decisions
- **Weather alerts**: Get notified about severe weather in your area
- **Multiple locations**: Save your favorite cities for quick access
- **Weather maps**: Visual radar and satellite imagery

### User Experience
- **Dark mode**: For those late-night weather checks
- **Custom themes**: Make the app match your style
- **Widget support**: Weather info right on your home screen
- **Voice search**: "Hey app, what's the weather in Tokyo?"
- **Offline mode**: Basic functionality even without internet

### Smart Features
- **Location-based weather**: Automatic weather for wherever you are
- **Smart notifications**: "It's going to rain in 30 minutes!"
- **Weather-based suggestions**: "Perfect day for a picnic!" or "Maybe stay inside today"
- **Historical data**: "This time last year it was..."

### Technical Improvements
- **Wear OS support**: Weather on your smartwatch
- **Better accessibility**: Making sure everyone can use the app
- **Performance monitoring**: Keep making it faster and smoother

Have a feature request? We'd love to hear it!

## 🤝 Contributing

Want to help make this weather app even better? We'd love to have you on board! Here's how you can contribute:

### Ways to Help
- **Found a bug?** Open an issue and tell us what went wrong
- **Have an idea?** Suggest new features or improvements
- **Code contributions**: Fix bugs, add features, or improve performance
- **Documentation**: Help make our docs even clearer
- **Testing**: Try the app on different devices and report what you find

### Getting Started with Contributing
1. **Fork the repository** - Make your own copy to work with
2. **Create a feature branch** - Keep your changes organized
3. **Make your changes** - Fix that bug or add that feature
4. **Test everything** - Make sure your changes work and don't break anything else
5. **Submit a pull request** - Share your improvements with us!

### Code Style
We try to keep things consistent:
- Follow the existing Kotlin coding conventions
- Add comments for complex logic
- Write tests for new features
- Keep commits focused and descriptive

### What We're Looking For
- Bug fixes (always appreciated!)
- Performance improvements
- New weather features
- UI/UX enhancements
- Better error handling
- More comprehensive tests

Every contribution, big or small, helps make this app better for everyone!

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**What does this mean?** You're free to use, modify, and distribute this code however you'd like! Just keep the original license notice if you share it.

## 🙏 Acknowledgments

Big thanks to the amazing tools and services that make this app possible:

- **[OpenWeatherMap](https://openweathermap.org/)** - For providing reliable weather data and a developer-friendly API
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Making Android UI development actually enjoyable
- **[Retrofit](https://square.github.io/retrofit/)** - For making API calls simple and elegant
- **[OkHttp](https://square.github.io/okhttp/)** - The networking powerhouse behind the scenes
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - For keeping everything smooth and responsive

## 💬 Support

Got questions? Running into issues? Here's how to get help:

- **Check the issues**: Someone might have already asked the same question
- **Open a new issue**: If you can't find an answer, create a new issue with details about your problem
- **Discussions**: For general questions or feature ideas, start a discussion

We're here to help and always happy to chat about weather apps, Android development, or anything related to this project!

---

*Made with ☀️ and a little bit of ⛈️ by developers who care about good weather apps*