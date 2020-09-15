package x.x.p455w0rd.activitys

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit.*
import x.x.p455w0rd.R
import x.x.p455w0rd.app.App
import x.x.p455w0rd.confirm
import x.x.p455w0rd.db.PasswordItem
import x.x.p455w0rd.eunms.PasswordType
import x.x.p455w0rd.now

class EditActivity : BaseBackActivity() {
    var isUpdate = false
    var mId = -1
    var item: PasswordItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        try {
            mId = intent.getIntExtra("id", -1)
            println(mId)
            if (mId > -1) {
                isUpdate = true
                val items = App.dao?.observerItemId(mId.toLong())
                items?.apply {
                    item = this.firstOrNull()
                    item?.apply {
                        et_title.setText(this.title)
                        et_account.setText(this.account)
                        et_password.setText(this.password)
                        memo.setText(this.memoInfo)
                    }
                }
            }
        } catch (e: Exception) {
        }
        btn_save.setOnClickListener {
            val title = et_title.text.toString()
            val account = et_account.text.toString()
            val password = et_password.text.toString()
            val memo = memo.text.toString()
            if (!TextUtils.isEmpty(title)) {
                if (!TextUtils.isEmpty(account)) {
                    if (!TextUtils.isEmpty(password)) {
                        save(title, account, password, memo)
                    } else {
                        et_password.requestFocus()
                        et_password.error = "请输入账号信息"
                    }
                } else {
                    et_account.requestFocus()
                    et_account.error = "请输入账号信息"
                }
            } else {
                et_title.requestFocus()
                et_title.error = "输入标题以便方便查找密码归属"
            }
        }
    }

    fun save(title: String, account: String, password: String, memo: String) {
        try {
            if (isUpdate) {
                println("更新:$mId")
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