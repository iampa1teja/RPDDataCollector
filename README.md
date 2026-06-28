# RPD Data Collector

Android app for recording robot motion data using smartphone IMU sensors, streamed in real-time to a PC over WebSocket.

**Package:** `com.rpd.data`  
**Stack:** Kotlin, Jetpack Compose, Ktor WebSocket (OkHttp), Jetpack Navigation

---

## What It Does

Uses your phone's gyroscope + accelerometer to capture motion data during robot teleoperation and streams it to a Python server ([rpd-lib](link-to-python-repo)) running on your PC.

---

## App Flow

```
Screen 1: Enter server URL → Handshake → Connected
Screen 2: Hold 3s to Start → Recording → Pause/Resume → Hold 3s to Stop → Confirm
```

---

## WebSocket Protocol

**Handshake:**
```json
App  →  Server : {"type": "handshake", "role": "app"}
Server  →  App : {"type": "handshake", "status": "ok"}
```

**Sensor Stream:**
```json
{
  "type": "sensor",
  "timestamp": 1234567890123,
  "gyroX": 0.0, "gyroY": 0.0, "gyroZ": 0.0,
  "accelX": 0.0, "accelY": 0.0, "accelZ": 0.0
}
```

**Control Messages:**
```json
{"type": "start"}
{"type": "pause"}
{"type": "resume"}
{"type": "stop"}
{"type": "gripper", "state": "open"}
{"type": "gripper", "state": "close"}
```

---

## App States

**Connect Screen:**
```
IDLE → CONNECTING → CONNECTED → (navigate to Recording Screen)
                 ↘ ERROR
```

**Recording Screen:**
```
IDLE → (3s hold + confirm) → RECORDING → PAUSED → RECORDING
                                       ↘ (3s hold + confirm) → IDLE
              ↑ auto-pause on call/background (TODO)
```

---

## Sensor

- Reads `TYPE_GYROSCOPE` + `TYPE_ACCELEROMETER` at `SENSOR_DELAY_FASTEST`
- Requires `HIGH_SAMPLING_RATE_SENSORS` permission
- Pushes readings into a `Channel`
- `RecordingViewModel` collects from channel and streams via WebSocket
- Python lib handles downsampling

## Gripper

- Volume keys: Up → open, Down → close
- Only active during `RECORDING` state
- Future: configurable on-screen buttons (open, close, mid, custom)
- Gripper values defined in Python config

---

## Package Structure

```
com.rpd.data/
├── MainActivity.kt              ← hosts NavHost, intercepts volume keys
├── navigation/
│   └── AppNavigation.kt
├── model/
│   └── Messages.kt              ← HandshakeRequest/Response, SensorMessage, GripperMessage
├── network/
│   └── WebSocketManager.kt
├── sensor/
│   └── SensorDataManager.kt     ← reads IMU, pushes to Channel
└── ui/
    ├── connect/
    │   ├── ConnectScreen.kt
    │   └── ConnectViewModel.kt
    ├── recording/
    │   ├── RecordingScreen.kt
    │   └── RecordingViewModel.kt
    └── elements/
        └── HoldButton.kt        ← 3s hold with circular fill animation
```

---

## Requirements

- Android 8.0+
- WiFi connection (same network as PC)
- Gyroscope + Accelerometer sensors

## Permissions
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.HIGH_SAMPLING_RATE_SENSORS"/>
```

---

## Testing

A mock Python server is included for testing without the full Python library:
```bash
pip install websockets
python mock_server.py
```
Enter `ws://<your-pc-ip>:8765` in the app.

---

## Current Status

- [x] WebSocket connection + handshake
- [x] Sensor streaming (gyro + accel)
- [x] Start/Stop/Pause/Resume recording
- [x] 3s hold button with circular animation
- [x] Confirmation dialog
- [x] Gripper control via volume keys
- [ ] Auto-pause on interruptions (calls, backgrounding)
- [ ] Gripper preference on first launch (SharedPreferences)
- [ ] Sensor fusion
- [ ] Fixed episode length mode

