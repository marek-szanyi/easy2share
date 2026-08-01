package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class FileRequestMessage(
    val fileId: String,
)
