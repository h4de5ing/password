package com.password.shared.platform

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

@Composable
actual fun rememberPlatformFileAccess(): PlatformFileAccess {
    val context = LocalContext.current
    val resolver = context.contentResolver

    val pendingCreate = remember { AtomicReference<CompletableDeferred<Uri?>?>(null) }
    val pendingOpen = remember { AtomicReference<CompletableDeferred<Uri?>?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        pendingCreate.getAndSet(null)?.complete(uri)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        pendingOpen.getAndSet(null)?.complete(uri)
    }

    return remember(resolver, exportLauncher, importLauncher) {
        object : PlatformFileAccess {
            override suspend fun exportText(suggestedFileName: String, text: String): Boolean {
                val deferred = CompletableDeferred<Uri?>()
                if (!pendingCreate.compareAndSet(null, deferred)) {
                    return false
                }

                exportLauncher.launch(suggestedFileName)
                val uri = deferred.await() ?: return false

                return withContext(Dispatchers.IO) {
                    runCatching {
                        resolver.openOutputStream(uri)?.use { os ->
                            os.write(text.toByteArray(Charsets.UTF_8))
                            os.flush()
                        } ?: error("openOutputStream returned null")
                    }.isSuccess
                }
            }

            override suspend fun importText(): String? {
                val deferred = CompletableDeferred<Uri?>()
                if (!pendingOpen.compareAndSet(null, deferred)) {
                    return null
                }

                importLauncher.launch(arrayOf("*/*"))
                val uri = deferred.await() ?: return null

                return withContext(Dispatchers.IO) {
                    readTextFromUri(resolver, uri)
                }
            }
        }
    }
}

private fun readTextFromUri(resolver: ContentResolver, uri: Uri): String? {
    return runCatching {
        resolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
    }.getOrNull()
}
