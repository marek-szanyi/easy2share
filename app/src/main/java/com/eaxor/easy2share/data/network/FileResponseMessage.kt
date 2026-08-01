package com.eaxor.easy2share.data.network

import kotlinx.serialization.Serializable

@Serializable
data class FileResponseMessage(
    val fileId: String,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileResponseMessage

        if (fileId != other.fileId) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileId.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
