package com.rpd.data.network

import android.util.Log
import com.rpd.data.model.HandshakeRequest
import com.rpd.data.model.HandshakeResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

sealed class ConnectionState {
    data object DISCONNECTED : ConnectionState()
    data object CONNECTING : ConnectionState()
    data object CONNECTED : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

object WebSocketManager {
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
    }
    private var session: WebSocketSession? = null
    private val _connectionState =
        MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)

    val connectionState: StateFlow<ConnectionState> =
        _connectionState.asStateFlow()

    private val json = Json { encodeDefaults = true }

    suspend fun connect(url: String) {
        _connectionState.value = ConnectionState.CONNECTING
        session = client.webSocketSession(urlString = url)
        _connectionState.value = ConnectionState.CONNECTED
    }

    suspend fun sendHandshake() {
        val currentSession = session
            ?: throw IllegalStateException("Not connected")

        val request = HandshakeRequest()
        val json = this.json.encodeToString<HandshakeRequest>(request)

        Log.d("WebSocketManager", "Sending handshake: $json")
        currentSession.send(Frame.Text(json))
    }

    suspend fun receiveHandshakeResponse(): HandshakeResponse {
        val currentSession = session
            ?: throw IllegalStateException("Not connected")

        val frame = currentSession.incoming.receive() as Frame.Text

        return this.json.decodeFromString(frame.readText())
    }

    suspend fun sendMessage(message: String) {
        val currentSession = session
            ?: throw IllegalStateException("Not connected")

        currentSession.send(Frame.Text(message))
    }

    suspend fun disconnect() {
        session?.close(
            CloseReason(
                CloseReason.Codes.NORMAL,
                "Disconnected by client"
            )
        )

        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}