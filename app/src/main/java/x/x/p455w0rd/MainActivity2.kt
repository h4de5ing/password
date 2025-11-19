package x.x.p455w0rd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import x.x.p455w0rd.theme.ComposePasswordTheme
import x.x.p455w0rd.ui.compose.MainUI

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ComposePasswordTheme { MainUI() } }
    }
}