package x.x.p455w0rd.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PasswordItem::class], version = 2, exportSchema = false)
abstract class RoomDBDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDBDatabase? = null
        fun create(context: Context): RoomDBDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, RoomDBDatabase::class.java, "data.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

    }
}