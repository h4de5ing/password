package x.x.p455w0rd.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PasswordItem::class], version = 3, exportSchema = false)
abstract class RoomDBDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDBDatabase? = null
        fun create(context: Context): RoomDBDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) 旧表改名
                db.execSQL("ALTER TABLE password RENAME TO password_old")

                // 2) 新表（移除 title 列）
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS password (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        type INTEGER NOT NULL,
                        account TEXT NOT NULL,
                        password TEXT NOT NULL,
                        memoInfo TEXT NOT NULL,
                        time INTEGER NOT NULL,
                        dataJson TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                // 3) 将旧数据写入新表：不再保留 title（按需求 A 直接丢弃）
                val cursor =
                    db.query("SELECT id,type,account,password,memoInfo,time,dataJson FROM password_old")
                val updates = ArrayList<String>(cursor.count)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val type = cursor.getInt(1)
                    val account = cursor.getString(2) ?: ""
                    val password = cursor.getString(3) ?: ""
                    val memoInfo = cursor.getString(4) ?: ""
                    val time = cursor.getLong(5)
                    val dataJsonOld = cursor.getString(6) ?: "{}"

                    // 用最简单的方式逃逸单引号，避免 SQL 语法错误
                    fun esc(s: String) = s.replace("'", "''")

                    updates += """
                        INSERT INTO password (id,type,account,password,memoInfo,time,dataJson)
                        VALUES ($id,$type,'${esc(account)}','${esc(password)}','${esc(memoInfo)}',$time,'${
                        esc(
                            dataJsonOld
                        )
                    }')
                    """.trimIndent()
                }
                cursor.close()

                updates.forEach { db.execSQL(it) }

                // 4) 删除旧表
                db.execSQL("DROP TABLE password_old")

                // 5) 还原索引（与实体声明一致）
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_password_id ON password (id)")
            }
        }


        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, RoomDBDatabase::class.java, "data.db")
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_2_3)
                .build()

    }
}