package com.bsu.wangbo.weather.network

import com.bsu.wangbo.weather.model.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WeatherApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // 提示：正式运行时请替换为你注册的 OpenWeatherMap 秘钥
    private val apiKey = "977e091e976d195c9beb4e89bb53552b"

    suspend fun fetchWeather(city: String): WeatherResponse {
        return httpClient.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("appid", apiKey)
            parameter("units", "metric") // 使用摄氏度
            parameter("lang", "zh_cn")   // 转换语言为中文
        }.body()
    }
}