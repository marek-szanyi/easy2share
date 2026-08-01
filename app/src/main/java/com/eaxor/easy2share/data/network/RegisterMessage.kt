package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class RegisterMessage(
    val clientName: String,
)
