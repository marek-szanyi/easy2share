/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.webengine

import android.util.Log
import com.eaxor.easy2share.data.network.ClipboardSharingMessage
import com.eaxor.easy2share.data.network.EncryptedMessage
import com.eaxor.easy2share.data.network.RegisterMessage
import com.eaxor.easy2share.data.network.ResponseMessage
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
 * Builds an embedded websocket server running over plain `ws://`.
 *
 * [key] is the 32-byte session key received by scanning a QR code (never generated
 * here). All application messages are encrypted with ChaCha20-Poly1305 using this key:
 * every binary frame is a CBOR-encoded [EncryptedMessage] whose `data` field holds
 * `ciphertext||tag` followed by the 12-byte nonce, and the decrypted plaintext is a
 * CBOR-encoded inner message. Transport-level TLS is intentionally not used so that
 * browser clients (which cannot do TLS-PSK) can connect.
 */
fun buildWebsocketEngine(
    dispatcher: CoroutineDispatcher,
    port: Int,
    key: ByteArray,
): TlsWebsocketEngine {
    require(key.isNotEmpty()) { "Session key must not be empty" }
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
                                val envelope = Cbor.decodeFromByteArray<EncryptedMessage>(receivedBinary)
                                val decrypted = envelope.data.decryptChaCha20(key)
                                val registerMessage = Cbor.decodeFromByteArray<RegisterMessage>(decrypted)
                                Log.i("WebEngine", "Client registered: ${registerMessage.clientName}")
                                val responseBytes =
                                    Cbor.encodeToByteArray(
                                        ResponseMessage(isOk = true, message = "Welcome", code = 201),
                                    )
                                val responseEnvelope = EncryptedMessage(responseBytes.encryptChaCha20(key))
                                send(Frame.Binary(true, Cbor.encodeToByteArray(responseEnvelope)))
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
    val payload = Cbor.encodeToByteArray(ClipboardSharingMessage(clipboard = content))
    val envelope = EncryptedMessage(payload.encryptChaCha20(key))
    val frameBytes = Cbor.encodeToByteArray(envelope)
    for (session in connectedSessions) {
        try {
            session.send(Frame.Binary(true, frameBytes))
        } catch (e: Exception) {
            Log.w("WebEngine", "Dropping client session after failed clipboard send", e)
            connectedSessions.remove(session)
        }
    }
    Log.i("WebEngine", "Clipboard broadcast sent to ${connectedSessions.size} client(s)")
}
