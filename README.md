# Unit Calculator (Android)

Offline-first electricity meter manager for people in Pakistan who run several meters against a
monthly unit limit. It eliminates manual calculations, remembers every reading on-device, and
makes it visually obvious whether consumption is safe or approaching the limit.

**Kotlin · Jetpack Compose · Material 3 · MVVM + Repository · Room · Hilt · DataStore · Coroutines/StateFlow**

No account, no cloud, no ads, no tracking. Internet is only used for opt-in features (bill sync,
scanner) planned in later milestones.

## Current status — M0–M8

Implemented:

- **Meters tab**: dashboard summary cards (totals, avg/day, projection, safe/warning/critical
  counts), search + sort, and per-meter cards with tap-to-reveal masked reference, an animated
  gradient progress bar (deep-green → dark-red), live consumed/remaining/percent, status badge,
  and expandable actions.
- **Add / edit meter** with full validation (current ≥ previous, target > 0, no negatives,
  optional decimals) and friendly inline errors.
- **Meter detail** with full reference, forecast (avg/day, projection, over/under), monthly reset,
  delete, and reading history.
- **Monthly reset** that snapshots the closed month to history and rolls the cycle forward.
- **Planning tab**: billing-cycle window with expected-vs-actual pace, a colour-graded day-by-day
  **calendar** (tap any day for expected/actual/difference), animated **Target-vs-Actual** line
  chart and **Daily usage** bar chart, and a forecast card (avg/day, projected end, overage).
- **Reading history**: every completed month, reachable from Settings, with per-month figures and a
  clear-all action. History persists until explicitly deleted.
- **Settings**: theme (light/dark/system), monthly reading date, default target, decimals toggle,
  reading history, **backup export/import**, about.
- **Backup (M7)**: export a versioned JSON snapshot (meters + history + settings) to any location
  via the Storage Access Framework, and restore it in a single transaction. Round-trip unit-tested.
- **Optional online modules (M8)**: `BillSyncEngine` (provider adapters behind a `BillProvider`
  interface) and `BillScanner` (OCR) are wired as isolated seams. No adapters are registered yet, so
  the app is fully offline and sync gracefully reports "unsupported" — adding a real provider later
  is a single `@Binds @IntoSet` with no other changes.
- **Persistence**: Room database with a seeded starter meter on first launch; settings in DataStore.

- **Polish (M9, ongoing)**: a "safe daily budget" chip on each meter card (units/day left to stay
  under the limit), animated list reordering when the sort changes, and a floating FAB positioned
  clear of the glass nav bar.

Planned next (see `PLAN.md`): concrete bill-sync provider adapters and an ML-Kit OCR backend when
desired; further visual polish.

Planning engines (`PlanningEngine`, `ForecastEngine`) are pure and unit-tested. The day-by-day
"actual" line is estimated from your running average pace, since the app doesn't require daily
meter logging.

## Architecture

```
Compose UI → ViewModel (StateFlow) → Repository (interface) → Room / DataStore
                                    ↘ pure engines: CalculationEngine, MeterValidator
```

- **domain/** — framework-free models, engines and repository interfaces (fully unit-tested).
- **data/** — Room entities/DAOs, DataStore, mappers, repository implementations.
- **ui/** — theme (neumorphism + glassmorphism), navigation, reusable components, screens.
- **di/** — Hilt modules.

Electricity providers live in a code registry (`ElectricityProvider`), so adding a company is a
one-line change with no database migration.

## Build & run

Requires Android Studio (Ladybug or newer) with Android SDK 35.

```bash
cd android
./gradlew assembleDebug      # build
./gradlew test               # run JVM unit tests (engine, validator, billing cycle)
```

Or open the `android/` folder in Android Studio and press Run.

> Note: `minSdk = 26` (Android 8.0) so the adaptive launcher icon works without legacy fallbacks.
