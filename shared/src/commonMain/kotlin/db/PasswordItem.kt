package com.password.shared.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json

@Entity(tableName = "password", indices = [Index(value = ["id"], unique = true)])
data class PasswordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var type: Int = 1,
    var account: String = "",
    var password: String = "",
    var memoInfo: String = "",
    var time: Long = System.currentTimeMillis(),
    var dataJson: String = "{}"
) {
    fun getPasswordType(): PasswordType {
        return PasswordType.fromId(type) ?: PasswordType.PASSWORD
    }

    fun getDataMap(): Map<String, String> {
        return try {
            @Suppress("UNCHECKED_CAST")
            Json.decodeFromString<Map<String, String>>(dataJson)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun setDataMap(data: Map<String, String>) {
        dataJson = Json.encodeToString(data)
    }
}
