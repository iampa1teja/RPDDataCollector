package com.rpd.data.network

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

    suspend fun connect(url: String) {
        try {
            session = client.webSocketSession(urlString = url)
            _connectionState.value = ConnectionState.CONNECTED

        } catch (e: Exception) {
            _connectionState.value =
                ConnectionState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun sendHandshake() {
        val currentSession = session
            ?: throw IllegalStateException("Not connected")

        val request = HandshakeRequest()
        val json = Json.encodeToString<HandshakeRequest>(request)

        currentSession.send(Frame.Text(json))
    }

    suspend fun receiveHandshakeResponse(): HandshakeResponse {
        val currentSession = session
            ?: throw IllegalStateException("Not connected")

        val frame = currentSession.incoming.receive() as Frame.Text

        return Json.decodeFromString(frame.readText())
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