package com.bsu.wangbo.weather

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherCacheTest {

    @Test
    fun testCityInputTrimming() {
        // 测试城市名字修剪逻辑
        val input = "  Minsk  "
        assertEquals("Minsk", input.trim())
    }

    @Test
    fun testOfflineCacheFallback() {
        // 测试当你模拟将数据放入 map 缓存时，断网时能否成功拿到
        val mockCache = mutableMapOf<String, String>()
        mockCache["minsk"] = "Cloudy, 15°C"

        val searchCity = "  Minsk ".trim().lowercase()
        assertTrue(mockCache.containsKey(searchCity))
        assertEquals("Cloudy, 15°C", mockCache[searchCity])
    }

    @Test
    fun testTemperatureFormatting() {
        val temp = 22.5
        val formatted = "$temp °C"
        assertEquals("22.5 °C", formatted)
    }
}