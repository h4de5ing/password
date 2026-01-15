package x.x.p455w0rd.security

import kotlinx.serialization.Serializable

@Serializable
data class ExportItem(
    val id: Long = 0,
    val type: Int = 1,
    val account: String = "",
    val password: String = "",
    val memoInfo: String = "",
    val time: Long = 0,
    val dataJson: String = "{}"
)

@Serializable
data class ExportPayload(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val items: List<ExportItem> = emptyList()
)

