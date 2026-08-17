/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.repository

import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import android.util.Log
import com.eaxor.easy2share.data.webengine.notifyClients
import com.eaxor.easy2share.domain.repository.ClipboardRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ClipboardRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ClipboardRepository {
        override suspend fun getContent(): String? =
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
                    } catch (exception: IOException) {
                        Log.e(TAG, "Unable to read clipboard content behind URI", exception)
                    } catch (exception: RuntimeException) {
                        Log.e(TAG, "Unable to access clipboard content behind URI", exception)
                    }
                }

                item.coerceToText(context)?.toString()?.takeIf { it.isNotEmpty() }
            }

        override suspend fun share(content: String) {
            notifyClients.send(content)
        }

        private companion object {
            const val TAG = "ClipboardRepository"
        }
    }
