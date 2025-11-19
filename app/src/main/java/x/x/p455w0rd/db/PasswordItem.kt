package x.x.p455w0rd.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json

@Entity(tableName = "password", indices = [Index(value = ["id"], unique = true)])
data class PasswordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var type: Int = 1,
    var title: String = "",
    var account: String = "",
    var password: String = "",
    var memoInfo: String = "",
    var time: Long = System.currentTimeMillis(),
    var dataJson: String = "{}" // 存储不同类型的数据
) {
    fun getPasswordType(): PasswordType {
        return PasswordType.fromId(type) ?: PasswordType.PASSWORD
    }

    fun getDataMap(): Map<String, String> {
        return try {
            @Suppress("UNCHECKED_CAST")
            Json.decodeFromString<Map<String, String>>(dataJson)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun setDataMap(data: Map<String, String>) {
        dataJson = Json.encodeToString(data)
    }
}

// 数据存储格式
typealias PasswordDataMap = Map<String, String>