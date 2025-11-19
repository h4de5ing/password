package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import x.x.p455w0rd.getConciseTime
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            title = passwordItem.title,
            onConfirm = {
                showDeleteDialog = false
                onDelete(passwordItem)
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

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
                    text = passwordItem.time.getConciseTime(context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                IconButton(
                    onClick = { showDeleteDialog = true },
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
    showSecret: Boolean = false,
    onShowSecretChange: (Boolean) -> Unit = {}
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
}

@Composable
private fun DisplayMnemonicInfo(
    dataMap: Map<String, String>,
    showMnemonic: Boolean = false,
    onShowMnemonicChange: (Boolean) -> Unit = {}
) {
    var showWords by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // 计数有多少个单词
    val wordCount = (1..24).count { dataMap["word_$it"]?.isNotEmpty() == true }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "助记词: ${wordCount}个单词${if (wordCount == 12) " (12字)" else if (wordCount == 24) " (24字)" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 显示/隐藏单词图标
            IconButton(
                onClick = { showWords = !showWords },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (showWords) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showWords) "隐藏单词" else "显示单词",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // 展开/折叠矩阵图标
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "折叠矩阵" else "展开矩阵",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // 只在展开状态下显示矩阵
    if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))

        // 矩阵显示单词：12个单词显示为3X4，24个单词显示为3X8
        val columnsCount = 3
        val rowsCount = (wordCount + columnsCount - 1) / columnsCount
        
        for (row in 0 until rowsCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columnsCount) {
                    val index = row * columnsCount + col + 1
                    val word = if (index <= wordCount) dataMap["word_$index"] ?: "" else ""
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (word.isNotEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = index.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                // 显示单词或星号
                                Text(
                                    text = if (showWords) word else "●",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
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

@Composable
private fun DeleteConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("确认删除")
        },
        text = {
            Text("确定要删除 \"$title\" 吗？此操作无法撤销。")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}