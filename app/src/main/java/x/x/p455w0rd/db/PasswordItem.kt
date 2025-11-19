package x.x.p455w0rd.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "password", indices = [Index(value = ["id"], unique = true)])
data class PasswordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var type: Int,
    var title: String,
    var account: String,
    var password: String,
    var memoInfo: String,
    var time: Long= System.currentTimeMillis()
)