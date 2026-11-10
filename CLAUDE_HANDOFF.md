# 📋 Project Handoff: "What's the Weather" Android App

> **For a new AI session:** read this whole file before changing code. It reflects
> the code as of **2026-11-11**. Where this doc and the code disagree, trust the code.

---

## 🧭 TL;DR

- Kotlin + Jetpack Compose Android weather app, **builds and runs** (v1.1.0 released).
- Active work branch: **`claude/festive-allen-9n3abt`** (one commit ahead of `main`:
  background flicker fix — **not yet tested on a device**).
- CI (GitHub Actions) builds a debug APK on every push to `main` and `claude/**`.
- Main open items: verify the flicker fix, decide on the notifications toggle,
  remove leftover dead code, performance pass. See **Open Work** below.

---

## 👤 User Context

- **Name:** Ahmet (GitHub: `Port2396`)
- **OS:** CachyOS (Arch-based), KDE. AUR helper: `yay`.
- **IDE:** Android Studio (at `/opt/android-studio/`), with the Claude agent panel.
- **Emulator:** Pixel 7A, API 35.
- **Prefers:** simple, clear explanations; honest feedback.

---

## 📁 Project Location

- **Local path:** `~/Documents/Weatherapp/` (moved — older notes said
  `~/Documents/Antigravity/Open Source App Forks/weather-master/`)
- **GitHub:** `https://github.com/Port2396/weather-master` (default branch `main`)
- `local.properties` (not in git): `sdk.dir=/home/ahmet/Android/Sdk`

---

## ⚙️ Environment — Read This If The Build Fails

**Gradle must run on Java 17–21.** Gradle 8.10.2 cannot run on Java 25/26
(Android Studio's embedded runtime is JBR 25).

- The Gradle daemon JVM is now pinned in `gradle/gradle-daemon-jvm.properties`
  (JetBrains Runtime 21). Android Studio downloads it to `~/.jdks/jbr-21.*`.
  The `foojay-resolver-convention` plugin in `settings.gradle.kts` lets Gradle
  download JDK toolchains on its own.
- Old symptom: *"Incompatible Gradle JVM version … Gradle 8.10.2 is incompatible
  with the Gradle JVM version 25"*. If it comes back: **Settings → Build,
  Execution, Deployment → Build Tools → Gradle → Gradle JDK → JBR 21 (or any
  JDK 17/21)**, then re-sync.
- Command line: the `gradlew` launcher also needs a JDK 17–21, e.g.
  `JAVA_HOME=~/.jdks/jbr-21.0.11 ./gradlew assembleDebug`.
- **Don't** accept Android Studio's "upgrade Gradle / AGP" prompts casually —
  AGP 8.7.3 is pinned to work with Gradle 8.10.2. Upgrading is a separate task.

Command-line build: `./gradlew assembleDebug`

---

## 🗂 What This Project Is

A ground-up Kotlin rewrite of a forked Java/XML weather app (the original code was
deleted).

| Item | Value |
|---|---|
| App name | What's the Weather |
| Package | `com.whatstheweather.app` |
| Version | 1.1.0 (versionCode 2) |
| Min / Target / Compile SDK | 26 / 35 / 35 |
| Java / JVM target | 17 |
| Release build | minify + shrink resources (ProGuard) |

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 (BOM 2024.12.01) |
| Architecture | MVVM + Clean Architecture (domain / data / presentation) |
| DI | Hilt 2.52 (KSP) |
| Async | Coroutines + StateFlow |
| Networking | Retrofit 2.11.0 + OkHttp 5.0.0-alpha.14 |
| Local DB | Room 2.6.1 (saved cities) |
| Preferences | DataStore 1.1.1 |
| Location | Play Services Location (FusedLocationProviderClient) |
| Navigation | Navigation Compose 2.8.5 |
| Permissions | Accompanist Permissions |
| Fonts | Outfit (headings/temps) + Inter (body) via Google Fonts |
| Build | AGP 8.7.3, Gradle 8.10.2, KTS, version catalog `gradle/libs.versions.toml` |

> Lottie and Coil are declared in `app/build.gradle.kts` but **not used anywhere**
> in the code. Weather icons and backgrounds are plain Compose (Canvas / vectors).

## 🌦 Data Source

**Open-Meteo only** (free, no API key):
- Forecast: `https://api.open-meteo.com/v1/`
- Air quality: `https://air-quality-api.open-meteo.com/v1/`
- City search: `https://geocoding-api.open-meteo.com/v1/`

All three are defined in `data/api/openmeteo/OpenMeteoService.kt`.
There is **no caching layer** — every load hits the network.

---

## 📐 Source Layout (47 Kotlin files)

```
app/src/main/java/com/whatstheweather/app/
├── WeatherApplication.kt, MainActivity.kt
├── di/            DatabaseModule, NetworkModule, RepositoryModule
├── data/
│   ├── api/openmeteo/   OpenMeteoService (forecast, AQI, geocoding), OpenMeteoDto
│   ├── local/           WeatherDatabase, dao/CityDao, entity/CityEntity
│   ├── datastore/       SettingsDataStore
│   └── repository/      Weather/City/Settings RepositoryImpl
├── domain/
│   ├── model/           WeatherData, Daily/HourlyForecast, AirQuality,
│   │                    WeatherCondition, City, AppSettings
│   ├── repository/      interfaces
│   └── usecase/         GetCurrentWeather, SearchCities, ManageCity
└── presentation/
    ├── AppThemeViewModel.kt        theme setting → MainActivity
    ├── theme/                      Color, Theme, Typography
    ├── navigation/                 NavGraph, Screen
    ├── util/                       UnitFormatter (°C/°F, wind), TimeFormatter (12/24h)
    └── ui/
        ├── home/       HomeScreen, HomeViewModel, HomeUiState
        ├── cities/     CityManagerScreen, CityManagerViewModel
        ├── settings/   SettingsScreen, SettingsViewModel
        └── common/     GlassCard, WeatherBackground, WeatherConditionIcon,
                        HourlyForecastRow, DailyForecastList
```

There are **no unit or instrumented tests** (`app/src/test` and `androidTest` don't exist).

---

## ✨ Features (working)

- Current conditions: temp, feels like, high/low, humidity, wind, UV, AQI,
  sunrise/sunset, precipitation chance
- Hourly (24h) and 10-day forecasts
- GPS location + city search + saved cities (Room), swipe between cities (HorizontalPager)
- Pull to refresh
- Glassmorphism cards over animated Canvas backgrounds (rain, snow, storm, sun,
  clouds, fog, stars), faded toward the bottom for legibility

**Settings that work:** temperature unit, wind unit, 12/24h time, theme
(Light = day look, Dark = night look, System = real time of day),
animated background on/off.

**Settings that don't work:** the **Notifications** toggle — saved, but nothing
reads it. No notification service or WorkManager job exists.

---

## ✅ Latest Change (branch `claude/festive-allen-9n3abt`, commit `2c911f0`)

Fix animated background flicker — **CI build passes; needs testing on the emulator**:

- `WeatherBackground.kt`: rain and snow used a looping transition, so every 1–3 s
  all particles teleported. Now driven by a continuous frame clock
  (`rememberElapsedSeconds()`); each particle wraps on its own.
- `WeatherBackground.kt`: clouds now start fully off-screen instead of popping in.
- `HomeScreen.kt`: keeps the last known condition while loading, so refresh / city
  swipe no longer flashes the sunny background.
- `HomeViewModel.kt`: `loadWeather` cancels the previous request, so a slow
  response can't show the wrong city's weather.
- `.github/workflows/android.yml`: now triggers on `main` and `claude/**`
  (it was set to `master`, which doesn't exist, so CI had never run).

**How to test:** run on emulator; watch rain/snow/clouds for jumps; pull to refresh
(background should not flash); swipe quickly between several cities.
If good → merge to `main`.

---

## 🎯 Open Work (in suggested order)

1. **Verify the flicker fix** on the emulator, then merge to `main`.
2. **Notifications toggle** — decide: implement severe-weather alerts
   (WorkManager + Open-Meteo, needs `POST_NOTIFICATIONS` runtime prompt) or
   remove the toggle. Not yet decided by the user.
3. **Dead code cleanup:**
   - `AppSettings` / `SettingsDataStore` still hold `apiProvider`,
     `openWeatherMapApiKey`, `weatherApiKey`, `tomorrowApiKey` and the
     `WeatherApiProvider` enum — their UI was removed in 1.1.0.
   - Unused dependencies: Lottie, Coil.
4. **Performance pass** — animations, recompositions.
5. **Light theme** — weather screens use hardcoded glass-over-gradient colours, so
   Material light colours have little effect. Proper light mode needs a redesign.
6. **Tests** — none exist; `UnitFormatter` / `TimeFormatter` are easy first targets.
7. **Release prep** — bump version, update `CHANGELOG.md`.

Known minor issue: no theme quick-toggle on the home screen (only the Settings cog).

---

## 🔑 Git

- Remote: `git@github.com:Port2396/weather-master.git` (SSH). GitHub Desktop also installed.
- Get the latest work branch:
  `git fetch origin && git checkout claude/festive-allen-9n3abt`

---

## 💬 History

| Date | What happened |
|---|---|
| Apr 2026 | Full rewrite written (~44 files); never built (no Android SDK). |
| May 8–20, 2026 | Android Studio + SDK installed, repo pushed to GitHub. First successful build after switching Java 26 → 17; fixed pull-to-refresh API. |
| May 21, 2026 | Settings wired up: units, time format, theme. Cache duration setting removed. |
| Jun 11, 2026 | **v1.1.0**: Outfit/Inter fonts, spacing, card shadows, brighter icons, background fade, animation toggle, theme drives day/night background. Removed broken Advanced Settings (API provider/keys). Added CHANGELOG. |
| Nov 11, 2026 | Background flicker fix + CI fixed (branch `claude/festive-allen-9n3abt`). Project moved to `~/Documents/Weatherapp`; fixed "Incompatible Gradle JVM" by setting Gradle JDK to 17. |

---

*Last updated: 2026-11-11*
