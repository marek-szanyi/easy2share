/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Guards the parts of the file transfer wire contract the web client depends on:
 * the `type` discriminator must be on the wire, and chunk payloads must travel as
 * compact CBOR byte strings rather than arrays of numbers.
 */
@OptIn(ExperimentalSerializationApi::class)
class FileTransferMessageTest {
    @Test
    fun `start message carries its type discriminator on the wire`() {
        val message =
            FileTransferStartMessage(
                fileId = "file-1",
                fileName = "report.pdf",
                mimeType = "application/pdf",
                fileSize = 4096,
                chunkCount = 1,
            )

        val encoded = protocolCbor.encodeToByteArray(message)

        assertTrue("`type` key missing from the frame", encoded.containsAscii("type"))
        assertTrue("discriminator missing from the frame", encoded.containsAscii(FileTransferType.START))
        assertEquals(message, protocolCbor.decodeFromByteArray<FileTransferStartMessage>(encoded))
    }

    @Test
    fun `chunk payload is encoded as a compact byte string`() {
        val payload = Random(7).nextBytes(4096)
        val message = FileChunkMessage(fileId = "file-1", chunkIndex = 3, data = payload)

        val encoded = protocolCbor.encodeToByteArray(message)

        // A CBOR array of numbers would need at least one byte per element plus
        // overhead; a byte string adds only a short header.
        assertTrue("chunk payload was not encoded as a byte string", encoded.size < payload.size + 128)
        assertTrue(encoded.containsAscii(FileTransferType.CHUNK))
        assertEquals(message, protocolCbor.decodeFromByteArray<FileChunkMessage>(encoded))
    }

    @Test
    fun `end message survives a round trip with and without an error`() {
        val completed = FileTransferEndMessage(fileId = "file-1", isComplete = true)
        val failed = FileTransferEndMessage(fileId = "file-1", isComplete = false, error = "Could not read the file")

        val encodedCompleted = protocolCbor.encodeToByteArray(completed)
        assertTrue(encodedCompleted.containsAscii(FileTransferType.END))
        assertEquals(completed, protocolCbor.decodeFromByteArray<FileTransferEndMessage>(encodedCompleted))
        assertEquals(failed, protocolCbor.decodeFromByteArray<FileTransferEndMessage>(protocolCbor.encodeToByteArray(failed)))
    }

    @Test
    fun `clipboard frames keep their existing shape`() {
        val message = ClipboardSharingMessage(clipboard = "secret")

        val encoded = protocolCbor.encodeToByteArray(message)

        assertTrue(encoded.containsAscii("clipboard"))
        assertEquals(message, protocolCbor.decodeFromByteArray<ClipboardSharingMessage>(encoded))
    }

    private fun ByteArray.containsAscii(value: String): Boolean = String(this, Charsets.ISO_8859_1).contains(value)
}
