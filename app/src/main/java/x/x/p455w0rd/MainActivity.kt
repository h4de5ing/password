package x.x.p455w0rd

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import x.x.p455w0rd.activitys.EditActivity
import x.x.p455w0rd.adapter.IndexViewAdapter
import x.x.p455w0rd.app.App
import x.x.p455w0rd.databinding.ActivityMainBinding
import x.x.p455w0rd.db.PasswordItem
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val indexAdapter = IndexViewAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.passwordList.adapter = indexAdapter
        indexAdapter.setOnItemClickListener { _, _, position ->
            startActivity(
                Intent(this, EditActivity::class.java).putExtra(
                    "id",
                    indexAdapter.data[position].id.toInt()
                )
            )
            println(indexAdapter.data[position])
        }
        binding.fab.setOnClickListener {
            startActivity(
                Intent(this, EditActivity::class.java).putExtra(
                    "id",
                    -1
                )
            )
        }
        App.dao?.observerPasswordItem()?.observe(this) {
            indexAdapter.setNewInstance(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.`in` -> in_()
            R.id.out -> out()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun in_() {
        select_file(this, object : DialogSelection {
            override fun onSelectedFilePaths(files: Array<String?>?) {
                try {
                    files?.apply {
                        val path = this[0]
                        val file = File(path)
                        if (file.isFile && file.exists()) {
                            val list = csv4File(path!!)
                            val passItems = mutableListOf<PasswordItem>()
                            list.forEach {
                                passItems.add(
                                    PasswordItem(
                                        it[0].toLong(),
                                        it[1].toInt(),
                                        it[2],
                                        it[3],
                                        it[4],
                                        it[5],
                                        it[6].toLong()
                                    )
                                )
                            }
                            App.dao?.insert(*passItems.toTypedArray())
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "导入失败${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        })
    }

    private fun out() {
        val items = App.dao?.output()
        val list = mutableListOf<Array<String>>()
        items?.apply {
            this.forEach {
                val array = arrayOf(
                    "${it.id}",
                    "${it.type}",
                    it.title,
                    it.account,
                    it.password,
                    it.memoInfo,
                    "${it.time}"
                )
                list.add(array)
            }
        }
        csv2File("${App.application?.obbDir?.absolutePath}/password.csv", list)
    }

    //指纹
//    private var mBiometricPrompt: BiometricPrompt? = null
//    private var mCancellationSignal: CancellationSignal? = null
//    private var mAuthenticationCallback: BiometricPrompt.AuthenticationCallback? = null
//    private val TAG = "gh0st"
//    private fun initFP() {
//        val executor = ContextCompat.getMainExecutor(this)
//                mCancellationSignal = CancellationSignal()
//        mCancellationSignal!!.setOnCancelListener { //handle cancel result
//            Log.i(TAG, "Canceled")
//        }
//
//        mAuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
//            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                super.onAuthenticationError(errorCode, errString)
//                Log.i(TAG, "onAuthenticationError $errString")
//            }
//
//            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                super.onAuthenticationSucceeded(result)
//                Log.i(TAG, "onAuthenticationSucceeded $result")
//            }
//
//            override fun onAuthenticationFailed() {
//                super.onAuthenticationFailed()
//                Log.i(TAG, "onAuthenticationFailed ")
//            }
//        }
//        mBiometricPrompt = BiometricPrompt(this,executor,mAuthenticationCallback)
////            .setTitle("指纹验证")
////            .setDescription("描述")
////            .setNegativeButton("取消", mainExecutor,
////                DialogInterface.OnClickListener { dialogInterface, i ->
////                    Log.i(
////                        TAG,
////                        "Cancel button clicked"
////                    )
////                })
////            .build()
//        mBiometricPrompt?.authenticate(mCancellationSignal, mainExecutor, mAuthenticationCallback)
//    }
}