package x.x.p455w0rd.app

import android.app.Application
import android.content.Context
import x.x.p455w0rd.db.connectToDatabase
import x.x.p455w0rd.db.upgradeDatabase

class App : Application() {
    companion object {
        var application: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        application = this.applicationContext
        connectToDatabase()
        upgradeDatabase()
    }
}