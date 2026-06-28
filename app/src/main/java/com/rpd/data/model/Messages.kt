package com.rpd.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HandshakeRequest(
    val type: String = "handshake",
    val role: String = "app"
)

@Serializable
data class HandshakeResponse(
    val type: String,
    val status: String
)

@Serializable
data class SensorMessage(
    val type: String = "sensor",
    val timestamp: Long = System.currentTimeMillis(),

    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float,

    val accelX: Float,
    val accelY: Float,
    val accelZ: Float
)

@Serializable
enum class GripperState {
    @SerialName("open") OPEN,
    @SerialName("close") Close
}

@Serializable
data class GripperMessage(
    val type: String = "gripper",
    val state: GripperState
)