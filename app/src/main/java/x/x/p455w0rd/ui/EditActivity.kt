package x.x.p455w0rd.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import x.x.p455w0rd.R
import x.x.p455w0rd.app.App
import x.x.p455w0rd.confirm
import x.x.p455w0rd.databinding.ActivityEditBinding
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.eunms.PasswordType
import x.x.p455w0rd.now

class EditActivity : BaseBackActivity() {
    var isUpdate = false
    var mId = -1
    var item: PasswordItem? = null
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            mId = intent.getIntExtra("id", -1)
            if (mId > -1) {
                isUpdate = true
                val items = App.dao?.observerItemId(mId.toLong())
                items?.apply {
                    item = this.firstOrNull()
                    item?.apply {
                        binding.etTitle.setText(this.title)
                        binding.etAccount.setText(this.account)
                        binding.etPassword.setText(this.password)
                        binding.memo.setText(this.memoInfo)
                    }
                }
                title = "编辑"
            } else {
                title = "新建"
            }
        } catch (_: Exception) {
        }
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val account = binding.etAccount.text.toString()
            val password = binding.etPassword.text.toString()
            val memo = binding.memo.text.toString()
            if (!TextUtils.isEmpty(title)) {
                if (!TextUtils.isEmpty(account)) {
                    if (!TextUtils.isEmpty(password)) {
                        save(title, account, password, memo)
                    } else {
                        binding.etPassword.requestFocus()
                        binding.etPassword.error = "请输入账号信息"
                    }
                } else {
                    binding.etAccount.requestFocus()
                    binding.etAccount.error = "请输入账号信息"
                }
            } else {
                binding.etTitle.requestFocus()
                binding.etTitle.error = "输入标题以便方便查找密码归属"
            }
        }
    }

    private fun save(title: String, account: String, password: String, memo: String) {
        try {
            if (isUpdate) {
                item?.apply {
                    this.type = PasswordType.Normal.ordinal
                    this.title = title
                    this.account = account
                    this.password = password
                    this.memoInfo = memo
                    this.time = now()
                    App.dao?.update(this)
                }
            } else {
                App.dao?.insert(
                    PasswordItem(
                        id = 0,
                        type = PasswordType.Normal.ordinal,
                        title = title,
                        account = account,
                        password = password,
                        memoInfo = memo,
                        time = now()
                    )
                )
            }
            runOnUiThread { finish() }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(
                    this@EditActivity,
                    "编辑异常${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                confirm(this@EditActivity, "确定删除这条【重要密码】？") {
                    this.item?.apply {
                        App.dao?.delete(this)
                        runOnUiThread { finish() }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}