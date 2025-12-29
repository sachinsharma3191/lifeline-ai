# LifelineAI

LifelineAI is a Kotlin Multiplatform (KMP) + Compose Multiplatform project that targets:

- Android
- Desktop (JVM)
- Web (Kotlin/JS)
- Server (Ktor)

It demonstrates a cross-platform app architecture with shared domain logic, persistence via SQLDelight, and a simple backend API.

## Project structure

- [/composeApp](./composeApp/src) contains the Compose Multiplatform UI.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that's common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the
      folder name.
      For example, if you want to edit the Desktop (JVM) specific part,
      the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

- [/server](./server/src/main/kotlin) is a Ktor server application (API).

- [/shared](./shared/src) contains shared code used by all targets:
  - domain models
  - repositories
  - view models
  - SQLDelight database schema and queries
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

## Features

- **Shared persistence** using SQLDelight
  - Finance: transactions + financial goals
  - Health: symptoms
  - Learning: goals
  - Services: community services
- **CRUD UI (Android + Desktop)**
  - Add and edit entries for Finance/Health/Learning
- **Services search + detail view**
  - Search-only list (hidden until you type)
  - Click a service to open a detail screen
  - Click the address to open a Google Maps search link
  - Demo map placeholder on the detail screen
- **Backend server (Ktor)**
  - Basic JSON endpoints under `/api/v1/*`

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run
widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
  To install directly to a running emulator/device:
  ```shell
  ./gradlew :composeApp:installDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run
widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run
widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run
widget
in your IDE's toolbar or run it directly from the terminal:

- JS target:
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:jsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
      ```

Note: the Wasm target/task is not enabled by default in this repo.

---

## Quick Start

### Prerequisites
- Java 21 or higher
- Android SDK (for Android development)
- Node.js (for web development)

### Build All Targets
```shell
./gradlew clean build
```

### Start Applications

**Server (Backend):**
```shell
./gradlew :server:run
```
Server will start on http://localhost:8080

To run the server on a different port:

```shell
./gradlew :server:run -Dserver.port=9090
```

Or:

```shell
PORT=9090 ./gradlew :server:run
```

**Desktop Application:**
```shell
./gradlew :composeApp:run
```

**Web Application:**
```shell
./gradlew :composeApp:jsBrowserDevelopmentRun
```
Web app will be available at http://localhost:8081

**Android Application:**
```shell
./gradlew :composeApp:assembleDebug
```
Install the generated APK on your device or emulator.