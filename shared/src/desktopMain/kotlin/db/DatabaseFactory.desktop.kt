package com.password.shared.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual object DatabaseFactory {
    actual fun create(): AppDatabase {
        val dbFile = File("data", "data.db")
        dbFile.parentFile?.mkdirs()
        return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
