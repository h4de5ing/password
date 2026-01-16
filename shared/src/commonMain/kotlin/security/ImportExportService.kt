package com.password.shared.security

import com.password.shared.db.PasswordItem
import com.password.shared.db.PasswordType
import com.password.shared.db.RoomDao
import kotlinx.serialization.json.Json

object ImportExportService {

    suspend fun exportJson(dao: RoomDao): String {
        val items = dao.output().map {
            val dataMap = it.getDataMap()
            val account = when (PasswordType.fromId(it.type) ?: PasswordType.PASSWORD) {
                PasswordType.PASSWORD -> dataMap["用户名"].orEmpty()
                PasswordType.GOOGLE_AUTH -> dataMap["网站"].orEmpty()
                PasswordType.MNEMONIC -> dataMap["word_1"].orEmpty()
                PasswordType.BANK_CARD -> dataMap["卡号"].orEmpty()
                PasswordType.ID_CARD -> dataMap["姓名"].orEmpty()
            }
            val password = dataMap["密码"].orEmpty()
            ExportItem(
                id = it.id,
                type = it.type,
                account = account,
                password = password,
                memoInfo = it.memoInfo,
                time = it.time,
                dataJson = it.dataJson
            )
        }
        return Json.encodeToString(ExportPayload(items = items))
    }

    /**
     * 导入策略：
     * - 当前实现：直接全部插入为新记录（忽略 ExportItem.id），避免跨设备 id 冲突。
     */
    suspend fun importJson(dao: RoomDao, json: String) {
        val payload = Json.decodeFromString<ExportPayload>(json)
        payload.items.forEach { e ->
            val dataJson = if (e.dataJson.isNotBlank() && e.dataJson != "{}") {
                e.dataJson
            } else {
                val map = buildMap {
                    when (PasswordType.fromId(e.type) ?: PasswordType.PASSWORD) {
                        PasswordType.PASSWORD -> {
                            if (e.account.isNotBlank()) put("用户名", e.account)
                            if (e.password.isNotBlank()) put("密码", e.password)
                        }

                        PasswordType.GOOGLE_AUTH -> {
                            if (e.account.isNotBlank()) put("网站", e.account)
                        }

                        PasswordType.MNEMONIC -> {
                            if (e.account.isNotBlank()) put("word_1", e.account)
                        }

                        PasswordType.BANK_CARD -> {
                            if (e.account.isNotBlank()) put("卡号", e.account)
                        }

                        PasswordType.ID_CARD -> {
                            if (e.account.isNotBlank()) put("姓名", e.account)
                        }
                    }
                }
                Json.encodeToString(map)
            }
            val item = PasswordItem(
                id = 0,
                type = e.type,
                memoInfo = e.memoInfo,
                time = if (e.time == 0L) System.currentTimeMillis() else e.time,
                dataJson = dataJson
            )
            dao.insert(item)
        }
    }
}
