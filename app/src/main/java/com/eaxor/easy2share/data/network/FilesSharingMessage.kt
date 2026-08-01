package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable


@Serializable
data class FileInfo(
    val fileName: String,
    val fileId: String,
)


@Serializable
data class FilesSharingMessage(
    val files: List<FileInfo> = emptyList(),
)
