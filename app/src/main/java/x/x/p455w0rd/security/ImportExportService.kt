package x.x.p455w0rd.security

import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.db.RoomDao

object ImportExportService {

    fun exportJson(dao: RoomDao): String {
        val items = dao.output().map {
            ExportItem(
                id = it.id,
                type = it.type,
                account = it.account,
                password = it.password,
                memoInfo = it.memoInfo,
                time = it.time,
                dataJson = it.dataJson
            )
        }
        return kotlinx.serialization.json.Json.encodeToString(ExportPayload(items = items))
    }

    /**
     * 导入策略：
     * - 当前实现：直接全部插入为新记录（忽略 ExportItem.id），避免跨设备 id 冲突。
     */
    fun importJson(dao: RoomDao, json: String) {
        val payload = kotlinx.serialization.json.Json.decodeFromString<ExportPayload>(json)
        payload.items.forEach { e ->
            val item = PasswordItem(
                id = 0,
                type = e.type,
                account = e.account,
                password = e.password,
                memoInfo = e.memoInfo,
                time = if (e.time == 0L) System.currentTimeMillis() else e.time,
                dataJson = e.dataJson
            )
            dao.insert(item)
        }
    }
}

