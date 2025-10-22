package com.weatherapp.data.repository

import com.weatherapp.data.api.WeatherApiService
import com.weatherapp.data.model.WeatherResponse
import com.weatherapp.data.model.Main
import com.weatherapp.data.model.Weather
import com.weatherapp.data.model.Wind
import com.weatherapp.data.model.Coordinates
import com.weatherapp.data.model.Sys
import com.weatherapp.data.model.Clouds
import com.weatherapp.domain.exception.WeatherException
import com.weatherapp.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class WeatherRepositoryImplTest {

    private lateinit var weatherApiService: WeatherApiService
    private lateinit var repository: WeatherRepositoryImpl

    private val sampleWeatherResponse = WeatherResponse(
        coordinates = Coordinates(longitude = -0.1257, latitude = 51.5085),
        weather = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        base = "stations",
        main = Main(
            temperature = 293.15,
            feelsLike = 292.87,
            tempMin = 291.15,
            tempMax = 295.15,
            pressure = 1013,
            humidity = 65
        ),
        visibility = 10000,
        wind = Wind(speed = 3.5, degrees = 230),
        clouds = Clouds(all = 0),
        dt = 1609459200,
        sys = Sys(
            type = 1,
            id = 1414,
            country = "GB",
            sunrise = 1609401600,
            sunset = 1609430400
        ),
        timezone = 0,
        id = 2643743,
        name = "London",
        cod = 200
    )

    @BeforeEach
    fun setup() {
        weatherApiService = mockk()
        repository = WeatherRepositoryImpl(weatherApiService)
    }

    @Test
    fun `getCurrentWeather should emit loading then success`() = runTest {
        val cityName = "London"
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } returns Response.success(sampleWeatherResponse)

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        
        val successResult = results[1] as Result.Success
        assertEquals("London", successResult.data.cityName)
        assertEquals(20, successResult.data.temperature) // 293.15 - 273.15
        assertEquals("Clear", successResult.data.condition)
        assertEquals("clear sky", successResult.data.description)
        assertEquals("01d", successResult.data.iconCode)
        assertEquals(65, successResult.data.humidity)
        assertEquals(3.5, successResult.data.windSpeed)
    }

    @Test
    fun `getCurrentWeather should handle 404 error as CityNotFoundException`() = runTest {
        val cityName = "InvalidCity"
        val httpException = HttpException(
            Response.error<WeatherResponse>(404, "Not Found".toResponseBody())
        )
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws httpException

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.CityNotFoundException)
    }

    @Test
    fun `getCurrentWeather should handle 401 error as InvalidApiKeyException`() = runTest {
        val cityName = "London"
        val httpException = HttpException(
            Response.error<WeatherResponse>(401, "Unauthorized".toResponseBody())
        )
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws httpException

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.InvalidApiKeyException)
    }

    @Test
    fun `getCurrentWeather should handle other HTTP errors as ApiException`() = runTest {
        val cityName = "London"
        val httpException = HttpException(
            Response.error<WeatherResponse>(500, "Internal Server Error".toResponseBody())
        )
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws httpException

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.ApiException)
    }

    @Test
    fun `getCurrentWeather should handle SocketTimeoutException as TimeoutException`() = runTest {
        val cityName = "London"
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws SocketTimeoutException("Timeout")

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.TimeoutException)
    }

    @Test
    fun `getCurrentWeather should handle IOException as NetworkException`() = runTest {
        val cityName = "London"
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws IOException("Network error")

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.NetworkException)
    }

    @Test
    fun `getCurrentWeather should handle unknown exceptions as UnknownException`() = runTest {
        val cityName = "London"
        val unknownException = RuntimeException("Unknown error")
        coEvery { weatherApiService.getCurrentWeather(cityName, "test_api_key") } throws unknownException

        val results = repository.getCurrentWeather(cityName).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorResult = results[1] as Result.Error
        assertTrue(errorResult.exception is WeatherException.UnknownException)
        val unknownExceptionResult = errorResult.exception as WeatherException.UnknownException
        assertEquals(unknownException, unknownExceptionResult.cause)
    }

    @Test
    fun `getCurrentWeatherByCoordinates should work correctly`() = runTest {
        val lat = 51.5085
        val lon = -0.1257
        coEvery { weatherApiService.getCurrentWeatherByCoordinates(lat, lon, "test_api_key") } returns Response.success(sampleWeatherResponse)

        val results = repository.getCurrentWeatherByCoordinates(lat, lon).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        
        val successResult = results[1] as Result.Success
        assertEquals("London", successResult.data.cityName)
        assertEquals(20, successResult.data.temperature)
    }
}