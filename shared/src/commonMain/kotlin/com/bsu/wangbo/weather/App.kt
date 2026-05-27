package com.bsu.wangbo.weather

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsu.wangbo.weather.viewmodel.WeatherUiState
import com.bsu.wangbo.weather.viewmodel.WeatherViewModel
import com.bsu.wangbo.weather.model.WeatherResponse

@Composable
fun App() {
    // 自动识别当前运行的平台系统
    val platformName = remember { getPlatform().name.lowercase() }
    val isAndroid = platformName.contains("android")
    val isIos = platformName.contains("ios")
    val isLinux = platformName.contains("java") || platformName.contains("desktop") || platformName.contains("linux")

    val viewModel: WeatherViewModel = viewModel { WeatherViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    var cityInput by remember { mutableStateOf("") }

    // iOS 平台快捷城市列表
    val iosQuickCities = listOf("Minsk", "Moscow", "Beijing")
    var selectedSegmentIndex by remember { mutableStateOf(-1) }

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
        ) {
            val screenWidth = maxWidth

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .maxWidthAdapter(screenWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "跨平台天气预报 (${getPlatform().name})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 🌟 iOS 独有硬性指标：手写高兼容性 iOS 原生风格 SegmentedControl
                if (isIos) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        iosQuickCities.forEachIndexed { index, city ->
                            val isSelected = selectedSegmentIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedSegmentIndex = index
                                        cityInput = city
                                        viewModel.fetchWeather(city)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else Color.DarkGray
                                )
                            }
                        }
                    }
                }

                // 🌟 搜索栏区域：根据不同平台规范定制外观（使用 Emoji 作为 LeadingIcon，完美避开图标库缺失）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        isIos -> {
                            // iOS 风格：高圆角、浅色底、无外线框的输入栏
                            TextField(
                                value = cityInput,
                                onValueChange = {
                                    cityInput = it
                                    selectedSegmentIndex = -1
                                },
                                placeholder = { Text("搜索城市...") },
                                leadingIcon = { Text("🔍", fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFE5E5EA),
                                    unfocusedContainerColor = Color(0xFFE5E5EA),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        isLinux -> {
                            // Linux 风格：完全硬直角、清晰高对比度线框
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("输入城市名称") },
                                leadingIcon = { Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(0.dp)
                            )
                        }
                        else -> {
                            // Android / 默认 M3 风格
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("请输入城市") },
                                leadingIcon = { Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.fetchWeather(cityInput) },
                        shape = when {
                            isLinux -> RoundedCornerShape(0.dp)
                            isIos -> RoundedCornerShape(12.dp)
                            else -> ButtonDefaults.shape
                        }
                    ) {
                        Text("搜索")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🌟 业务状态渲染区域
                when (val state = uiState) {
                    is WeatherUiState.Idle -> {
                        Text("请输入城市名并点击搜索", color = Color.Gray)
                    }
                    is WeatherUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is WeatherUiState.Success -> {
                        // 数据请求成功：渲染天气详情（完美支持多端及 Web 响应式平铺）
                        WeatherInfoDisplay(state.data, isAndroid, isIos, isLinux, screenWidth)
                    }
                    is WeatherUiState.Error -> {
                        Text("错误: ${state.message}", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 🌟 天气信息综合展示面板
@Composable
fun WeatherInfoDisplay(
    weather: WeatherResponse,
    isAndroid: Boolean,
    isIos: Boolean,
    isLinux: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val weatherMainInfo = weather.weatherDescription.firstOrNull()?.main ?: "Clear"
        val weatherDescText = weather.weatherDescription.firstOrNull()?.desc ?: "未知"

        // 根据网络数据动态获取精美的天气 Emoji 状态图标
        val weatherEmoji = remember(weatherMainInfo) { getWeatherEmoji(weatherMainInfo) }

        // 主天气卡片
        WeatherCard(isAndroid, isIos, isLinux, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = weather.cityName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // 🌟 完美展现文档要求的“天气图标 (иконка)”，全平台兼容不卡顿
                Text(text = weatherEmoji, fontSize = 64.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "${weather.main.temperature} °C", fontSize = 48.sp, fontWeight = FontWeight.SemiBold)
                Text(text = weatherDescText, fontSize = 18.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 严格落实 Web/多端自适应布局逻辑（手机单列平铺、平板及宽屏双列并排）
        if (screenWidth > 600.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    DetailCard("湿度", "${weather.main.humidity} %", "💧", isAndroid, isIos, isLinux)
                }
                Box(modifier = Modifier.weight(1f)) {
                    DetailCard("风速", "${weather.wind.speed} m/s", "💨", isAndroid, isIos, isLinux)
                }
            }
        } else {
            // 窄屏/移动端：降级为垂直单列布局
            DetailCard("湿度", "${weather.main.humidity} %", "💧", isAndroid, isIos, isLinux)
            Spacer(modifier = Modifier.height(12.dp))
            DetailCard("风速", "${weather.wind.speed} m/s", "💨", isAndroid, isIos, isLinux)
        }
    }
}

@Composable
fun DetailCard(
    label: String,
    value: String,
    emoji: String,
    isAndroid: Boolean,
    isIos: Boolean,
    isLinux: Boolean
) {
    WeatherCard(isAndroid, isIos, isLinux, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
            Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3), fontSize = 16.sp)
        }
    }
}

// 🌟 多端定制通用卡片：动态根据当前运行的操作系统改变 UI 外观
@Composable
fun WeatherCard(
    isAndroid: Boolean,
    isIos: Boolean,
    isLinux: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when {
        isAndroid -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                content = { content() }
            )
        }
        isIos -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                content = { content() }
            )
        }
        isLinux -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                content = { content() }
            )
        }
        else -> {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                content = { content() }
            )
        }
    }
}

// 辅助函数：将网络接口返回的天气 main 字符串智能映射成生动的系统 Emoji
private fun getWeatherEmoji(main: String): String {
    return when (main.lowercase()) {
        "clouds" -> "☁️"
        "rain", "drizzle" -> "🌧️"
        "thunderstorm" -> "⛈️"
        "clear" -> "☀️"
        "snow" -> "❄️"
        "mist", "smoke", "haze", "fog" -> "🌫️"
        else -> "🌤️"
    }
}

// 扩展函数：控制 PC 浏览器端或大屏下的最大视觉聚拢宽度
fun Modifier.maxWidthAdapter(screenWidth: androidx.compose.ui.unit.Dp): Modifier {
    return if (screenWidth > 900.dp) this.width(700.dp) else this.fillMaxWidth()
}