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
fun DisplayBankCardInfo(
    dataMap: Map<String, String>, showCardNumber: Boolean, onShowCardNumberChange: (Boolean) -> Unit
) {
    val cardType = dataMap["卡类型"] ?: "信用卡"
    val bankName = dataMap["银行名称"] ?: ""
    val cardNumber = dataMap["卡号"] ?: ""
    val cardholder = dataMap["持卡人"] ?: ""
    val cvv = dataMap["CVV"] ?: ""
    val expiry = dataMap["有效期"] ?: ""
    val openBank = dataMap["开户行"] ?: ""

    // 卡类型显示
    Text(
        text = "卡类型: $cardType",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))

    if (bankName.isNotEmpty()) {
        Text(
            text = "银行: $bankName", style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (cardNumber.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "卡号: ${
                    if (showCardNumber) cardNumber else {
                        val lastFour = cardNumber.takeLast(4)
                        "**** **** **** $lastFour"
                    }
                }", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onShowCardNumberChange(!showCardNumber) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = if (showCardNumber) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility, contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (cardholder.isNotEmpty()) {
        Text(
            text = "持卡人: $cardholder", style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    // CVV 只在信用卡时显示
    if (cardType == "信用卡" && cvv.isNotEmpty()) {
        Text(
            text = "CVV: ***", style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (expiry.isNotEmpty()) {
        Text(
            text = "有效期: $expiry", style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (openBank.isNotEmpty()) {
        Text(
            text = "开户行: $openBank", style = MaterialTheme.typography.bodyMedium
        )
    }
}
