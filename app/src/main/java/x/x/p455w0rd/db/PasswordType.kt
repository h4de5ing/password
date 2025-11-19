package x.x.p455w0rd.db

/**
 * 密码类型枚举
 */
enum class PasswordType(val id: Int, val displayName: String, val fields: List<String>) {
    PASSWORD(
        1,
        "密码",
        listOf("标题", "用户名", "密码", "网站", "备注")
    ),
    GOOGLE_AUTH(
        2,
        "谷歌验证码",
        listOf("标题", "网站", "恢复代码", "备注")
    ),
    MNEMONIC(
        3,
        "助记词",
        listOf("标题", "助记词(12或24)", "备注")
    ),
    BANK_CARD(
        4,
        "银行卡",
        listOf("标题", "卡类型", "银行名称", "卡号", "持卡人", "CVV", "有效期", "开户行", "备注")
    ),
    ID_CARD(
        5,
        "身份证",
        listOf("标题", "姓名", "身份证号码", "地址", "备注")
    );

    companion object {
        fun fromId(id: Int): PasswordType? = entries.find { it.id == id }
    }
}
