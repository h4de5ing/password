package com.password.shared

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.password.shared.db.DatabaseFactory
import com.password.shared.theme.AppTheme
import com.password.shared.ui.screens.MainUI
import org.jetbrains.compose.resources.painterResource
import password.shared.generated.resources.Res
import password.shared.generated.resources.desktop_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "密码本",
        icon = painterResource(Res.drawable.desktop_icon),
    ) {
        val db = remember { DatabaseFactory.create() }
        val dao = remember(db) { db.roomDao() }
        AppTheme { MainUI(dao) }
    }
}