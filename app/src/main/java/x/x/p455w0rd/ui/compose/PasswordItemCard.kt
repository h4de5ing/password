package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.db.PasswordType
import x.x.p455w0rd.getConciseTime

@Composable
fun PasswordItemCard(
    passwordItem: PasswordItem,
    onItemClick: (PasswordItem) -> Unit,
    onEdit: (PasswordItem) -> Unit,
    onDelete: (PasswordItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val passwordType = passwordItem.getPasswordType()
    val dataMap = passwordItem.getDataMap()
    var showSensitiveInfo by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        text = passwordItem.time.getConciseTime(context),
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