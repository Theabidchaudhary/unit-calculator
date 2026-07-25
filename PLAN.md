# Unit Calculator — Project Plan

> Offline-first Android app for managing multiple electricity meters against a monthly unit limit.
> Kotlin · Jetpack Compose · Material 3 · MVVM · Room · Coroutines/StateFlow · DataStore.
>
> **Status: awaiting approval. No app code written yet.**

---

## 0. Repository note

The existing `orwyx` repo is a Next.js website. The Android app is unrelated tech, so it will
live in its own self-contained Gradle project under **`/android`** to avoid polluting the web
project. Everything below is rooted at `/android`.

---

## 1. Complete Feature List

### Core (MVP — build first)
1. **Meter CRUD** — create/edit/delete meters; one default meter on first launch; unlimited meters.
2. **Meter fields** — name, masked reference number (`********4827`, tap-to-reveal), provider,
   target limit, previous reading, current reading.
3. **Live calculations** — consumed, remaining, % used (never manual).
4. **Animated progress bar** with a smooth 7-stop gradient (deep-green → dark-red).
5. **Status badge** — Safe / Moderate / Warning / Critical / Exceeded, auto-derived.
6. **Monthly reset** — current→previous, clear current, snapshot to history.
7. **Persistent auto-save** — every edit committed to Room immediately.
8. **Dashboard summary cards** — totals + safe/warning/critical counts + daily-avg + projection.
9. **Search & sort** — by name / reference; sort by name, remaining, consumption, danger, newest, oldest.
10. **Settings** — theme (light/dark/system), reading date, default target, decimals toggle,
    export/import backup, about, privacy, reset-all.

### Planning & Insight (Phase 2)
11. **Planning calendar** — from reading date to next reading date, per-day expected vs actual, colored.
12. **Forecast engine** — avg daily usage, projected end-of-month, expected overage, safety status.
13. **Charts** — Target vs Actual, Daily Usage, Monthly History, Consumption Trend (Compose-drawn).
14. **Reading history** — one record per completed month; never auto-deleted.

### Optional / Online (Phase 3, isolated modules)
15. **Bill sync** — fetch bill by reference number where a provider exposes a no-login endpoint.
16. **Bill scanner** — photo/PDF → OCR extract fields → confirm before save.
17. **Backup** — export/import full JSON snapshot to user-chosen file.

### Future-ready hooks (designed for, not built)
Notifications, widgets, Wear OS, Drive backup, CSV export, analytics, AI prediction,
multiple planning strategies. Architecture leaves seams for each (see §11).

---

## 2. Folder Structure

```
android/
├── build.gradle.kts / settings.gradle.kts / gradle/libs.versions.toml   (version catalog)
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/orwyx/unitcalculator/
│           ├── UnitCalculatorApp.kt              (Application, DI graph init)
│           ├── di/                               (Hilt modules: Db, Repo, Engine, Prefs)
│           ├── core/
│           │   ├── util/                         (Result, formatters, date utils, Mask)
│           │   └── model/                        (MeterStatus enum, gradient stops)
│           ├── data/
│           │   ├── local/
│           │   │   ├── AppDatabase.kt
│           │   │   ├── dao/                       (MeterDao, HistoryDao, BillDao)
│           │   │   ├── entity/                    (MeterEntity, HistoryEntity, BillEntity)
│           │   │   └── converters/                (BigDecimal/Date TypeConverters)
│           │   ├── prefs/                         (SettingsDataStore)
│           │   ├── mapper/                        (entity ⇄ domain)
│           │   └── repository/                    (MeterRepositoryImpl, HistoryRepositoryImpl…)
│           ├── domain/
│           │   ├── model/                         (Meter, ReadingHistory, Bill, Settings)
│           │   ├── repository/                    (interfaces)
│           │   └── engine/
│           │       ├── CalculationEngine.kt       (consumed/remaining/percent/status/color)
│           │       ├── PlanningEngine.kt          (calendar schedule, expected-per-day)
│           │       └── ForecastEngine.kt          (projection, overage)
│           ├── sync/                              (BillSyncEngine + provider adapters, isolated)
│           ├── ocr/                               (BillScannerEngine — ML Kit / PDF)
│           ├── backup/                            (BackupManager — JSON export/import)
│           └── ui/
│               ├── theme/                         (Color, Type, Shape, Theme, Neumorph modifiers)
│               ├── navigation/                    (Routes, NavGraph, BottomBar)
│               ├── components/                    (reusable composables — see §10)
│               └── screens/
│                   ├── meters/     (list, detail, add/edit sheet, VM)
│                   ├── planning/   (calendar, forecast, charts, VM)
│                   ├── history/    (VM)
│                   ├── settings/   (VM)
│                   └── search/
└── (unit tests under app/src/test, instrumented under app/src/androidTest)
```

---

## 3. Database Schema (Room)

**`meters`**
| column | type | notes |
|---|---|---|
| id | Long PK autoGen | |
| name | String | |
| referenceNumber | String | stored full; masked in UI |
| providerId | String | FK to provider registry (code, not table) |
| targetLimit | Double | > 0 |
| previousReading | Double | |
| currentReading | Double | ≥ previous |
| cycleStartDate | Long (epochDay) | derived from reading date |
| createdAt / updatedAt | Long | for sort newest/oldest |
| sortOrder | Int | manual ordering (future) |

**`reading_history`** (one per completed month)
| id PK | meterId FK→meters | monthLabel | previousReading | currentReading | unitsConsumed | target | remaining | billAmount? | avgDailyUsage | status | closedAt |

**`bills`** (synced/scanned)
| id PK | meterId FK | billMonth | readingDate | previousReading | currentReading | unitsConsumed | billAmount | source (SYNC/OCR/MANUAL) | fetchedAt |

**`daily_readings`** (optional, Phase 2 — for actual-per-day planning input)
| id PK | meterId FK | date (epochDay) | reading |

Indices on `meterId`. Cascade delete history/bills with meter. Migrations versioned from v1.
Providers are a **code registry** (sealed/enum + data), not a DB table, so new companies are a one-file change.

---

## 4. App Architecture (MVVM + Repository + Engines)

```
Compose UI  ──state──>  ViewModel (StateFlow<UiState>)
    ▲                        │ calls
    │ events                 ▼
    └──────────────  Repository (interface)  ──>  Room DAO / DataStore / Sync / OCR
                              │ uses
                              ▼
                    Domain Engines (pure, testable):
                    CalculationEngine · PlanningEngine · ForecastEngine
```

- **UI** is stateless; hoisted state; single immutable `UiState` per screen.
- **ViewModels** expose `StateFlow`, receive intents, never touch Android/DB directly.
- **Repositories** are interfaces in `domain`, implemented in `data`; the only DB gateway.
- **Engines** are pure Kotlin (no Android deps) → fast unit tests, reusable, SOLID.
- **DI** via Hilt. **Threading** via coroutines + `Dispatchers.IO` in repos; Room returns `Flow`.
- Online/OCR/backup modules are **isolated behind interfaces** so they can fail/upgrade independently.

---

## 5. Screen Flow

```
First launch ──> Meters (Dashboard + 1 seeded meter)
Meters ──FAB(+)──> Add/Edit Meter sheet ──save──> Meters
Meters ──tap card──> Meter Detail (full number, history, reset, charts)
Meters ──reset──> confirm ──> snapshot to History
Bottom nav ──> Planning (calendar + forecast + charts)
Top bar ──> Settings ; Top bar ──> Search (filter Meters)
Settings ──> Export/Import, Theme, Reading Date, About/Privacy, Reset-all
Detail ──> Bill Sync / Bill Scanner (optional)
```

---

## 6. Navigation Map

- **Bottom nav (2 tabs):** `Meters`, `Planning` — floating glass bar, raised center accent per reference.
- **Top app bar (persistent):** `Search` + `Settings` actions.
- **Nav Compose routes:**
  `meters` · `meterDetail/{id}` · `meterEdit?id={id}` · `planning` · `history` ·
  `settings` · `search` · `billScan/{meterId}` · `about` · `privacy`.
- Type-safe routes (Kotlin serialization nav). Shared-element/fade transitions between screens.

---

## 7. UI Component Hierarchy

```
UnitCalculatorTheme
└── Scaffold(topBar, bottomBar=GlassBottomNav, fab=AddMeterFab)
    ├── MetersScreen
    │   ├── DashboardSummary (Lazy row of AnimatedSummaryCard ×N)
    │   ├── SearchSortRow
    │   └── LazyColumn of MeterCard
    │       └── MeterCard { Header(name, StatusBadge, MaskedReference)
    │                        AnimatedProgressBar
    │                        StatsRow(consumed/remaining/percent)
    │                        ExpandableActions(reset, edit, sync) }
    ├── PlanningScreen
    │   ├── ForecastHeader (projection cards)
    │   ├── PlanningCalendar (grid of ColoredDayCell)
    │   └── ChartsSection (TargetVsActual, DailyUsage, Trend)
    ├── HistoryScreen → HistoryRecordCard list
    └── SettingsScreen → SettingRow / ThemeSelector / etc.
```

---

## 8. Implementation Roadmap

- **M0 — Scaffolding:** Gradle + version catalog, Hilt, theme system (colors/type/shape,
  neumorph+glass modifiers), navigation shell, bottom nav, empty screens. *(compiles & runs)*
- **M1 — Data layer:** Room entities/DAOs/db, DataStore settings, repositories, mappers, seed meter.
- **M2 — Calculation engine + Meter list/card:** live math, gradient progress, status badge,
  masked reference, auto-save. Unit tests for engine.
- **M3 — Add/Edit + validation + Detail screen + monthly reset.**
- **M4 — Dashboard summary cards + search/sort.**
- **M5 — Planning + Forecast engines + calendar + charts.**
- **M6 — History persistence & screen.**
- **M7 — Settings complete + Backup export/import (JSON).**
- **M8 — Optional: Bill sync adapters + OCR scanner (isolated, feature-flagged).**
- **M9 — Polish: animations, app icon, empty states, accessibility, perf pass (100+ meters).**

Each milestone is committed separately and left in a runnable state.

---

## 9. List of Reusable Components

Buttons: `PrimaryButton`, `SoftIconButton`, `AddMeterFab`.
Surfaces: `NeumorphicCard`, `GlassCard`, `SectionHeader`, `ExpandableCard`.
Data viz: `AnimatedProgressBar`, `StatusBadge`, `GradientRing`, `MiniChart`,
`LineChart`, `BarChart`, `AnimatedCounterText`.
Inputs: `LabeledTextField`, `NumberField` (decimal-aware, validated), `ProviderPicker`,
`ReadingDatePicker`, `MaskedReferenceField`.
Nav/scaffold: `GlassBottomNav`, `AppTopBar`, `SearchBar`, `SortMenu`.
Feedback: `EmptyState`, `ConfirmDialog`, `LoadingShimmer`, `SnackbarHost`.
Layout: `SummaryCardRow`, `ColoredDayCell`, `MeterCard`.

---

## 10. Design System (from reference — inspiration only)

- **Palette:** blue accent (`#2F4CDD`-ish primary, lighter `#5B7CFA`), soft off-white surfaces in
  light, deep navy/charcoal in dark; multi-stop status gradient green→red.
- **Shape:** large radii (20–28dp), pill buttons, floating glass bottom bar w/ raised center.
- **Elevation:** soft dual-shadow neumorphism + translucent glass overlays (blur/scrim).
- **Type:** clear hierarchy, large friendly numerals for readings/percentages.
- **Motion:** animated color transitions, counters, progress fills, expandable cards, screen fades.
- Full light + dark parity.

---

## 11. Future-Ready Seams

- **Notifications/Widgets/Wear:** engines are pure + repo is Flow-based → drive a notifier/glance
  widget with zero UI-layer changes.
- **Drive/CSV backup:** `BackupManager` writes a serializable snapshot behind a `BackupTarget`
  interface (Local now; Drive/CSV later).
- **Provider sync:** each provider is a `BillProvider` adapter behind `BillSyncEngine`.
- **Planning strategies:** `PlanningStrategy` interface (sequential now; proportional/weighted later).
- **AI prediction:** `ForecastEngine` behind interface → swap heuristic for ML model later.

---

## 12. Suggestions to Improve (kept simple)

1. **Cost estimate per meter** using provider slab tariffs — high value for the "stay under limit" goal.
2. **"Safe daily budget" chip** on each card: remaining ÷ days-left — actionable, one line of math.
3. **Home-screen widget** showing worst meter's status (uses existing engine; small add later).
4. **Local reminder notification** on reading date ("time to log readings") — offline, no account.
5. **Undo on reset/delete** via snackbar — prevents accidental data loss.
6. **Per-meter color/emoji tag** for fast visual scanning of many meters.
7. **Bill-photo attachment** stored locally even when OCR isn't possible — still useful record.

All are additive and respect offline-first / no-tracking constraints.
```
