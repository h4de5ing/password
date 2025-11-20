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

    if (name.isNotEmpty()) {
        Text(
            text = "姓名: $name",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (idNumber.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "身份证号: ${
                    if (showIdNumber) idNumber else {
                        val lastThree = idNumber.takeLast(3)
                        "*".repeat(idNumber.length - 3) + lastThree
                    }
                }",
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
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (address.isNotEmpty()) {
        Text(
            text = "地址: ${address.take(20)}${if (address.length > 20) "..." else ""}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
