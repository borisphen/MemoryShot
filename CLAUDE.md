# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Working Agreement

- **Любые изменения — только с явного согласия.** Перед тем как что-то менять, описать что именно будет изменено и дождаться подтверждения. До подтверждения не трогать ничего.
- **Вносить только явно согласованные изменения.** Не рефакторить, не переименовывать, не удалять ничего сверх того, что обсуждено.
- **Заделы на будущее не трогать.** Код помеченный как заготовка или placeholder (например незаполненные flow, пустые модули) — оставлять как есть.
- **Удалять только подтверждённо мёртвый код** — то есть код, который гарантированно не будет использован и не является заделом на будущее, и только после явного подтверждения.

## App Icon Design

Иконка выбрана и зафиксирована. **Не менять без явного согласования.**
Полная спека: [docs/icon-design.md](docs/icon-design.md)

## Build & Development Commands

**Prerequisites:** Add your Groq API key to `local.properties` before building:
```
GROQ_API_KEY=your_key_here
```

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single module's tests
./gradlew :core:core-domain:test

# Run Detekt static analysis
./gradlew detekt

# Install debug APK on connected device
./gradlew installDebug
```

Requires **JDK 17**. `compileSdk` and `targetSdk` are both 36, `minSdk` is 26.

CI runs on push/PR to `master`, builds a debug APK, creates a GitHub release, and notifies via Telegram. The `GROQ_API_KEY` must be set as a GitHub Actions secret in the `Environment` environment.

## Architecture Overview

MemoryShot is a multi-module Android app that captures **voice notes enriched with screen context**. When the user speaks, the foreground service simultaneously captures a screenshot, runs OCR on it, combines the voice + screen text, sends it to the Groq LLM API, and saves the structured result (title, summary, tags) to a local Room database.

### Module Structure

```
app/                          — Application shell, DI root, MainActivity, ForegroundService
core/
  core-domain/                — Interfaces, use cases, domain models (no Android deps)
  core-data/                  — Implementations: Room DB, Retrofit/Groq, OCR, speech
  core-ui/                    — Shared Compose theme, components
feature/
  feature-voice/
    domain/                   — (empty placeholder)
    data/                     — (empty placeholder)
    presentation/             — AiComponent + Dependencies interface (currently unused)
  feature-history/
    presentation/             — History screen: HistoryViewModel, HistoryComponent
utils/
  util/                       — Either<L,R>, apiCall{}, Flows, DateTime helpers
  util-ui/                    — composeViewModel{}, activityViewModel{}, ContextUtil
  util-platform/              — ScreenshotSaver, BitmapUtils, Log wrapper
build-logic/                  — Convention plugins (AndroidApplicationConventionPlugin,
                                AndroidLibraryConventionPlugin, ComposeConventionPlugin)
```

### Dependency Injection (Dagger 2, not Hilt)

The project uses a **component-dependencies pattern** for feature modules:

1. **`AppComponent`** (`@Singleton`) is the root graph. It implements `HistoryDependencies` (and could implement others), exposing selected bindings to feature subgraphs.
2. Feature modules define a `*Dependencies` interface listing what they need from the parent graph (e.g., `HistoryDependencies` exposes `MemoryNoteRepository`).
3. Feature components are created manually: `HistoryComponent.factory().create(appComponent)`.
4. `AppComponent` is created in `MemoryApplication` and accessed via `MemoryApplication.appComponent`.
5. `ForegroundMemoryShotService` uses member injection via `appComponent.inject(this)`.

`MainViewModel` uses `@AssistedInject` with `ServiceController` as the assisted parameter. `HistoryViewModel` uses a manual inner `Factory` class.

### Core Data Flow

**Voice capture pipeline** (triggered by `ACTION_CAPTURE` intent to `ForegroundMemoryShotService`):
1. `AndroidSpeechRecognizerWrapper` listens for speech (Russian `ru-RU` locale, restarts on error)
2. On result: `ScreenCaptureManager.captureOneFrameOrNull()` grabs one frame from a `MediaProjection`-backed `VirtualDisplay` via a conflated `Channel<Bitmap>` (3 s timeout)
3. `ProcessScreenshotUseCase`: runs `OcrEngineImpl` (Google ML Kit) on the bitmap, then saves the image via `ScreenshotSaver`
4. `CreateNoteWithContextUseCase`: builds a combined prompt (voice + OCR text), calls `AiRepositoryImpl.processQuestion()` with `Prompt.QUESTION_ANALYZER`
5. `AiRepositoryImpl` calls Groq API (`llama-3.3-70b-versatile`, temp=0.2, 512 tokens) expecting a JSON `{"title","summary","tags"}` response
6. Parsed result is saved as `MemoryNote` to Room via `MemoryNoteRepositoryImpl`
7. Speech recognition restarts after 200 ms delay

**MediaProjection setup**: The user grants screen capture permission in `MainActivity`. The resulting `resultCode` + `Intent` are persisted to `SharedPreferences` via `PreferenceStorage`, then retrieved when `startService()` is called from `MainViewModel`.

### Navigation

Uses **androidx Navigation3** (alpha, `1.0.0-alpha07`) with a `NavBackStack`. Two destinations are defined as `@Serializable data object` keys (`ScreenA` = main, `ScreenB` = history) in `MainActivity`.

### OCR Engines

Two `OcrEngine` implementations are provided, both bound with `@Named`:
- `@Named("TextRecognition")` → `OcrEngineImpl` (Google ML Kit, active in production via `AppModule`)
- `@Named("Tess")` → `TessOcrEngineImpl` (Tesseract via `tess-two`)

### Error Handling

All repository/use-case results use a custom `Either<L, R>` type (in `:utils:util`) modelled on Arrow. Network calls go through `apiCall { }` which wraps the Retrofit call in `Either.catch { }`. Use `.fold(ifLeft = ..., ifRight = ...)` to handle results.

### Build Conventions

Custom Gradle plugins in `build-logic/` standardise SDK versions and Kotlin/Compose setup across all modules:
- `memoryshot.android.application` — applies to `:app`
- `memoryshot.android.library` — applies to all library modules
- `memoryshot.compose` — enables Compose + adds BOM dependencies

### Detekt

Static analysis is configured via `config/detekt.yml` with `maxIssues: 0` (build fails on any finding). The config is referenced in each module's `build.gradle.kts` via `detekt { config.setFrom(files("../config/detekt.yml")) }`.
