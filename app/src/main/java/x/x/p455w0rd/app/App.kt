package x.x.p455w0rd.app

import android.app.Application
import android.content.Context
import x.x.p455w0rd.db.RoomDBDatabase
import x.x.p455w0rd.db.RoomDao

class App : Application() {
    companion object {
        lateinit var application: Context
        lateinit var dao: RoomDao
    }

    override fun onCreate() {
        super.onCreate()
        application = this.applicationContext
        dao = RoomDBDatabase.create(this).roomDao()
    }
}