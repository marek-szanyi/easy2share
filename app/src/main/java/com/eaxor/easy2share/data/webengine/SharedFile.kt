/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.webengine

import java.io.InputStream

/**
 * A file queued for sharing with the connected clients.
 *
 * The bytes are pulled lazily through [openStream] so that large files are
 * streamed chunk by chunk instead of being held in memory.
 */
data class SharedFile(
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val openStream: () -> InputStream?,
)
