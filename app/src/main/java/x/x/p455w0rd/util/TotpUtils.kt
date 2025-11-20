package x.x.p455w0rd.util

import java.util.Locale.getDefault
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * TOTP/HOTP验证码生成工具 - Google Authenticator 兼容实现
 * 基于RFC 4226 (HOTP) 和 RFC 6238 (TOTP) 标准
 *
 * Google Authenticator 核心功能:
 * 1. 扫描二维码获取 Base32 编码的密钥
 * 2. 每 30 秒生成一个 6 位数字验证码
 * 3. 基于时间的一次性密码（TOTP）算法
 * 4. 支持多个账户的多个验证器
 * 5. 支持验证码验证（前后时间窗口容错）
 */
object TotpUtils {

    // ========== 常量定义 ==========
    /** TOTP 验证码位数 */
    private const val DIGITS = 6

    /** TOTP 时间步长（秒）- Google Authenticator 标准为 30 秒 */
    private const val TIME_STEP = 30L

    /** HMAC 算法 - RFC 4226 推荐使用 HmacSHA1 */
    private const val HMAC_ALGORITHM = "HmacSHA1"

    /** Base32 字母表 - RFC 4648 标准 */
    private const val BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    /** 默认时间偏差窗口 - 允许前后各一个时间步长的误差 */
    private const val DEFAULT_WINDOW_SIZE = 1

    /** 无效验证码返回值 */
    private const val INVALID_CODE = "000000"

    /** 默认倒计时值 */
    private const val DEFAULT_COUNTDOWN = 30

    // ========== 主要公开接口 ==========

    /**
     * 生成当前的 TOTP 验证码
     * 这是 Google Authenticator 的核心功能
     *
     * @param secret Base32 编码的密钥（来自扫描二维码）
     * @return 6 位验证码字符串
     *
     * 使用示例:
     * ```
     * val secret = "JBSWY3DPEBLW64TMMQ=====" // 从二维码获取
     * val code = TotpUtils.generateTotpCode(secret)
     * // code = "123456"
     * ```
     */
    fun generateTotpCode(secret: String): String {
        if (secret.isBlank()) return INVALID_CODE

        return try {
            val key = base32Decode(secret)
            if (key.isEmpty()) return INVALID_CODE

            val timeCounter = System.currentTimeMillis() / 1000 / TIME_STEP
            generateHotp(key, timeCounter)
        } catch (_: Exception) {
            INVALID_CODE
        }
    }

    /**
     * 获取当前 TOTP 验证码及其剩余有效时间
     * 用于 UI 显示验证码和进度条
     *
     * @param secret Base32 编码的密钥
     * @return Pair<验证码, 剩余秒数(0-30)>
     *
     * 使用示例:
     * ```
     * val (code, countdown) = TotpUtils.getTotpWithCountdown(secret)
     * // code = "123456", countdown = 15
     * // UI 可以基于 countdown 显示进度条
     * ```
     */
    fun getTotpWithCountdown(secret: String): Pair<String, Int> {
        if (secret.isBlank()) return Pair(INVALID_CODE, DEFAULT_COUNTDOWN)

        return try {
            val code = generateTotpCode(secret)
            val currentTime = System.currentTimeMillis() / 1000
            val timeRemaining = (TIME_STEP - (currentTime % TIME_STEP)).toInt()
            Pair(code, timeRemaining)
        } catch (_: Exception) {
            Pair(INVALID_CODE, DEFAULT_COUNTDOWN)
        }
    }

    /**
     * 验证用户输入的验证码是否正确
     * 用于登录时验证用户输入的验证码
     *
     * @param secret Base32 编码的密钥
     * @param code 用户输入的 6 位验证码
     * @param windowSize 时间窗口大小
     *   - 0: 仅验证当前时间步长的码
     *   - 1: 验证当前及前后各一个时间步长的码（容错 ±30 秒）
     *   - 2: 验证当前及前后各两个时间步长的码（容错 ±60 秒）
     * @return true 验证成功，false 验证失败
     *
     * 使用示例:
     * ```
     * val isValid = TotpUtils.verifyTotp(secret, "123456")
     * if (isValid) {
     *     // 验证成功，允许用户登录
     * } else {
     *     // 验证失败，提示用户重试
     * }
     * ```
     *
     * Google Authenticator 验证流程:
     * 1. 用户扫描二维码，获取密钥
     * 2. Google Authenticator 每 30 秒生成一个验证码
     * 3. 用户在登录时手动输入当前显示的验证码
     * 4. 服务器使用此函数验证码是否正确
     * 5. 允许 ±1 个时间步长的偏差，以处理网络延迟和时钟偏差
     */
    fun verifyTotp(secret: String, code: String, windowSize: Int = DEFAULT_WINDOW_SIZE): Boolean {
        if (secret.isBlank() || code.length != DIGITS || !code.all { it.isDigit() }) return false
        return try {
            val key = base32Decode(secret)
            if (key.isEmpty()) return false
            val timeCounter = System.currentTimeMillis() / 1000 / TIME_STEP
            // 在时间窗口内检查验证码（容错机制）
            for (offset in -windowSize..windowSize) {
                val hotp = generateHotp(key, timeCounter + offset)
                if (hotp == code) return true
            }
            false
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 获取下次验证码的剩余时间（秒）
     * 用于判断是否需要刷新 UI
     *
     * @return 剩余时间（秒），范围 1-30
     */
    fun getTimeUntilNextCode(): Int {
        val currentTime = System.currentTimeMillis() / 1000
        val timeRemaining = (TIME_STEP - (currentTime % TIME_STEP)).toInt()
        return if (timeRemaining > 0) timeRemaining else TIME_STEP.toInt()
    }

    // ========== 内部算法实现 ==========

    /**
     * Base32 解码 - 将 Base32 编码的字符串转换为字节数组
     *
     * Base32 编码是 RFC 4648 标准，Google Authenticator 使用此格式
     * 例如: "JBSWY3DPEBLW64TMMQ=====" 解码后得到密钥字节数组
     *
     * @param encoded Base32 编码的字符串
     * @return 解码后的字节数组
     */
    private fun base32Decode(encoded: String): ByteArray {
        val input = encoded.uppercase(getDefault()).replace("[^A-Z2-7]".toRegex(), "")
        if (input.isEmpty()) return ByteArray(0)

        val output = ByteArray((input.length * 5) / 8)
        var bitBuffer = 0
        var bufferLength = 0
        var outputIndex = 0

        for (char in input) {
            val value = BASE32_ALPHABET.indexOf(char)
            if (value == -1) continue

            bitBuffer = (bitBuffer shl 5) or value
            bufferLength += 5

            if (bufferLength >= 8) {
                bufferLength -= 8
                output[outputIndex++] = (bitBuffer shr bufferLength).toByte()
                bitBuffer = bitBuffer and ((1 shl bufferLength) - 1)
            }
        }

        return output.take(outputIndex).toByteArray()
    }

    /**
     * 生成 HOTP 验证码 - RFC 4226 标准实现
     *
     * HOTP 是基于计数器的一次性密码算法
     * 步骤:
     * 1. 将计数器转换为 8 字节大端序数组
     * 2. 使用 HMAC-SHA1 生成哈希值
     * 3. 进行动态截取（Dynamic Truncation）
     * 4. 提取最后 6 位数字
     *
     * @param key 密钥字节数组
     * @param counter 计数器值（对于 TOTP，计数器 = 当前时间 / 30）
     * @return 6 位 HOTP 验证码
     */
    private fun generateHotp(key: ByteArray, counter: Long): String {
        return try {
            // 步骤1: 将计数器转换为 8 字节数组（大端序）
            val counterBytes = ByteArray(8)
            var temp = counter
            for (i in 7 downTo 0) {
                counterBytes[i] = (temp and 0xFF).toByte()
                temp = temp shr 8
            }

            // 步骤2: 使用 HMAC-SHA1 生成哈希
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            val secretKey = SecretKeySpec(key, 0, key.size, HMAC_ALGORITHM)
            mac.init(secretKey)
            val hash = mac.doFinal(counterBytes)

            // 步骤3: 动态截取（Dynamic Truncation）- RFC 4226 第 5.3 节
            // 使用最后一个字节的最低 4 位作为偏移量
            val offset = hash[hash.size - 1].toInt() and 0x0f

            // 从偏移量处取 4 个字节，第一个字节的最高位清零
            val truncatedHash = ((hash[offset].toInt() and 0x7f) shl 24) or
                    ((hash[offset + 1].toInt() and 0xff) shl 16) or
                    ((hash[offset + 2].toInt() and 0xff) shl 8) or
                    (hash[offset + 3].toInt() and 0xff)

            // 步骤4: 提取最后 6 位数字（模 10^6）
            val otp = truncatedHash % 1000000
            otp.toString().padStart(DIGITS, '0')
        } catch (_: Exception) {
            INVALID_CODE
        }
    }
}
