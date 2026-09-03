/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.repository

import android.content.Context
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.net.toUri
import com.eaxor.easy2share.data.webengine.SharedFile
import com.eaxor.easy2share.data.webengine.shareFiles
import com.eaxor.easy2share.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Resolves the documents picked through the Storage Access Framework and hands
 * them to the web engine, which encrypts and streams them to the clients.
 *
 * Only metadata is read here; the bytes stay behind a lazy stream so that large
 * files are never held in memory.
 */
class FileRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : FileRepository {
        override suspend fun share(fileUris: List<String>): Int =
            withContext(Dispatchers.IO) {
                fileUris.count { rawUri ->
                    val sharedFile = describe(rawUri)
                    if (sharedFile == null) {
                        false
                    } else {
                        shareFiles.send(sharedFile)
                        true
                    }
                }
            }

        private fun describe(rawUri: String): SharedFile? =
            try {
                val uri = rawUri.toUri()
                var fileName = uri.lastPathSegment?.substringAfterLast('/') ?: FALLBACK_FILE_NAME
                var size = UNKNOWN_SIZE

                context.contentResolver
                    .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIndex >= 0 && !cursor.isNull(nameIndex)) {
                                fileName = cursor.getString(nameIndex)
                            }
                            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                                size = cursor.getLong(sizeIndex)
                            }
                        }
                    }

                SharedFile(
                    fileName = fileName.ifBlank { FALLBACK_FILE_NAME },
                    mimeType = context.contentResolver.getType(uri) ?: DEFAULT_MIME_TYPE,
                    size = size,
                    openStream = { context.contentResolver.openInputStream(uri) },
                )
            } catch (exception: SecurityException) {
                Log.e(TAG, "Missing permission to read $rawUri", exception)
                null
            } catch (exception: RuntimeException) {
                Log.e(TAG, "Unable to resolve $rawUri", exception)
                null
            }

        private companion object {
            const val TAG = "FileRepository"
            const val FALLBACK_FILE_NAME = "shared-file"
            const val DEFAULT_MIME_TYPE = "application/octet-stream"
            const val UNKNOWN_SIZE = -1L
        }
    }
