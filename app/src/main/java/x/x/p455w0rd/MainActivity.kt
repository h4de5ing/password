package x.x.p455w0rd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import x.x.p455w0rd.activitys.EditActivity
import x.x.p455w0rd.adapter.IndexViewAdapter
import x.x.p455w0rd.beans.PasswordItem
import x.x.p455w0rd.db.Item

class MainActivity : AppCompatActivity() {
    val indexAdapter = IndexViewAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        password_list.adapter = indexAdapter
        indexAdapter.setOnItemClickListener { _, _, position ->
            startActivity(
                Intent(this, EditActivity::class.java).putExtra(
                    "id",
                    indexAdapter.data[position].id
                )
            )
        }
        fab.setOnClickListener { startActivity(Intent(this, EditActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        loadAllData()
    }

    private fun loadAllData() {
        transaction {
            try {
                val adapterList = mutableListOf<PasswordItem>()
                val lists = Item.selectAll()
                lists.forEach {
                    adapterList.add(
                        PasswordItem(
                            it[Item.id],
                            it[Item.type],
                            it[Item.title],
                            it[Item.account],
                            it[Item.password],
                            it[Item.memoInfo],
                            it[Item.time]
                        )
                    )
                }
                runOnUiThread {
                    indexAdapter.setNewInstance(adapterList)
                    indexAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}