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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DisplayIdCardInfo(
    dataMap: Map<String, String>,
    showIdNumber: Boolean,
    onShowIdNumberChange: (Boolean) -> Unit
) {
    val name = dataMap["姓名"] ?: ""
    val idNumber = dataMap["身份证号码"] ?: ""
    val address = dataMap["地址"] ?: ""

    // 姓名始终显示
    Text(
        text = "姓名: $name",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))

    // 身份证号始终显示（带显隐按钮，同时控制身份证号和地址的显示/隐藏）
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        val masked = if (showIdNumber) idNumber else {
            val keep = idNumber.takeLast(3)
            "•".repeat((idNumber.length - 3).coerceAtLeast(0)) + keep
        }
        Text(
            text = "身份证号: $masked",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onShowIdNumberChange(!showIdNumber) },
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = if (showIdNumber) Icons.Default.VisibilityOff
                else Icons.Default.Visibility,
                contentDescription = if (showIdNumber) "隐藏身份证信息" else "显示身份证信息"
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))

    // 地址始终显示
    val maskedAddress = if (showIdNumber) {
        address
    } else {
        // 地址默认隐藏：不展示明文，给一个固定占位，避免泄露位置信息
        "••••••"
    }
    Text(
        text = "地址: ${maskedAddress.take(20)}${if (maskedAddress.length > 20) "..." else ""}",
        style = MaterialTheme.typography.bodyMedium
    )
}
