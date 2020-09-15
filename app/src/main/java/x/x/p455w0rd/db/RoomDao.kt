package x.x.p455w0rd.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoomDao {
    @Query("SELECT * FROM password order by id")
    fun observerPasswordItem(): LiveData<MutableList<PasswordItem>>

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