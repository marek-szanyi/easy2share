package com.eaxor.easy2share.domain.webengine

import com.eaxor.easy2share.data.network.TransportMessage
import kotlinx.coroutines.flow.MutableSharedFlow

interface IWebRouteHandler {

    val nextHandler: IWebRouteHandler?

    suspend fun handle(message: TransportMessage, responseFlow: MutableSharedFlow<TransportMessage> )

}