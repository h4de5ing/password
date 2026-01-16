package com.password.shared.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberPlatformFileAccess(): PlatformFileAccess {
    return remember {
        object : PlatformFileAccess {
            override suspend fun exportText(suggestedFileName: String, text: String): Boolean {
                val file = chooseSaveFile(suggestedFileName) ?: return false
                return withContext(Dispatchers.IO) {
                    runCatching {
                        file.writeText(text, Charsets.UTF_8)
                    }.isSuccess
                }
            }

            override suspend fun importText(): String? {
                val file = chooseOpenFile() ?: return null
                return withContext(Dispatchers.IO) {
                    runCatching { file.readText(Charsets.UTF_8) }.getOrNull()
                }
            }
        }
    }
}

private fun chooseOpenFile(): File? {
    val dialog = FileDialog(null as Frame?, "导入", FileDialog.LOAD)
    dialog.isVisible = true

    val directory = dialog.directory ?: return null
    val fileName = dialog.file ?: return null
    return File(directory, fileName)
}

private fun chooseSaveFile(suggestedFileName: String): File? {
    val dialog = FileDialog(null as Frame?, "导出", FileDialog.SAVE)
    dialog.file = suggestedFileName
    dialog.isVisible = true

    val directory = dialog.directory ?: return null
    val fileName = dialog.file ?: return null
    return File(directory, fileName)
}
