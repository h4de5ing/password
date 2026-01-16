package com.password.shared.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.password.shared.db.PasswordItem
import com.password.shared.db.RoomDao
import com.password.shared.db.TestDataSeeder
import com.password.shared.platform.rememberPlatformFileAccess
import com.password.shared.security.ExportCrypto
import com.password.shared.security.ImportExportService
import com.password.shared.ui.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainUI(dao: RoomDao) {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snack = remember(scope, hostState) { createSnackController(scope, hostState) }
    val fileAccess = rememberPlatformFileAccess()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPasswordItem by remember { mutableStateOf<PasswordItem?>(null) }

    var showTopMenu by remember { mutableStateOf(false) }
    var showExportPwdDialog by remember { mutableStateOf(false) }
    var showImportPwdDialog by remember { mutableStateOf(false) }

    var pendingImportEncryptedText by remember { mutableStateOf<String?>(null) }

    fun showMsg(msg: String) {
        snack.show(msg)
    }

    CompositionLocalProvider(LocalSnackController provides snack) {
//        LaunchedEffect(Unit) { TestDataSeeder.seed(dao, perTypeCount = 3) }
        MainScreen(
            passwordListFlow = dao.observerPasswordItem(),
            snackbarHost = { AppSnackbarHost(hostState) },
            topBarActions = {
                IconButton(onClick = { showTopMenu = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "菜单")
                }
                DropdownMenu(
                    expanded = showTopMenu, onDismissRequest = { showTopMenu = false }) {
                    DropdownMenuItem(text = { Text("导出") }, onClick = {
                        showTopMenu = false
                        showExportPwdDialog = true
                    })
                    DropdownMenuItem(text = { Text("导入") }, onClick = {
                        showTopMenu = false
                        scope.launch {
                            val text = fileAccess.importText()
                            if (text.isNullOrBlank()) {
                                showMsg("导入已取消")
                            } else {
                                pendingImportEncryptedText = text
                                showImportPwdDialog = true
                            }
                        }
                    })
                }
            },
            onAddClick = {
                editingPasswordItem = null
                showAddDialog = true
            },
            onItemClick = { item ->
                showMsg("查看 ${item.getPasswordType().displayName}")
            },
            onEdit = { item ->
                editingPasswordItem = item
                showAddDialog = true
            },
            onDelete = { item ->
                scope.launch(Dispatchers.IO) {
                    dao.delete(item)
                }
                showMsg("已删除")
            })

        if (showAddDialog) {
            AddPasswordDialog(
                passwordItem = editingPasswordItem,
                onDismiss = {
                    showAddDialog = false
                    editingPasswordItem = null
                },
                onConfirm = { type, dataMap, memo ->
                    val isEditing = editingPasswordItem != null
                    val editingId = editingPasswordItem?.id ?: 0

                    val account = when (type) {
                        1 -> dataMap["用户名"] ?: ""
                        2 -> dataMap["网站"] ?: ""
                        3 -> dataMap["word_1"] ?: ""
                        4 -> dataMap["卡号"] ?: ""
                        5 -> dataMap["姓名"] ?: ""
                        else -> ""
                    }

                    val newItem = PasswordItem(
                        id = editingId,
                        type = type,
                        account = account,
                        password = dataMap["密码"] ?: "",
                        memoInfo = memo,
                        dataJson = "",
                    ).apply {
                        setDataMap(dataMap)
                    }

                    scope.launch(Dispatchers.IO) {
                        if (isEditing) {
                            dao.update(newItem)
                        } else {
                            dao.insert(newItem)
                        }
                    }
                    showMsg(if (isEditing) "已保存" else "已添加")
                },
            )
        }

        if (showExportPwdDialog) {
            PasswordPromptDialog(
                title = "加密导出",
                confirmText = "下一步",
                label = "导出密码",
                onDismiss = { showExportPwdDialog = false },
                onConfirm = { pwd ->
                    showExportPwdDialog = false

                    scope.launch {
                        val encrypted = runCatching {
                            withContext(Dispatchers.IO) {
                                val json = ImportExportService.exportJson(dao)
                                ExportCrypto.encryptJsonWithPassword(json, pwd.toCharArray())
                            }
                        }.getOrElse { e ->
                            showMsg("导出失败：${e.message ?: e::class.simpleName}")
                            return@launch
                        }

                        val ok = fileAccess.exportText(
                            suggestedFileName = "password_backup.p455w0rd", text = encrypted
                        )
                        showMsg(if (ok) "导出成功" else "导出已取消")
                    }
                })
        }

        if (showImportPwdDialog) {
            PasswordPromptDialog(title = "加密导入", confirmText = "导入", label = "导入密码", onDismiss = {
                showImportPwdDialog = false
                pendingImportEncryptedText = null
            }, onConfirm = { pwd ->
                val encrypted = pendingImportEncryptedText
                showImportPwdDialog = false
                pendingImportEncryptedText = null

                if (encrypted.isNullOrBlank()) {
                    showMsg("导入失败：未选择文件")
                    return@PasswordPromptDialog
                }

                scope.launch {
                    val json = runCatching {
                        withContext(Dispatchers.Default) {
                            ExportCrypto.decryptJsonWithPassword(encrypted, pwd.toCharArray())
                        }
                    }.getOrElse { e ->
                        showMsg("导入失败：${e.message ?: e::class.simpleName}")
                        return@launch
                    }

                    runCatching {
                        withContext(Dispatchers.IO) { ImportExportService.importJson(dao, json) }
                    }.onSuccess {
                        showMsg("导入成功")
                    }.onFailure { e ->
                        showMsg("导入失败：${e.message ?: e::class.simpleName}")
                    }
                }
            })
        }
    }
}
