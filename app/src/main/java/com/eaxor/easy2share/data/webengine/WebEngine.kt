/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.webengine

import android.util.Log
import com.eaxor.easy2share.data.network.ClipboardSharingMessage
import com.eaxor.easy2share.data.network.EncryptedMessage
import com.eaxor.easy2share.data.network.FileChunkMessage
import com.eaxor.easy2share.data.network.FileTransferEndMessage
import com.eaxor.easy2share.data.network.FileTransferStartMessage
import com.eaxor.easy2share.data.network.RegisterMessage
import com.eaxor.easy2share.data.network.ResponseMessage
import com.eaxor.easy2share.data.network.protocolCbor
import com.eaxor.easy2share.domain.security.decryptChaCha20
import com.eaxor.easy2share.domain.security.encryptChaCha20
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.cbor.cbor
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readBytes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.event.Level
import java.io.IOException
import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.time.Duration.Companion.seconds

typealias TlsWebsocketEngine = EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

/** Sessions that completed the encrypted register handshake. */
private val connectedSessions = CopyOnWriteArraySet<WebSocketSession>()

/**
 * Entry point for pushing clipboard content to every connected client. Only
 * the most recent value matters, so an unconsumed older value is dropped.
 */
val notifyClients = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

/**
 * Entry point for pushing files to every connected client. Unlike the clipboard
 * every queued file must reach the clients, so nothing is ever dropped.
 */
val shareFiles = Channel<SharedFile>(capacity = Channel.UNLIMITED)

internal fun resetWebEngineState() {
    connectedSessions.clear()
    while (notifyClients.tryReceive().isSuccess) {}
    while (shareFiles.tryReceive().isSuccess) {}
}

/** Payload size of a single [FileChunkMessage] before encryption. */
private const val FILE_CHUNK_SIZE = 128 * 1024

/** Sent as `chunkCount` when the source does not expose its size up front. */
private const val UNKNOWN_CHUNK_COUNT = -1

/**
 * Builds an embedded websocket server running over plain `ws://`.
 *
 * [key] is the 32-byte session key received by scanning a QR code (never generated
 * here). All application messages are encrypted with ChaCha20-Poly1305 using this key:
 * every binary frame is a CBOR-encoded [EncryptedMessage] whose `data` field holds
 * `ciphertext||tag` followed by the 12-byte nonce, and the decrypted plaintext is a
 * CBOR-encoded inner message. Transport-level TLS is intentionally not used so that
 * browser clients (which cannot do TLS-PSK) can connect.
 *
 * Inner messages are distinguished by their shape: the handshake response carries
 * `isOk`, clipboard pushes carry `clipboard`, and file transfers carry an explicit
 * `type` field ([FileTransferStartMessage], [FileChunkMessage],
 * [FileTransferEndMessage]) plus a `fileId`, so several transfers may interleave
 * on the same socket.
 */
fun buildWebsocketEngine(
    dispatcher: CoroutineDispatcher,
    port: Int,
    key: ByteArray,
): TlsWebsocketEngine {
    require(key.size == 32) { "Session key must be 32 bytes (ChaCha20-Poly1305)" }
    return embeddedServer(Netty, applicationEnvironment {}, {
        connector { this.port = port }
        enableHttp2 = false
    }) {
        Log.i("WebEngine", "application module: configuring server")
        configureServer()
        configureRouting(dispatcher, key)
        Log.i("WebEngine", "application module: routing configured")
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.configureServer() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        // Servers must not mask frames (RFC 6455); browsers reject masked server frames.
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/api")
        }
        format { call ->
            val userAgent = call.request.headers["User-Agent"] ?: "unknown"
            "Method: ${call.request.httpMethod.value}, Path: ${call.request.path()}, User-Agent: $userAgent"
        }
    }
    install(ContentNegotiation) {
        val module = SerializersModule {}
        cbor(
            Cbor {
                ignoreUnknownKeys = true
                serializersModule = module
            },
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
private suspend fun Application.configureRouting(
    dispatcher: CoroutineDispatcher,
    key: ByteArray,
) {
    withContext(dispatcher) {
        routing {
//        route("/api") {
//            post("/getfile") {
//                val encryptedMessage = call.receive<EncryptedMessage>()
//                val decryptedRawRequest = encryptedMessage.data.decryptChaCha20(key)
//                val fileRequest : FileRequestMessage = Cbor.decodeFromByteArray(decryptedRawRequest)
//            }
//        }
            webSocket("/notify") {
                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Binary) {
                            val receivedBinary = frame.readBytes()
                            try {
                                val envelope = protocolCbor.decodeFromByteArray<EncryptedMessage>(receivedBinary)
                                val decrypted = envelope.data.decryptChaCha20(key)
                                val registerMessage = protocolCbor.decodeFromByteArray<RegisterMessage>(decrypted)
                                Log.i("WebEngine", "Client registered: ${registerMessage.clientName}")
                                val responseBytes =
                                    protocolCbor.encodeToByteArray(
                                        ResponseMessage(isOk = true, message = "Welcome", code = 201),
                                    )
                                val responseEnvelope = EncryptedMessage(responseBytes.encryptChaCha20(key))
                                send(Frame.Binary(true, protocolCbor.encodeToByteArray(responseEnvelope)))
                                Log.i("WebEngine", "Welcome response sent to ${registerMessage.clientName}")
                                connectedSessions.add(this)
                            } catch (e: Exception) {
                                Log.e("WebEngine", "Error decoding or decrypting message", e)
                                e.printStackTrace()
                            }
                        }
                    }
                } finally {
                    connectedSessions.remove(this)
                }
            }
        }
    }
    // Application scope (not the withContext above) so the broadcaster does not
    // block module setup and is cancelled together with the server.
    launch(dispatcher) {
        for (content in notifyClients) {
            broadcastClipboard(content, key)
        }
    }
    // File transfers run on their own consumer so a large file never delays a
    // clipboard push (and vice versa).
    launch(dispatcher) {
        for (file in shareFiles) {
            broadcastFile(file, key)
        }
    }
}

/**
 * Encrypts [content] as a [ClipboardSharingMessage] and pushes it to every
 * registered client. Sessions that fail to receive are dropped.
 */
@OptIn(ExperimentalSerializationApi::class)
private suspend fun broadcastClipboard(
    content: String,
    key: ByteArray,
) {
    broadcastFrame(protocolCbor.encodeToByteArray(ClipboardSharingMessage(clipboard = content)), key)
    Log.i("WebEngine", "Clipboard broadcast sent to ${connectedSessions.size} client(s)")
}

/**
 * Streams [file] to every registered client as a start frame, a series of
 * [FILE_CHUNK_SIZE] chunk frames and a terminating frame. Every frame is
 * individually encrypted with a fresh nonce, exactly like clipboard pushes.
 */
@OptIn(ExperimentalSerializationApi::class)
private suspend fun broadcastFile(
    file: SharedFile,
    key: ByteArray,
) {
    if (connectedSessions.isEmpty()) {
        Log.w("WebEngine", "No connected client to receive ${file.fileName}")
        return
    }

    val fileId = UUID.randomUUID().toString()
    val chunkCount =
        if (file.size > 0) {
            ((file.size + FILE_CHUNK_SIZE - 1) / FILE_CHUNK_SIZE).toInt()
        } else {
            UNKNOWN_CHUNK_COUNT
        }

    broadcastFrame(
        protocolCbor.encodeToByteArray(
            FileTransferStartMessage(
                fileId = fileId,
                fileName = file.fileName,
                mimeType = file.mimeType,
                fileSize = file.size,
                chunkCount = chunkCount,
            ),
        ),
        key,
    )

    var chunkIndex = 0
    var failure: String? = null
    try {
        val stream = file.openStream() ?: throw IOException("Unable to open ${file.fileName}")
        stream.use { input ->
            val buffer = ByteArray(FILE_CHUNK_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read <= 0) break
                broadcastFrame(
                    protocolCbor.encodeToByteArray(
                        FileChunkMessage(
                            fileId = fileId,
                            chunkIndex = chunkIndex,
                            data = buffer.copyOf(read),
                        ),
                    ),
                    key,
                )
                chunkIndex++
            }
        }
    } catch (e: IOException) {
        Log.e("WebEngine", "Failed to read ${file.fileName} while sharing", e)
        failure = e.message ?: "Could not read the file"
    } catch (e: RuntimeException) {
        Log.e("WebEngine", "Failed to share ${file.fileName}", e)
        failure = e.message ?: "Could not share the file"
    }

    broadcastFrame(
        protocolCbor.encodeToByteArray(
            FileTransferEndMessage(
                fileId = fileId,
                isComplete = failure == null,
                error = failure,
            ),
        ),
        key,
    )
    Log.i("WebEngine", "Shared ${file.fileName} as $chunkIndex chunk(s) to ${connectedSessions.size} client(s)")
}

/** Encrypts an already CBOR-encoded inner message and sends it to every client. */
@OptIn(ExperimentalSerializationApi::class)
private suspend fun broadcastFrame(
    payload: ByteArray,
    key: ByteArray,
) {
    val frameBytes = protocolCbor.encodeToByteArray(EncryptedMessage(payload.encryptChaCha20(key)))
    for (session in connectedSessions) {
        try {
            session.send(Frame.Binary(true, frameBytes))
        } catch (e: Exception) {
            Log.w("WebEngine", "Dropping client session after failed send", e)
            connectedSessions.remove(session)
        }
    }
}
