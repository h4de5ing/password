package x.x.p455w0rd.ui.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import x.x.p455w0rd.util.TotpUtils

@Composable
fun DisplayGoogleAuthInfo(dataMap: Map<String, String>) {
    val context = LocalContext.current
    val secret = dataMap["恢复代码"] ?: ""
    var totpCode by remember { mutableStateOf("000000") }
    var countdownSeconds by remember { mutableIntStateOf(30) }

    LaunchedEffect(secret) {
        while (true) {
            val (code, countdown) = TotpUtils.getTotpWithCountdown(secret)
            totpCode = code
            countdownSeconds = countdown
            delay(1000)
        }
    }

    val website = dataMap["网站"] ?: ""

    if (website.isNotEmpty()) {
        Text(
            text = "网站: $website",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    // 显示TOTP验证码
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "验证码",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = totpCode,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("TOTP Code", totpCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "验证码已复制", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "复制验证码",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Text(
            text = "${countdownSeconds}s",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
