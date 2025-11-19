package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import x.x.p455w0rd.TimeUtils
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.db.PasswordType

@Composable
fun PasswordItemCard(
    passwordItem: PasswordItem,
    onItemClick: (PasswordItem) -> Unit,
    onDelete: (PasswordItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val passwordType = passwordItem.getPasswordType()
    val dataMap = passwordItem.getDataMap()
    var showSensitiveInfo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onItemClick(passwordItem) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和类型标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = passwordItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = passwordType.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 根据类型显示内容
            when (passwordType) {
                PasswordType.PASSWORD -> {
                    DisplayPasswordInfo(
                        dataMap,
                        showSensitiveInfo,
                        { showSensitiveInfo = it }
                    )
                }

                PasswordType.GOOGLE_AUTH -> {
                    DisplayGoogleAuthInfo(
                        dataMap,
                        showSensitiveInfo,
                        { showSensitiveInfo = it }
                    )
                }

                PasswordType.MNEMONIC -> {
                    DisplayMnemonicInfo(
                        dataMap,
                        showSensitiveInfo,
                        { showSensitiveInfo = it }
                    )
                }

                PasswordType.BANK_CARD -> {
                    DisplayBankCardInfo(
                        dataMap,
                        showSensitiveInfo,
                        { showSensitiveInfo = it }
                    )
                }

                PasswordType.ID_CARD -> {
                    DisplayIdCardInfo(
                        dataMap,
                        showSensitiveInfo,
                        { showSensitiveInfo = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (passwordItem.memoInfo.isNotBlank()) {
                Text(
                    text = "备注: ${passwordItem.memoInfo}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUtils.getConciseTime(passwordItem.time, context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                IconButton(
                    onClick = { onDelete(passwordItem) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除"
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayPasswordInfo(
    dataMap: Map<String, String>,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit
) {
    val username = dataMap["用户名"] ?: ""
    val password = dataMap["密码"] ?: ""
    val website = dataMap["网站"] ?: ""

    if (username.isNotEmpty()) {
        Text(
            text = "用户名: $username",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (password.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "密码: ${
                    if (showPassword) password
                    else "•".repeat(password.length.coerceAtLeast(3))
                }",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onShowPasswordChange(!showPassword) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = if (showPassword) "隐藏密码" else "显示密码"
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (website.isNotEmpty()) {
        Text(
            text = "网站: $website",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DisplayGoogleAuthInfo(
    dataMap: Map<String, String>,
    showSecret: Boolean,
    onShowSecretChange: (Boolean) -> Unit
) {
    var totpCode by remember { mutableStateOf("000000") }
    var countdownSeconds by remember { mutableIntStateOf(30) }

    // 初始化TOTP代码
//    remember {
//        val (code, countdown) = x.x.p455w0rd.util.TotpUtils.getTotpWithCountdown("")
//        totpCode = code
//        countdownSeconds = countdown
//    }

    val website = dataMap["网站"] ?: ""
    val recoveryCode = dataMap["恢复代码"] ?: ""

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
            Text(
                text = totpCode,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "${countdownSeconds}s",
            style = MaterialTheme.typography.bodySmall
        )
    }
    Spacer(modifier = Modifier.height(4.dp))

    if (recoveryCode.isNotEmpty()) {
        Text(
            text = "恢复代码: ${recoveryCode.take(20)}...",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DisplayMnemonicInfo(
    dataMap: Map<String, String>,
    showMnemonic: Boolean,
    onShowMnemonicChange: (Boolean) -> Unit
) {
    var showMnemonicExpanded by remember { mutableStateOf(false) }

    // 计数有多少个单词
    val wordCount = (1..24).count { dataMap["word_$it"]?.isNotEmpty() == true }

    Text(
        text = "助记词: ${wordCount}个单词${if (wordCount == 12) " (12字)" else if (wordCount == 24) " (24字)" else ""}",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))

    if (showMnemonicExpanded) {
        // 显示所有单词
        for (i in 1..wordCount) {
            val word = dataMap["word_$i"] ?: ""
            if (word.isNotEmpty()) {
                Text(
                    text = "$i. $word",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "点击收起",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        // 显示前几个单词作为预览
        val previewWords = (1..3).mapNotNull { dataMap["word_$it"]?.takeIf { it.isNotEmpty() } }
        Text(
            text = previewWords.joinToString(", ") + if (wordCount > 3) "..." else "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "点击展开",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DisplayBankCardInfo(
    dataMap: Map<String, String>,
    showCardNumber: Boolean,
    onShowCardNumberChange: (Boolean) -> Unit
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
            text = "银行: $bankName",
            style = MaterialTheme.typography.bodyMedium
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
                }",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onShowCardNumberChange(!showCardNumber) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = if (showCardNumber) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (cardholder.isNotEmpty()) {
        Text(
            text = "持卡人: $cardholder",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    // CVV 只在信用卡时显示
    if (cardType == "信用卡" && cvv.isNotEmpty()) {
        Text(
            text = "CVV: ***",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (expiry.isNotEmpty()) {
        Text(
            text = "有效期: $expiry",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (openBank.isNotEmpty()) {
        Text(
            text = "开户行: $openBank",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DisplayIdCardInfo(
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