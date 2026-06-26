# RPD Data Collector

Android app for recording robot motion data using smartphone IMU sensors, streamed in real-time to a PC over WebSocket.

**Package:** `com.rpd.data`  
**Stack:** Kotlin, Jetpack Compose, Ktor WebSocket, Jetpack Navigation

---

## What It Does

Uses your phone's gyroscope + accelerometer to capture motion data during robot teleoperation and streams it to a Python server ([rpd-lib](link-to-python-repo)) running on your PC.

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
```
DISCONNECTED → CONNECTED → RECORDING → PAUSED → RECORDING → STOPPED
                                ↑ auto-pause on call/background
```

---

## Package Structure

```
com.rpd.data/
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt
├── ui/
│   └── connect/
│       ├── ConnectScreen.kt
│       └── ConnectViewModel.kt
├── network/
│   └── WebSocketManager.kt
└── model/
    └── Messages.kt
```

---

## Requirements

- Android 8.0+
- WiFi connection
- Gyroscope + Accelerometer sensors

---
