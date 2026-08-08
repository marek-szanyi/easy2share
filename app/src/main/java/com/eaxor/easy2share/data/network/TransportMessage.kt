/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class TransportMessage(
    val helloMessage: RegisterMessage? = null,
    val clipboardSharingMessage: ClipboardSharingMessage? = null,
    val responseMessage: ResponseMessage? = null,
)
