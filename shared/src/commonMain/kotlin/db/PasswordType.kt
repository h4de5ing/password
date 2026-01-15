package com.password.shared.db

/**
 * 密码类型枚举
 */
enum class PasswordType(val id: Int, val displayName: String) {
    PASSWORD(1, "密码"),
    GOOGLE_AUTH(2, "谷歌验证码"),
    MNEMONIC(3, "助记词"),
    BANK_CARD(4, "银行卡"),
    ID_CARD(5, "身份证");

    companion object {
        fun fromId(id: Int): PasswordType? = entries.find { it.id == id }
    }
}
