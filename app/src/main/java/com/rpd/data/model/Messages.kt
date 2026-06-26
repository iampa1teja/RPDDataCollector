package com.rpd.data.model
import kotlinx.serialization.Serializable

@Serializable
data class HandshakeRequest (
    val type: String = "handshake",
    val role: String = "app"
)

@Serializable
data class HandshakeResponse(
    val type: String,
    val status: String
)