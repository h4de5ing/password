package com.password.shared.ui.components

import com.password.shared.db.PasswordItem
import com.password.shared.db.PasswordType
import com.password.shared.util.getConciseTime
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PasswordItemCard(
    passwordItem: PasswordItem,
    onItemClick: (PasswordItem) -> Unit,
    onEdit: (PasswordItem) -> Unit,
    onDelete: (PasswordItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val passwordType = passwordItem.getPasswordType()
    val dataMap = passwordItem.getDataMap()
    val displayTitle = passwordType.displayName
    var showSensitiveInfo by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            title = displayTitle,
            onConfirm = {
                showDeleteDialog = false
                onDelete(passwordItem)
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onItemClick(passwordItem) },
                    onLongClick = { showMenu = true }
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            // 绘制背景渐变
            val gradientColors = getCardGradientColors(passwordType)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .drawBehind {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = gradientColors,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f)
                            )
                        )
                    }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 类型指示器 - 彩色圆点
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .background(getCardBackgroundColor(passwordType))
                )

                // 类型标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = passwordType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
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
                        DisplayGoogleAuthInfo(dataMap)
                    }

                    PasswordType.MNEMONIC -> {
                        DisplayMnemonicInfo(dataMap)
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
                        text = passwordItem.time.getConciseTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("编辑") },
                onClick = {
                    showMenu = false
                    onEdit(passwordItem)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑"
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("删除") },
                onClick = {
                    showMenu = false
                    showDeleteDialog = true
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除"
                    )
                }
            )
        }
    }
}
