package com.bsu.wangbo.weather

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "task1_weather",
    ) {
        App()
    }
}