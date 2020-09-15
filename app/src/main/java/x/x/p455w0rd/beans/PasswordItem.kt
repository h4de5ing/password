package x.x.p455w0rd.beans

data class PasswordItem(
    val id: Int,
    val type: Int,
    var title: String,
    var account: String,
    var password: String,
    var memoInfo: String,
    var time: Long
)