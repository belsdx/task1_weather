package com.bsu.wangbo.weather.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("name") val cityName: String,
    @SerialName("main") val main: MainInfo,
    @SerialName("weather") val weatherDescription: List<WeatherDescription>,
    @SerialName("wind") val wind: WindInfo
)

@Serializable
data class MainInfo(
    @SerialName("temp") val temperature: Double,
    @SerialName("humidity") val humidity: Int
)

@Serializable
data class WeatherDescription(
    @SerialName("main") val main: String,       // 例如: Clear, Rain
    @SerialName("description") val desc: String, // 详细中文描述
    @SerialName("icon") val icon: String        // 天气图标 ID
)

@Serializable
data class WindInfo(
    @SerialName("speed") val speed: Double
)