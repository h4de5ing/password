package com.password.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PasswordItem::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}

// 数据库构造器 - 平台特定实现
expect object DatabaseFactory {
    fun create(): AppDatabase
}