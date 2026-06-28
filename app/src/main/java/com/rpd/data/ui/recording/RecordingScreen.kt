package com.rpd.data.ui.recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rpd.data.sensor.SensorDataManager
import com.rpd.data.ui.elements.HoldButton

@Composable
fun RecordingScreen(
    navController: NavController,
    viewModel: RecordingViewModel
) {
    val recordingUiState by viewModel.recordingUiState.collectAsState()
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(recordingUiState) {
        when (recordingUiState) {
            is RecordUiState.RECORDING -> {
                SensorDataManager.startListening(context)
            }

            is RecordUiState.IDLE, is RecordUiState.PAUSED -> {
                SensorDataManager.stopListening()
            }

            else -> {}
        }
    }

    if (showConfirmDialog) {
        AlertDialog(onDismissRequest = {
            showConfirmDialog = false
        }, title = {
            Text("Are you sure?")
        }, confirmButton = {
            TextButton(
                onClick = {
                    pendingAction?.invoke()
                    showConfirmDialog = false
                }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showConfirmDialog = false
                }) {
                Text("Cancel")
            }
        })
    }
    when (recordingUiState) {
        is RecordUiState.IDLE -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HoldButton(
                    label = "Hold to Start", onHoldComplete = {
                        showConfirmDialog = true
                        pendingAction = {
                            viewModel.startRecording()
                        }
                    })
            }
        }

        is RecordUiState.RECORDING -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    Text("🔴")
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text("Recording")
                }
                HoldButton(
                    label = "Hold to Stop", onHoldComplete = {
                        showConfirmDialog = true
                        pendingAction = {
                            viewModel.stopRecording()
                        }
                    })
                Button(
                    onClick = {
                        viewModel.pauseRecording()
                    }) {
                    Text("Pause")
                }
            }
        }

        is RecordUiState.PAUSED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Paused")
                Button(
                    onClick = {
                        viewModel.resumeRecording()
                    }) {
                    Text("Resume")
                }
                HoldButton(
                    label = "Hold to Stop", onHoldComplete = {
                        showConfirmDialog = true
                        pendingAction = {
                            viewModel.stopRecording()
                        }
                    })
            }
        }

        is RecordUiState.Error -> {
            val error = recordingUiState as RecordUiState.Error
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${error.message}")
                Button(
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Text("Back")
                }
            }
        }
    }
}