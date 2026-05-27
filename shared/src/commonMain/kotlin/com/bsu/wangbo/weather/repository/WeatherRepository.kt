package com.bsu.wangbo.weather.repository

import com.bsu.wangbo.weather.model.WeatherResponse
import com.bsu.wangbo.weather.network.WeatherApi

class WeatherRepository(private val api: WeatherApi) {
    // 内存持久化缓存，用于在无网络或请求失败时提供数据兜底
    private var cachedWeather: WeatherResponse? = null

    suspend fun getWeather(city: String): WeatherResponse {
        return try {
            val remoteData = api.fetchWeather(city)
            cachedWeather = remoteData // 请求成功时刷新本地缓存
            remoteData
        } catch (e: Exception) {
            // 当网络发生异常（如断网），如果本地有历史成功缓存则直接返回，否则向外抛出异常
            cachedWeather ?: throw e
        }
    }
}