package com.password.shared.platform

import androidx.compose.runtime.Composable

/**
 * Platform-specific file picking + IO for encrypted import/export.
 *
 * - Android: SAF (CreateDocument / OpenDocument)
 * - Desktop: native FileDialog
 */
interface PlatformFileAccess {
    /**
     * Prompts the user to pick a destination file and writes [text] to it.
     * Returns false if user cancels or write fails.
     */
    suspend fun exportText(suggestedFileName: String, text: String): Boolean

    /**
     * Prompts the user to pick a file and reads its text.
     * Returns null if user cancels or read fails.
     */
    suspend fun importText(): String?
}

@Composable
expect fun rememberPlatformFileAccess(): PlatformFileAccess
