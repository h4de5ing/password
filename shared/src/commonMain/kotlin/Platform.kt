package com.password.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
