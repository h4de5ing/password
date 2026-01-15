package com.password.shared

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello from ${platform.name}!"
    }
}
