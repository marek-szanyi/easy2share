package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage(
    val isOk: Boolean,
    val message: String,
    val code: Int,
)
