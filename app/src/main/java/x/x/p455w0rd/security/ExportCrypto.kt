package x.x.p455w0rd.security

import android.util.Base64
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 跨设备导入/导出用口令加密：PBKDF2(HmacSHA256) + AES-256-GCM。
 *
 * 文件内容使用 compact 字符串格式：
 * v=1;salt=...;iv=...;ct=...
 */
object ExportCrypto {
    private const val VERSION = 1

    private const val SALT_LEN = 16
    private const val IV_LEN = 12
    private const val KEY_LEN_BITS = 256

    // 兼顾安全与性能（minSdk 26，PBKDF2 性能可接受）
    private const val PBKDF2_ITERATIONS = 200_000

    private val rng = SecureRandom()

    @Throws(IllegalArgumentException::class)
    fun encryptJsonWithPassword(json: String, password: CharArray): String {
        require(password.isNotEmpty()) { "password is empty" }

        val salt = ByteArray(SALT_LEN).also { rng.nextBytes(it) }
        val iv = ByteArray(IV_LEN).also { rng.nextBytes(it) }
        val key = deriveAesKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val ciphertext = cipher.doFinal(json.toByteArray(Charsets.UTF_8))

        return "v=$VERSION;salt=${b64(salt)};iv=${b64(iv)};ct=${b64(ciphertext)}"
    }

    /**
     * 口令错误 / 文件被篡改会抛出 [GeneralSecurityException]。
     */
    @Throws(GeneralSecurityException::class, IllegalArgumentException::class)
    fun decryptJsonWithPassword(compact: String, password: CharArray): String {
        require(password.isNotEmpty()) { "password is empty" }

        val parts = compact.split(';')
            .filter { it.isNotBlank() }
            .associate { kv ->
                val idx = kv.indexOf('=')
                require(idx > 0) { "Bad format" }
                kv.substring(0, idx) to kv.substring(idx + 1)
            }

        val v = parts["v"]?.toIntOrNull() ?: error("Missing v")
        require(v == VERSION) { "Unsupported version: $v" }

        val salt = b64d(parts["salt"] ?: error("Missing salt"))
        val iv = b64d(parts["iv"] ?: error("Missing iv"))
        val ct = b64d(parts["ct"] ?: error("Missing ct"))
        val key = deriveAesKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        val plaintext = cipher.doFinal(ct)
        return plaintext.toString(Charsets.UTF_8)
    }

    private fun deriveAesKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LEN_BITS)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = skf.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun b64(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun b64d(s: String): ByteArray = Base64.decode(s, Base64.NO_WRAP)
}

