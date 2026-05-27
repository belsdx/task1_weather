package com.bsu.wangbo.weather

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform