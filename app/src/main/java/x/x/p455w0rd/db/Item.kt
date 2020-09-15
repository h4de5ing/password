package x.x.p455w0rd.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Item : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val type: Column<Int> = integer("type")
    val title: Column<String> = varchar("title", 20)
    val account: Column<String> = varchar("account", 32)
    val password: Column<String> = varchar("password", 64)
    //备忘信息 ，可以存储支付密码 二次密码 google验证码
    val memoInfo: Column<String> = varchar("memoInfo", 100)
    var time: Column<Long> = long("time")
}