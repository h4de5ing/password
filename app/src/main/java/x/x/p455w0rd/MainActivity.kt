package x.x.p455w0rd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import x.x.p455w0rd.theme.ComposePasswordTheme
import x.x.p455w0rd.ui.compose.MainUI
import x.x.p455w0rd.ui.snack.AppSnackbarHost
import x.x.p455w0rd.ui.snack.LocalSnackController
import x.x.p455w0rd.ui.snack.createSnackController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposePasswordTheme {
                val hostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val snack = remember(scope, hostState) { createSnackController(scope, hostState) }

                CompositionLocalProvider(LocalSnackController provides snack) {
                    Scaffold(
                        snackbarHost = { AppSnackbarHost(hostState) }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            MainUI()
                        }
                    }
                }
            }
        }
    }
}