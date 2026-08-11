/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.security

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

val secureRandom = SecureRandom()
const val NONCE_SIZE = 12

fun ByteArray.encryptChaCha20(key: ByteArray): ByteArray {
    val nonce = ByteArray(NONCE_SIZE)
    secureRandom.nextBytes(nonce)

    val iv = IvParameterSpec(nonce)
    val secretKey = SecretKeySpec(key, "ChaCha20-Poly1305")

    // A fresh Cipher per call keeps encryption thread-safe.
    val chaChaCipher = Cipher.getInstance("ChaCha20-Poly1305")
    chaChaCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

    val encryptedMessage = chaChaCipher.doFinal(this)
    val output: ByteArray =
        ByteBuffer
            .allocate(encryptedMessage.size + nonce.size)
            .put(encryptedMessage)
            .put(nonce)
            .array()

    return output
}

fun ByteArray.decryptChaCha20(key: ByteArray): ByteArray {
    val bb = ByteBuffer.wrap(this)

    // split cText to get the appended nonce
    val encryptedText = ByteArray(this.size - NONCE_SIZE)
    val nonce = ByteArray(NONCE_SIZE)
    bb.get(encryptedText)
    bb.get(nonce)

    val iv = IvParameterSpec(nonce)
    val secretKey = SecretKeySpec(key, "ChaCha20-Poly1305")

    // A fresh Cipher per call keeps decryption thread-safe.
    val chaChaCipher = Cipher.getInstance("ChaCha20-Poly1305")
    chaChaCipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
    return chaChaCipher.doFinal(encryptedText)
}
