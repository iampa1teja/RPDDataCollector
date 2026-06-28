package com.rpd.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.rpd.data.model.SensorMessage
import kotlinx.coroutines.channels.Channel

object SensorDataManager : SensorEventListener {
    val sensorChannel = Channel<SensorMessage>(
        capacity = Channel.UNLIMITED
    )
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    private val gyroValues = FloatArray(3)
    private val accelValues = FloatArray(3)

    fun startListening(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager?.registerListener(
            this,
            gyroscope,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type){
            Sensor.TYPE_GYROSCOPE -> {
                gyroValues[0] = event.values[0]
                gyroValues[1] = event.values[1]
                gyroValues[2] = event.values[2]
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelValues[0] = event.values[0]
                accelValues[1] = event.values[1]
                accelValues[2] = event.values[2]
            }
        }
        val message = SensorMessage(
            gyroX = gyroValues[0],
            gyroY = gyroValues[1],
            gyroZ = gyroValues[2],
            accelX = accelValues[0],
            accelY = accelValues[1],
            accelZ = accelValues[2]
        )

        sensorChannel.trySend(message)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // skip
    }
}