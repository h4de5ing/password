package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DisplayPasswordInfo(
    dataMap: Map<String, String>, showPassword: Boolean, onShowPasswordChange: (Boolean) -> Unit
) {
    val username = dataMap["用户名"] ?: ""
    val password = dataMap["密码"] ?: ""
    val website = dataMap["网站"] ?: ""
    Text(
        text = "用户名: ${if (showPassword) username else "•".repeat(username.length.coerceAtLeast(3))}",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "密码: ${
                if (showPassword) password
                else "•".repeat(password.length.coerceAtLeast(3))
            }", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onShowPasswordChange(!showPassword) }, modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = if (showPassword) Icons.Default.VisibilityOff
                else Icons.Default.Visibility,
                contentDescription = if (showPassword) "隐藏信息" else "显示信息"
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "网站: $website", style = MaterialTheme.typography.bodyMedium
    )
}
