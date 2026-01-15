package com.password.shared.db

import android.content.Context
import androidx.room.Room

actual object DatabaseFactory {
    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    actual fun create(): AppDatabase {
        val ctx = context ?: throw IllegalStateException("DatabaseFactory not initialized")
        return Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "data.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
