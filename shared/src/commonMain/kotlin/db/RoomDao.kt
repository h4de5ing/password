package com.password.shared.db

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
    suspend fun output(): List<PasswordItem>

    @Query("SELECT * FROM password where id=(:id)")
    suspend fun observerItemId(id: Long): List<PasswordItem>

    @Insert
    suspend fun insert(vararg item: PasswordItem)

    @Update
    suspend fun update(item: PasswordItem)

    @Delete
    suspend fun delete(item: PasswordItem)
}
