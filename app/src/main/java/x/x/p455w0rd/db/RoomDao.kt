package x.x.p455w0rd.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM password order by id")
    fun observerPasswordItem(): Flow<List<PasswordItem>>

    @Query("SELECT * FROM password order by id")
    fun output(): List<PasswordItem>

    @Query("SELECT * FROM password where id=(:id)")
    fun observerItemId(id: Long): MutableList<PasswordItem>

    @Insert
    fun insert(vararg item: PasswordItem)

    @Update
    fun update(item: PasswordItem)

    @Delete
    fun delete(item: PasswordItem)
}