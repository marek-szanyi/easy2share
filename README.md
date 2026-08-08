# easy2share

> Share clipboard & files from Android to any device — no cloud, no installs.

---

**easy2share** runs an encrypted local-network server on your phone. Open the link on your PC, scan the QR code once to establish a secure session, and start sharing instantly.

![easy2share home screen](docs/screenshot.png)

## Features

- 📋 **Clipboard sharing** — push text to any browser in one tap
- 🔒 **End-to-end encrypted** — TLS + session key via QR code scan
- 🌐 **Zero server** — everything stays on your local network
- 📦 **No PC software** — just a browser tab

## Tech stack

`Kotlin` · `Jetpack Compose` · `Ktor / Netty` · `Hilt` · `CameraX` · `ML Kit`

## Requirements

- Android 15+ (API 35)
- Wi-Fi

## Build

```sh
./gradlew assembleRelease
```

## License

[MIT](LICENSE.md) © 2026 Eaxor LLC
