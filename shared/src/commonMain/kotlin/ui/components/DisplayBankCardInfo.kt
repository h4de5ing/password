package com.password.shared.ui.components

import com.password.shared.util.ClipboardUtil
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
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
    dataMap: Map<String, String>, showCardNumber: Boolean, onShowCardNumberChange: (Boolean) -> Unit,
    onShowMessage: (String) -> Unit = {}
) {
    val cardType = dataMap["卡类型"] ?: "信用卡"
    val bankName = dataMap["银行名称"] ?: ""
    val cardNumber = dataMap["卡号"] ?: ""
    val cardholder = dataMap["持卡人"] ?: ""
    val cvv = dataMap["CVV"] ?: ""
    val expiry = dataMap["有效期"] ?: ""
    val openBank = dataMap["开户行"] ?: ""

    Text(
        text = "卡类型: $cardType",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "银行: $bankName", style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(4.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        val maskedCardNumber = if (showCardNumber) {
            cardNumber
        } else {
            val lastFour = cardNumber.takeLast(4).padStart(4, '•')
            "**** **** **** $lastFour"
        }

        Text(
            text = "卡号: $maskedCardNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (cardType != "信用卡") {
            IconButton(
                onClick = {
                    val text = listOf(bankName, cardNumber, cardholder, openBank).joinToString("，")
                    ClipboardUtil.copyToClipboard(text)
                    onShowMessage("已复制")
                }, modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy, contentDescription = "复制银行卡信息"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        IconButton(
            onClick = { onShowCardNumberChange(!showCardNumber) }, modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = if (showCardNumber) Icons.Default.VisibilityOff
                else Icons.Default.Visibility,
                contentDescription = if (showCardNumber) "隐藏银行卡信息" else "显示银行卡信息"
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "持卡人: ${
            if (showCardNumber) cardholder else "•".repeat(
                cardholder.length.coerceAtLeast(
                    2
                )
            )
        }", style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(4.dp))
    if (cardType == "信用卡") {
        Text(
            text = "CVV: ${if (showCardNumber) cvv else "***"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
    Text(
        text = "有效期: ${if (showCardNumber) expiry else "**/**"}",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "开户行: ${
            if (showCardNumber) openBank else "•".repeat(
                openBank.length.coerceAtLeast(
                    4
                )
            )
        }", style = MaterialTheme.typography.bodyMedium
    )
}
