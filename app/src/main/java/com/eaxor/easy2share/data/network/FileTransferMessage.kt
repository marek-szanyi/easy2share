/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString

/**
 * Wire discriminators for the push-based file transfer part of the protocol.
 *
 * Clipboard and handshake messages are recognised by field presence
 * (`clipboard` / `isOk`); file messages carry an explicit `type` so a stream of
 * interleaved transfers can be routed without ambiguity.
 */
object FileTransferType {
    const val START = "fileTransferStart"
    const val CHUNK = "fileChunk"
    const val END = "fileTransferEnd"
}

/** Announces a file whose chunks follow, one encrypted frame per chunk. */
@Serializable
data class FileTransferStartMessage(
    val fileId: String,
    val fileName: String,
    val mimeType: String,
    val fileSize: Long,
    val chunkCount: Int,
    val type: String = FileTransferType.START,
)

/**
 * A single slice of the file identified by [fileId]. Chunks of one file are sent
 * in ascending [chunkIndex] order; each one travels inside its own
 * [EncryptedMessage] envelope with a fresh nonce.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FileChunkMessage(
    val fileId: String,
    val chunkIndex: Int,
    @ByteString val data: ByteArray,
    val type: String = FileTransferType.CHUNK,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileChunkMessage

        if (chunkIndex != other.chunkIndex) return false
        if (fileId != other.fileId) return false
        if (!data.contentEquals(other.data)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunkIndex
        result = 31 * result + fileId.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

/**
 * Terminates a transfer. [isComplete] is false when the source could not be read
 * to the end, in which case [error] carries a short reason for the client.
 */
@Serializable
data class FileTransferEndMessage(
    val fileId: String,
    val isComplete: Boolean,
    val error: String? = null,
    val type: String = FileTransferType.END,
)
