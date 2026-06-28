package com.rpd.data.ui.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpd.data.network.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConnectUiState {
    data object IDLE : ConnectUiState()
    data object CONNECTING : ConnectUiState()
    data object CONNECTED : ConnectUiState()
    data class Error(val message: String?) : ConnectUiState()
}

class ConnectViewModel : ViewModel() {
    private val _connectionUI = MutableStateFlow<ConnectUiState>(ConnectUiState.IDLE)
    val connectionUI: StateFlow<ConnectUiState> = _connectionUI.asStateFlow()

    fun connectToServer(url: String) {
        viewModelScope.launch {
            try {
                _connectionUI.value = ConnectUiState.CONNECTING
                WebSocketManager.connect(url)
                WebSocketManager.sendHandshake()
                val response = WebSocketManager.receiveHandshakeResponse()
                if (response.status == "ok") {
                    _connectionUI.value = ConnectUiState.CONNECTED
                }else{
                    _connectionUI.value = ConnectUiState.Error("Handshake Failed")
                }
            }catch (e: Exception) {
                _connectionUI.value = ConnectUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            WebSocketManager.disconnect()
            _connectionUI.value = ConnectUiState.IDLE
        }
    }

}