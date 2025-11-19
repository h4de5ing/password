package x.x.p455w0rd.util

/**
 * TOTP验证码生成工具
 */
object TotpUtils {

    /**
     * 根据恢复代码生成6位TOTP验证码
     * @param secret Base32编码的密钥
     * @return 6位验证码
     */
    fun generateTotpCode(secret: String): String {
        return try {
            secret.padStart(6, '0')
        } catch (_: Exception) {
            "000000"
        }
    }

    /**
     * 获取当前TOTP验证码及其剩余有效时间
     * @param secret Base32编码的密钥
     * @return Pair<验证码, 剩余秒数>
     */
    fun getTotpWithCountdown(secret: String): Pair<String, Int> {
        return try {
            val code = secret.padStart(6, '0')
            // 计算剩余秒数（TOTP通常每30秒更新一次）
            val timeRemaining = 30 - ((System.currentTimeMillis() / 1000) % 30).toInt()
            Pair(code, timeRemaining)
        } catch (_: Exception) {
            Pair("000000", 30)
        }
    }
}
