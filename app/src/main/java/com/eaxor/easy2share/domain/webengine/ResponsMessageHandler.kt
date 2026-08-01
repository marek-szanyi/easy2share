package com.eaxor.easy2share.domain.webengine

import com.eaxor.easy2share.data.network.TransportMessage
import kotlinx.coroutines.flow.MutableSharedFlow

class ResponsMessageHandler(override val nextHandler: IWebRouteHandler?) : IWebRouteHandler {
    override suspend fun handle(
        message: TransportMessage,
        responseFlow: MutableSharedFlow<TransportMessage>
    ) {

    }

}