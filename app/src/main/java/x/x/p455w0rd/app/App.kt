package x.x.p455w0rd.app

import android.app.Application
import android.content.Context
import x.x.p455w0rd.db.RoomDBDatabase
import x.x.p455w0rd.db.RoomDao

class App : Application() {
    companion object {
        var application: Context? = null
        var dao: RoomDao? = null
    }

    override fun onCreate() {
        super.onCreate()
        application = this.applicationContext
        dao = RoomDBDatabase.create(this).roomDao()
    }
}