package com.mordecai.router

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform