package com.rpd.data.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HoldButton(
    label: String,
    onHoldComplete: () -> Unit,
    enabled: Boolean = true,
    idleColor: Color = Color.Red,
    activeColor: Color = Color.Green
) {

    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "Hold Progress"
    )
    val scope = rememberCoroutineScope()
    var holdJob by remember {
        mutableStateOf<Job?>(null)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .pointerInput(enabled) {
                    detectTapGestures(
                        onPress = {
                            if (!enabled) return@detectTapGestures
                            progress = 0f
                            holdJob = scope.launch {
                                repeat(60) {
                                    delay(50.milliseconds)
                                    progress += 50f / 3000f
                                    if (progress >= 1f) {
                                        progress = 1f
                                        onHoldComplete()
                                        return@launch
                                    }
                                }
                            }
                            tryAwaitRelease()
                            holdJob?.cancel()
                            if (progress < 1f) {
                                progress = 0f
                            }
                        }
                    )
                }
        ) {
            drawCircle(
                color = idleColor,
                style = Stroke(
                    width = 8.dp.toPx()
                )
            )
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(
                    width = 8.dp.toPx()
                )
            )
        }
        Text(text = label)
    }
}