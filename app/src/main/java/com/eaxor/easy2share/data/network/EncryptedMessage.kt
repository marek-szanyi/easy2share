/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class EncryptedMessage(
    @ByteString val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMessage

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int = data.contentHashCode()

    companion object
}
