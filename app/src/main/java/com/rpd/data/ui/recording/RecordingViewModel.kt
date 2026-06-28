package com.rpd.data.ui.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpd.data.model.SensorMessage
import com.rpd.data.network.WebSocketManager
import com.rpd.data.sensor.SensorDataManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

sealed class RecordUiState {
    data object IDLE : RecordUiState()
    data object RECORDING : RecordUiState()
    data object PAUSED : RecordUiState()
    data class Error(val message: String?) : RecordUiState()
}

class RecordingViewModel : ViewModel() {
    private val _recordingUiState = MutableStateFlow<RecordUiState>(RecordUiState.IDLE)
    val recordingUiState: StateFlow<RecordUiState> = _recordingUiState.asStateFlow()
    private var sensorJob: Job? = null

    private fun startSensorCollection() {
        sensorJob?.cancel()
        sensorJob = viewModelScope.launch {
            try {
                for (message in SensorDataManager.sensorChannel) {
                    val json = Json.encodeToString<SensorMessage>(message)
                    WebSocketManager.sendMessage(json)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Error")
            } finally {
                sensorJob = null
            }
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type":"start"}""")
                _recordingUiState.value = RecordUiState.RECORDING
                startSensorCollection()
            } catch (e: Exception) {
                _recordingUiState.value =
                    RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type":"stop"}""")
                _recordingUiState.value = RecordUiState.IDLE
                sensorJob?.cancel()
                sensorJob = null
            } catch (e: Exception) {
                _recordingUiState.value = RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun pauseRecording() {
        viewModelScope.launch {
            try {
                sensorJob?.cancel()
                sensorJob = null
                WebSocketManager.sendMessage("""{"type":"pause"}""")
                _recordingUiState.value = RecordUiState.PAUSED
            } catch (e: Exception) {
                _recordingUiState.value =
                    RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun resumeRecording() {
        viewModelScope.launch {
            try {
                WebSocketManager.sendHandshake()
                val response = WebSocketManager.receiveHandshakeResponse()
                if (response.status == "ok") {
                    WebSocketManager.sendMessage("""{"type":"resume"}""")
                    _recordingUiState.value = RecordUiState.RECORDING
                    startSensorCollection()
                } else {
                    _recordingUiState.value =
                        RecordUiState.Error("Handshake failed")
                }
            } catch (e: Exception) {
                _recordingUiState.value =
                    RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun sendGripperCommand(state: String) {
        viewModelScope.launch {
            try {
                WebSocketManager.sendMessage("""{"type":"gripper","state":"$state"}""")
            } catch (e: Exception) {
                _recordingUiState.value =
                    RecordUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}