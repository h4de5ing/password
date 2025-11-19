package x.x.p455w0rd

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.github.h4de5ing.filepicker.DialogUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import x.x.p455w0rd.adapter.IndexViewAdapter
import x.x.p455w0rd.app.App
import x.x.p455w0rd.databinding.ActivityMainBinding
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.ui.EditActivity
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
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
        App.dao.observerPasswordItem().observe(this) {
            indexAdapter.setNewInstance(it)
        }
        binding.masked.setOnClickListener { initFP() }
        XXPermissions.with(this)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        toast("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        toast("被永久拒绝授权，请手动授予文件读写权限")
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    }
                }
            })
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.masked.visibility = View.VISIBLE
        initFP()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.`in` -> import()
            R.id.out -> export()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun import() {
        DialogUtils.selectExternalCacheFile(this, "导入") {
            try {
                val path = it[0]
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
                    App.dao.insert(*passItems.toTypedArray())
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "导入失败${e.message}", Toast.LENGTH_LONG)
                    .show()
                e.printStackTrace()
            }
        }
    }

    private fun export() {
        DialogUtils.selectExternalCacheDir(this, "导出", true) {
            val items = App.dao.output()
            val list = mutableListOf<Array<String>>()
            items.forEach {
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
            csv2File(it[0] + File.separator + it[1], list)
        }
    }

    private var mBiometricPrompt: BiometricPrompt? = null
    private val TAG = "gh0st"
    private fun initFP() {
        if (isSupport()) {
            val executor = ContextCompat.getMainExecutor(this)
            mBiometricPrompt =
                BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.e(TAG, "onAuthenticationError $errorCode $errString")
                        binding.masked.visibility = View.VISIBLE
                    }

                    override fun onAuthenticationFailed() {
                        Log.e(TAG, "onAuthenticationFailed")
                        binding.masked.visibility = View.VISIBLE
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.e(TAG, "onAuthenticationSucceeded")
                        binding.masked.visibility = View.GONE
                    }
                })
            val prompt = BiometricPrompt.PromptInfo.Builder()
                .setTitle("指纹验证")
                .setDescription("显示描述")
                .setSubtitle("请手指触碰指纹传感器")
                .setNegativeButtonText("使用密码登录")
                .build()
            mBiometricPrompt?.authenticate(prompt)
        } else {
            binding.masked.visibility = View.GONE
        }
    }

    @SuppressLint("RestrictedApi")
    private fun isSupport(): Boolean {
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        val managerCompat = FingerprintManagerCompat.from(this)
        if (!managerCompat.isHardwareDetected) {
            binding.tvMask.text = "系统不支持指纹功能"
            return false
        } else if (!keyguardManager.isKeyguardSecure) {
            binding.tvMask.text = "屏幕未设置锁屏 请先设置锁屏并添加一个指纹"
            return false
        } else if (!managerCompat.hasEnrolledFingerprints()) {
            binding.tvMask.text = "至少在系统中添加一个指纹"
            return false
        }
        return true
    }
}