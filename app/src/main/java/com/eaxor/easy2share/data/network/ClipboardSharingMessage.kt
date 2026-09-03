/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class ClipboardSharingMessage(
    // Field name is part of the wire contract: the web client reads `response.clipboard`.
    val clipboard: String,
)
