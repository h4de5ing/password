package com.password.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.password.shared.db.PasswordType

/**
 * 背景样式颜色映射表
 */
@Composable
fun getCardBackgroundColor(passwordType: PasswordType): Color {
    return when (passwordType) {
        PasswordType.PASSWORD -> Color(0xFF3b82f6)          // 蓝色
        PasswordType.GOOGLE_AUTH -> Color(0xFFFF9500)       // 橙色
        PasswordType.MNEMONIC -> Color(0xFFa855f7)          // 紫色
        PasswordType.BANK_CARD -> Color(0xFFd97706)         // 金色
        PasswordType.ID_CARD -> Color(0xFFdc2626)           // 红色
    }
}

/**
 * 获取类型对应的渐变色对
 */
@Composable
fun getCardGradientColors(passwordType: PasswordType): List<Color> {
    return when (passwordType) {
        PasswordType.PASSWORD -> listOf(Color(0xFF1e3a8a), Color(0xFF3b82f6))
        PasswordType.GOOGLE_AUTH -> listOf(Color(0xFFEA4335), Color(0xFFFF9500))
        PasswordType.MNEMONIC -> listOf(Color(0xFF7c3aed), Color(0xFFa855f7))
        PasswordType.BANK_CARD -> listOf(Color(0xFF92400e), Color(0xFFd97706))
        PasswordType.ID_CARD -> listOf(Color(0xFF7f1d1d), Color(0xFFdc2626))
    }
}
