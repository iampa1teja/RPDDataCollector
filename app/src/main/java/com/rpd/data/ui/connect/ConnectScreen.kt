package com.rpd.data.ui.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rpd.data.navigation.Routes

@Composable
fun ConnectScreen(
    navController: NavController
) {
    val viewModel: ConnectViewModel = viewModel()
    val uiState by viewModel.connectionUI.collectAsState()

    var url by remember {
        mutableStateOf("")
    }

    // Navigate when connected
    LaunchedEffect(uiState) {
        if (uiState is ConnectUiState.CONNECTED) {
            navController.navigate(Routes.RECORDING) {
                popUpTo(Routes.CONNECT) {
                    inclusive = true
                }
            }
        }
    }

    when (uiState) {

        is ConnectUiState.IDLE -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = {
                        Text("Enter Server URL")
                    }
                )

                Button(
                    onClick = {
                        viewModel.connectToServer(url)
                    }
                ) {
                    Text("Connect")
                }

                Text("Status: Idle")
            }
        }

        is ConnectUiState.CONNECTING -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator()

                Text("Connecting...")
            }
        }

        is ConnectUiState.CONNECTED -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Connected")
            }
        }

        is ConnectUiState.Error -> {

            val errorState = uiState as ConnectUiState.Error

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Error: ${errorState.message}")

                Button(
                    onClick = {
                        viewModel.disconnect()
                    }
                ) {
                    Text("Back")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConnectScreen() {
    ConnectScreen(
        rememberNavController()
    )
}