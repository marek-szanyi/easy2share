package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class TransportMessage(
    val helloMessage: RegisterMessage? = null,
    val clipboardSharingMessage: ClipboardSharingMessage? = null,
    val responseMessage: ResponseMessage? = null,

    )
