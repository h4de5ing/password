package com.password.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.password.shared.db.DatabaseFactory
import com.password.shared.theme.AppTheme
import com.password.shared.ui.components.AppSnackbarHost
import com.password.shared.ui.components.LocalSnackController
import com.password.shared.ui.components.createSnackController
import com.password.shared.ui.screens.MainScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "密码本"
    ) {
        // 创建数据库和 DAO
        val db = DatabaseFactory.create()
        val dao = db.roomDao()

        AppTheme {
            val hostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val snack = remember(scope, hostState) { createSnackController(scope, hostState) }

            CompositionLocalProvider(LocalSnackController provides snack) {
                Scaffold(
                    snackbarHost = { AppSnackbarHost(hostState) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        MainScreen(
                            passwordListFlow = dao.observerPasswordItem(),
                            onAddClick = {
                                scope.launch(Dispatchers.IO) {
                                    // TODO: 添加密码对话框
                                    snack.show("添加密码功能待实现")
                                }
                            },
                            onItemClick = { item ->
                                // TODO: 显示密码详情
                                snack.show("查看 ${item.getPasswordType().displayName}")
                            },
                            onEdit = { item ->
                                // TODO: 编辑密码对话框
                                snack.show("编辑 ${item.getPasswordType().displayName}")
                            },
                            onDelete = { item ->
                                scope.launch(Dispatchers.IO) {
                                    dao.delete(item)
                                    snack.show("已删除")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
