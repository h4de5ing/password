package com.password.shared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.password.shared.db.DatabaseFactory
import com.password.shared.db.RoomDao
import com.password.shared.theme.AppTheme
import com.password.shared.ui.screens.MainUI
import com.password.shared.util.ClipboardUtil

class MainActivity : ComponentActivity() {
    private lateinit var dao: RoomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 初始化平台特定的工具类
        DatabaseFactory.init(this)
        ClipboardUtil.init(this)
        // 创建数据库和 DAO
        val db = DatabaseFactory.create()
        dao = db.roomDao()
        setContent { AppTheme { MainUI(dao) } }
    }
}
