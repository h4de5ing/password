package com.password.shared.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 全局 Snackbar 控制器：通过 CompositionLocalProvider 注入，在任意 Composable 内都可调用。
 */
interface SnackController {
    fun show(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    )
}

val LocalSnackController = staticCompositionLocalOf<SnackController> {
    error("LocalSnackController not provided")
}

@Composable
fun AppSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState)
}

fun createSnackController(
    scope: CoroutineScope,
    hostState: SnackbarHostState
): SnackController = object : SnackController {
    override fun show(message: String, actionLabel: String?, duration: SnackbarDuration) {
        scope.launch {
            hostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }
}
