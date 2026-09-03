/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor

/**
 * CBOR codec shared by every application message on the websocket.
 *
 * `encodeDefaults` is on because the file transfer messages carry their `type`
 * discriminator as a defaulted property and the web client routes on it;
 * `ignoreUnknownKeys` keeps older clients from breaking when the protocol grows.
 */
@OptIn(ExperimentalSerializationApi::class)
val protocolCbor =
    Cbor {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
