/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Reads the current primary clip and normalises it into a single string ready
 * for sharing:
 *
 *  - textual content is returned as-is;
 *  - non-textual content (e.g. an image behind a content URI) is read as raw
 *    bytes and Base64 encoded (standard alphabet with padding, matching what
 *    the web client decodes);
 *  - `null` when the clipboard is empty or unreadable.
 */
class GetClipboardContentUseCase
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        suspend operator fun invoke(): String? =
            withContext(Dispatchers.IO) {
                val clipboard = context.getSystemService(ClipboardManager::class.java)
                val clip = clipboard?.primaryClip
                if (clip == null || clip.itemCount == 0) return@withContext null

                val item = clip.getItemAt(0)
                item.text
                    ?.toString()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { return@withContext it }

                item.uri?.let { uri ->
                    try {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            return@withContext Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Unable to read clipboard content behind URI", e)
                    }
                }

                // Last resort for non-text, non-URI clips (e.g. an Intent).
                item.coerceToText(context)?.toString()?.takeIf { it.isNotEmpty() }
            }

        private companion object {
            const val TAG = "GetClipboardContent"
        }
    }
