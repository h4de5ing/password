package com.password.shared

import kotlin.test.Test
import kotlin.test.assertTrue

class GreetingTest {

    @Test
    fun testExample() {
        val greeting = Greeting()
        assertTrue(greeting.greet().contains("Hello"), "Check that the greeting contains Hello")
    }
}
