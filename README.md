# RPD Data Collector

Android app for recording robot motion data using smartphone IMU sensors, streamed in real-time to a PC over WebSocket.

**Package:** `com.rpd.data`  
**Stack:** Kotlin, Jetpack Compose, Ktor WebSocket, Jetpack Navigation

---

## What It Does

Uses your phone's gyroscope + accelerometer to capture motion data during robot teleoperation and streams it to a Python server running on your PC.

---

## App Flow

```
Screen 1: Enter server URL → Handshake → Connected
Screen 2: Start → Recording → Pause/Resume → Stop
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
  "gyro": {"x": 0.0, "y": 0.0, "z": 0.0},
  "accel": {"x": 0.0, "y": 0.0, "z": 0.0}
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
IDLE → RECORDING → PAUSED → RECORDING → IDLE
              ↘ ERROR
              ↑ auto-pause on call/background
```

---

## Package Structure

```
com.rpd.data/
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt
├── model/
│   └── Messages.kt
├── network/
│   └── WebSocketManager.kt
└── ui/
    ├── connect/
    │   ├── ConnectScreen.kt
    │   └── ConnectViewModel.kt
    └── recording/
        ├── RecordingScreen.kt
        └── RecordingViewModel.kt
```

---

## Requirements

- Android 8.0+
- WiFi connection
- Gyroscope + Accelerometer sensors

---

## Current Status

- [x] Messages.kt
- [x] WebSocketManager.kt
- [x] ConnectViewModel.kt
- [x] AppNavigation.kt
- [ ] MainActivity.kt
- [ ] ConnectScreen.kt
- [ ] RecordingScreen.kt
- [ ] RecordingViewModel.kt
- [ ] Sensor streaming
- [ ] Gripper control
- [ ] Auto-pause on interruption
- [ ] Sensor fusion
