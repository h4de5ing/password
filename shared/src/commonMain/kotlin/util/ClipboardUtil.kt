package com.password.shared.util

// 平台特定的剪贴板实现
expect object ClipboardUtil {
    fun copyToClipboard(text: String)
}
