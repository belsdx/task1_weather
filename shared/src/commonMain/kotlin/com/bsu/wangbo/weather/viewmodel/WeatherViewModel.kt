package com.bsu.wangbo.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsu.wangbo.weather.model.WeatherResponse
import com.bsu.wangbo.weather.network.WeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val data: WeatherResponse, val isOffline: Boolean = false) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel : ViewModel() {
    private val api = WeatherApi()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState

    // 🌟 实验室文档要求的轻量级离线缓存库
    private companion object {
        private val cacheMap = mutableMapOf<String, WeatherResponse>()
    }

    fun fetchWeather(city: String) {
        val trimmedCity = city.trim()
        if (trimmedCity.isBlank()) return

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = api.fetchWeather(trimmedCity)
                // 成功获取数据，更新/保存到离线缓存中
                cacheMap[trimmedCity.lowercase()] = response
                _uiState.value = WeatherUiState.Success(response, isOffline = false)
            } catch (e: Exception) {
                // 🌟 核心离线机制：如果请求因断网等异常失败，尝试去缓存中抓取该城市上一次的数据
                val cachedData = cacheMap[trimmedCity.lowercase()]
                if (cachedData != null) {
                    _uiState.value = WeatherUiState.Success(cachedData, isOffline = true)
                } else {
                    _uiState.value = WeatherUiState.Error(
                        "无法连接到网络，且无该城市的离线缓存历史记录。"
                    )
                }
            }
        }
    }
}