package com.rpd.data.ui.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpd.data.network.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecordUiState {
    data object IDLE : RecordUiState()
    data object RECORDING : RecordUiState()
    data object PAUSED : RecordUiState()
    data class Error(val message: String?) : RecordUiState()
}

class RecordingViewModel : ViewModel() {
    private val _recordingUiState = MutableStateFlow<RecordUiState>(RecordUiState.IDLE)
    val recordingUiState: StateFlow<RecordUiState> = _recordingUiState.asStateFlow()

    fun startRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type": "start"}""")
                _recordingUiState.value = RecordUiState.RECORDING
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type": "stop"}""")
                _recordingUiState.value = RecordUiState.IDLE
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun pauseRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type": "pause"}""")
                _recordingUiState.value = RecordUiState.PAUSED
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }


    fun resumeRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendHandshake()
                val response = WebSocketManager.receiveHandshakeResponse()
                if (response.status == "ok") {
                    WebSocketManager.sendMessage("""{"type": "resume"}""")
                    _recordingUiState.value = RecordUiState.RECORDING
                } else {
                    _recordingUiState.value = RecordUiState.Error("Handshake Failed")
                }
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Message")
            }
        }
    }

    fun sendGripperCommand(state: String) {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type":"gripper","state":"$state"}""")
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Message")
            }
        }
    }

}