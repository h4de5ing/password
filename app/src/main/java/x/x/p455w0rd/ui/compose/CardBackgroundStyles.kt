package x.x.p455w0rd.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import x.x.p455w0rd.db.PasswordType

/**
 * 卡片背景样式定义
 * 为不同类型的密码卡片设计独特的背景样式
 */
object CardBackgroundStyles {

    /**
     * 获取对应密码类型的背景样式
     */
    @Composable
    fun getBackgroundModifier(passwordType: PasswordType): Modifier {
        return when (passwordType) {
            PasswordType.PASSWORD -> passwordBackground()
            PasswordType.GOOGLE_AUTH -> googleAuthBackground()
            PasswordType.MNEMONIC -> mnemonicBackground()
            PasswordType.BANK_CARD -> bankCardBackground()
            PasswordType.ID_CARD -> idCardBackground()
        }
    }

    /**
     * 密码类型背景 - 深蓝色渐变 + 锁形装饰
     * 🔐 象征：安全、私密、保护
     */
    @Composable
    private fun passwordBackground(): Modifier {
        val gradientColors = listOf(
            Color(0xFF1e3a8a),  // 深蓝色
            Color(0xFF3b82f6)   // 蓝色
        )
        return Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    }

    /**
     * 谷歌验证码类型背景 - 橙绿色渐变 + Google 风格
     * 🔐 象征：Google、验证、实时更新
     */
    @Composable
    private fun googleAuthBackground(): Modifier {
        val gradientColors = listOf(
            Color(0xFFEA4335),  // Google 红
            Color(0xFFFF9500)    // 橙色
        )
        return Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    }

    /**
     * 助记词类型背景 - 紫色渐变 + 神秘感
     * ✨ 象征：加密、复杂、重要、回忆
     */
    @Composable
    private fun mnemonicBackground(): Modifier {
        val gradientColors = listOf(
            Color(0xFF7c3aed),  // 紫色
            Color(0xFFa855f7)   // 浅紫色
        )
        return Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    }

    /**
     * 银行卡类型背景 - 金色渐变 + 奢华感
     * 💳 象征：金融、价值、信任、金钱
     */
    @Composable
    private fun bankCardBackground(): Modifier {
        val gradientColors = listOf(
            Color(0xFF92400e),  // 深棕色
            Color(0xFFd97706)   // 金色
        )
        return Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    }

    /**
     * 身份证类型背景 - 红色渐变 + 正式感
     * 🪪 象征：身份、官方、正式、重要
     */
    @Composable
    private fun idCardBackground(): Modifier {
        val gradientColors = listOf(
            Color(0xFF7f1d1d),  // 深红色
            Color(0xFFdc2626)   // 红色
        )
        return Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    }

    /**
     * 通用卡片容器 - 用于应用背景样式
     */
    @Composable
    fun StyledCardContainer(
        passwordType: PasswordType,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            // 背景样式层
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(getBackgroundModifier(passwordType))
                    .background(Color.Transparent)
            )

            // 内容层
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                content()
            }
        }
    }
}

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
