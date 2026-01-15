package x.x.p455w0rd.db

import kotlin.random.Random

/**
 * 用于调试时快速填充测试数据。
 *
 * 约定：每种 [PasswordType] 随机生成 3 条记录。
 * 注意：这是本地测试数据，不用于生产环境。
 */
object TestDataSeeder {

    fun seed(dao: RoomDao, perTypeCount: Int = 3) {
        // 简单防抖：如果已经有数据，就不重复灌入
        // 如果你希望每次都追加，可以把这段删掉。
        runCatching {
            if (dao.output().isNotEmpty()) return
        }

        PasswordType.entries.forEach { type ->
            repeat(perTypeCount) {
                dao.insert(buildItem(type))
            }
        }
    }

    private fun buildItem(type: PasswordType): PasswordItem {
        val (dataMap, memo) = when (type) {
            PasswordType.PASSWORD -> {
                val username = randomUsername()
                val pwd = randomPassword()
                mapOf(
                    "用户名" to username,
                    "密码" to pwd,
                    "网站" to randomWebsite()
                ) to "自动生成：密码账号"
            }

            PasswordType.GOOGLE_AUTH -> {
                mapOf(
                    "网站" to randomWebsite(),
                    "恢复代码" to randomRecoveryCodes()
                ) to "自动生成：2FA"
            }

            PasswordType.MNEMONIC -> {
                val words = randomMnemonicWords(if (Random.nextBoolean()) 12 else 24)
                // 该类型界面用 word_1..word_n
                val map = buildMap {
                    words.forEachIndexed { idx, w -> put("word_${idx + 1}", w) }
                }
                map to "自动生成：助记词"
            }

            PasswordType.BANK_CARD -> {
                val cardNo = randomCardNumber()
                mapOf(
                    "卡类型" to listOf("信用卡", "借记卡").random(),
                    "银行名称" to listOf("中国银行", "工商银行", "建设银行", "招商银行").random(),
                    "卡号" to cardNo,
                    "持卡人" to randomChineseName(),
                    "CVV" to Random.nextInt(100, 1000).toString(),
                    "有效期" to String.format("%02d/%02d", Random.nextInt(1, 13), Random.nextInt(26, 40)),
                    "开户行" to "${listOf("上海", "北京", "深圳", "杭州").random()}分行"
                ) to "自动生成：银行卡"
            }

            PasswordType.ID_CARD -> {
                mapOf(
                    "姓名" to randomChineseName(),
                    "身份证号码" to randomIdNumber(),
                    "地址" to randomAddress()
                ) to "自动生成：身份证"
            }
        }

        // account 字段用于列表快速识别/搜索（项目现有逻辑）
        val account = when (type) {
            PasswordType.PASSWORD -> dataMap["用户名"].orEmpty()
            PasswordType.GOOGLE_AUTH -> dataMap["网站"].orEmpty()
            PasswordType.MNEMONIC -> dataMap["word_1"].orEmpty()
            PasswordType.BANK_CARD -> dataMap["卡号"].orEmpty()
            PasswordType.ID_CARD -> dataMap["姓名"].orEmpty()
        }

        return PasswordItem(
            id = 0,
            type = type.id,
            account = account,
            password = dataMap["密码"].orEmpty(),
            memoInfo = memo,
            dataJson = "{}"
        ).apply {
            setDataMap(dataMap)
        }
    }

    private fun randomWebsite(): String =
        listOf("github.com", "google.com", "openai.com", "example.com", "bank.example").random()

    private fun randomUsername(): String =
        listOf("alice", "bob", "charlie", "dev", "user").random() + Random.nextInt(10, 99)

    private fun randomPassword(): String {
        val chars = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "!@#%^&*").toCharArray()
        return buildString {
            repeat(Random.nextInt(10, 16)) {
                append(chars[Random.nextInt(chars.size)])
            }
        }
    }

    private fun randomRecoveryCodes(): String =
        (1..8).joinToString("\n") { Random.nextInt(10000000, 99999999).toString() }

    private fun randomMnemonicWords(count: Int): List<String> {
        // 简易假词库（避免引入额外依赖）
        val pool = listOf(
            "apple", "river", "stone", "cloud", "paper", "green", "night", "ocean",
            "dawn", "light", "sound", "earth", "wind", "metal", "salt", "ember",
            "mouse", "bridge", "forest", "dream", "candle", "silver", "gold", "mountain"
        )
        return List(count) { pool.random() }
    }

    private fun randomCardNumber(): String {
        // 16 位
        return (1..16).joinToString("") { Random.nextInt(0, 10).toString() }
    }

    private fun randomChineseName(): String {
        val family = listOf("张", "李", "王", "赵", "陈", "刘", "杨", "黄").random()
        val given = listOf("伟", "芳", "娜", "敏", "静", "磊", "强", "洋", "婷", "杰").random() +
                listOf("", "", "", "华", "明", "丽").random()
        return family + given
    }

    private fun randomIdNumber(): String {
        // 非真实规则，仅生成看起来像 18 位
        val prefix = listOf("110101", "310101", "440301", "330106").random()
        val year = Random.nextInt(1970, 2005)
        val month = Random.nextInt(1, 13)
        val day = Random.nextInt(1, 29)
        val seq = Random.nextInt(100, 999)
        val last = Random.nextInt(0, 10)
        return String.format("%s%04d%02d%02d%03d%d", prefix, year, month, day, seq, last)
    }

    private fun randomAddress(): String {
        val city = listOf("上海", "北京", "深圳", "杭州", "成都", "武汉").random()
        val road = listOf("人民路", "中山路", "解放路", "科技大道", "和平街").random()
        val no = Random.nextInt(1, 999)
        return "$city$road${no}号"
    }
}
