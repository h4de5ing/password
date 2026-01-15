package com.password.shared.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

actual object ClipboardUtil {
    private lateinit var context: Context

    fun init(ctx: Context) {
        context = ctx
    }

    actual fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)
    }
}
