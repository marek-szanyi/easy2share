/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.webengine

import com.eaxor.easy2share.data.network.TransportMessage
import kotlinx.coroutines.flow.MutableSharedFlow

class HelloMessageHandler(
    override val nextHandler: IWebRouteHandler?,
) : IWebRouteHandler {
    override suspend fun handle(
        message: TransportMessage,
        responseFlow: MutableSharedFlow<TransportMessage>,
    ) {
        // Handle the hello message
    }
}
