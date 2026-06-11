# Changelog

All notable changes to "What's the Weather" are documented here.
This project follows [Semantic Versioning](https://semver.org/).

## [1.1.0] — 2026-06-11

First working, polished build. The app now builds, runs, and the settings
actually affect the UI.

### Added
- **Unit conversion** — temperature (°C / °F) and wind speed (km/h / mph / m/s)
  now convert live based on the user's preference (`UnitFormatter`).
- **Time format** — 12h / 24h setting now applied to the hourly forecast and
  sunrise/sunset times (`TimeFormatter`).
- **Light / Dark / System theme** — now visibly drives the background:
  Light = day look, Dark = night look, System = follows the real time of day.
- **Animated Background toggle** — new switch in Settings to turn the rain/snow/
  cloud effects on or off (also a battery saver).
- **New typography** — Outfit (headings + temperature) and Inter (body/labels)
  via Google Fonts.

### Changed
- **Readability** — animated weather effects now fade toward the bottom of the
  screen so cards and text stay legible.
- **Weather icons** — replaced the dim grey-green condition icons with brighter,
  clearer ones.
- **UI spacing** — more consistent padding and breathing room across the home
  screen; subtle drop shadows on glass cards for depth.

### Removed
- **Cache Duration setting** — removed (no caching layer was implemented).
- **Advanced Settings section** (API provider switcher + API key fields) —
  removed; it was non-functional and caused a crash.

### Fixed
- App now compiles and runs (was never successfully built before).
- Fixed a crash when opening the API provider options.
- Resolved Compose pull-to-refresh API incompatibility
  (`PullToRefreshContainer` → `PullToRefreshBox`).

## [1.0.0] — 2026-04

### Added
- Initial ground-up rewrite: Kotlin, Jetpack Compose, Material 3, MVVM + Clean
  Architecture, Hilt, Open-Meteo API. ~44 source files. (Not yet buildable.)
