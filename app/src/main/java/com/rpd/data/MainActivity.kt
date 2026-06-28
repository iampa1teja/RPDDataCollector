package com.rpd.data

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rpd.data.navigation.AppNavigation
import com.rpd.data.ui.theme.RPDDataCollectorTheme
import com.rpd.data.ui.recording.RecordingViewModel
import androidx.activity.viewModels
import com.rpd.data.ui.recording.RecordUiState

class MainActivity : ComponentActivity() {
    private val recordingViewModel: RecordingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPDDataCollectorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(recordingViewModel = recordingViewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (recordingViewModel.recordingUiState.value !is RecordUiState.RECORDING) {
            return super.onKeyDown(keyCode, event)
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                recordingViewModel.sendGripperCommand("open")
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                recordingViewModel.sendGripperCommand("close")
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}