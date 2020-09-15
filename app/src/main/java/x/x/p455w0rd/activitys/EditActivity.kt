package x.x.p455w0rd.activitys

import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import x.x.p455w0rd.R
import x.x.p455w0rd.db.Item
import x.x.p455w0rd.eunms.PasswordType
import x.x.p455w0rd.now

class EditActivity : BaseBackActivity() {
    var isUpdate = false
    var mId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        try {
            mId = intent.getIntExtra("id", -1)
            transaction {
                val item = Item.select { Item.id eq mId }
                item?.apply {
                    isUpdate = true
                    title = getString(R.string.activity_edit)
                    runOnUiThread {
                        this.forEach {
                            et_title.setText(it[Item.title])
                            et_account.setText(it[Item.account])
                            et_password.setText(it[Item.password])
                            memo.setText(it[Item.memoInfo])
                        }
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
        transaction {
            try {
                if (isUpdate) {
                    Item.update { item ->
                        item[Item.id] = mId
                        item[Item.type] = PasswordType.Normal.ordinal
                        item[Item.title] = title
                        item[Item.account] = account
                        item[Item.password] = password
                        item[Item.memoInfo] = memo
                        item[Item.time] = now()
                    }
                } else {
                    Item.insert { item ->
                        item[Item.type] = PasswordType.Normal.ordinal
                        item[Item.title] = title
                        item[Item.account] = account
                        item[Item.password] = password
                        item[Item.memoInfo] = memo
                        item[Item.time] = now()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}