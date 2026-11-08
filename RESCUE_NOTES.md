# SRLappMobile — Rescue Notes (local only, not for push)

**Commit rescued:** `c63a850 Building the apk file to test the application` (2026-05-26)
**Date:** 2026-11-08
**Host:** NixOS, Java 17.0.20, Android SDK at `~/Android/Sdk` (platform-tools 37.0.1, platforms;android-36, build-tools;36.0.0)
**Outcome:** `gradle assembleDebug` → **BUILD SUCCESSFUL in 3m 18s, 45 tasks** → `app/build/outputs/apk/debug/app-debug.apk` (30M, `com.example.srlappexperiment`, versionCode 2, versionName 1.1.0, compileSdk 36, minSdk 24, targetSdk 36)

## What was actually broken?

Almost nothing in Kotlin. The tracked `build_log.txt` shows 55 errors (`ModernCard`, `isPremium`, `targetLanguage`, `onNavigateToSupport`, …) from the author's earlier machine (`/home/dev-lab/...`). At HEAD those symbols exist and the build is green:

- `ModernCard` defined in `presentation/ui/components/CustomComponents.kt:63` and imported everywhere
- `prefs.isPremium` / `prefs.targetLanguage` used correctly in `SettingsScreen.kt`
- `MainScreen.kt` passes `onNavigateToSubscription/Admin/Support` correctly

So this was a **stale-log rescue**, not a code rescue. No `.kt` files were changed.

## What WAS fixed (local diff)

1. `gradle/wrapper/gradle-wrapper.properties`: `networkTimeout` 10000 → 300000 ms (10s was too short for the 130MB Gradle download).
2. `local.properties.example` (new template) + `local.properties` (gitignored, `sdk.dir=/home/devlab/Android/Sdk`, empty `GEMINI_API_KEY`).
3. `README.md` Quick Start corrected to SDK 36, real clone URL, `cp local.properties.example`, and the nix fallback.

`app/google-services.json` is tracked and matches `namespace = "com.example.srlappexperiment"` — no action was taken on it.

## Build command that proved it

```bash
export ANDROID_HOME=$HOME/Android/Sdk
nix shell nixpkgs#gradle --command bash -c \
  'export ANDROID_HOME=/home/devlab/Android/Sdk JAVA_HOME=/nix/store/kykaas4mrvyaw2365y2fg8sn22qh5qb0-openjdk-17.0.20+8/lib/openjdk; gradle assembleDebug --stacktrace'
# log: /tmp/opencode/rescue_baseline.log (local)
```

`./gradlew` itself could not download Gradle 8.13 on this host (github releases returned 503 then 502). The nix `gradle` 8.14.6 binary was used as a drop-in for the same `assembleDebug` task. On a normal network `./gradlew assembleDebug` should work directly (wrapper still pins 8.13).

Full baseline log was saved locally at `/tmp/opencode/rescue_baseline.log` (not committed).

## Improvement pass (2026-11-08, skills: ui-ux-pro-max + coding-standards + security-review)

- **README** fully rewritten to match `libs.versions.toml` (Kotlin 2.0.21, AGP 8.13.2,
  Compose BOM 2024.09.00, Hilt 2.51.1, Room 2.6.1, Firebase BOM 33.7.0, SDK 36),
  new Security / Troubleshooting / Build verification sections, fixed repo links.
- **Deprecations fixed** (RTL/a11y per ui-ux-pro-max jetpack-compose): 8 icons
  `Icons.Default.X` → `Icons.AutoMirrored.Filled.X` (ArrowBack, Send, VolumeUp,
  MenuBook, LibraryBooks, FormatListBulleted, HelpCenter, Chat), `Divider` →
  `HorizontalDivider`, `TabRowDefaults.Indicator` → `SecondaryIndicator`,
  `CircularProgressIndicator(progress = …)` → lambda overload. Kotlin
  deprecated warnings after: 0.
- **Room indices** added to all 10 `TeacherEntities` foreign keys; `DATABASE_VERSION`
  3 → 4 (destructive migration — debug-safe). kapt index warnings after: 0.
- **Tests added**: `SpacedRepetitionBoundsTest` (3) + `ConstantsContractTest` (3).
  Suite: 8 suites, 37 tests, 0 failures (`testDebugUnitTest` passes).
- **Security**: login test-account card now `if (BuildConfig.DEBUG)`; no `AIza`
  keys in Kotlin (key comes from `BuildConfig.GEMINI_API_KEY`).

Final check: `gradle assembleDebug testDebugUnitTest` → BUILD SUCCESSFUL,
APK rebuilt (2026-11-08 13:36).

## Startup / emulator fixes (2026-11-08, P1+P2)

- **Splash race**: `SplashScreen` had a fixed `delay(2500)` and read
  `isUserLoggedIn`/`isOnboardingComplete` StateFlows whose `initialValue=false`
  (`WhileSubscribed(5000)`). On a slow emulator Firebase/DataStore had not
  emitted yet, so even logged-in users routed to walkthrough — perceived as
  "won't start". Now `MainViewModel.awaitLoginState()`/`awaitOnboarded()`
  (`first()` on the real flows) with `withTimeoutOrNull(3000)` + 1.2s brand time.
- **Main-thread init**: WorkManager `enqueueUniquePeriodicWork` (opens its own DB)
  moved from `Application.onCreate` to `Dispatchers.IO` fire-and-forget.
  Firebase Analytics + Perf collection disabled when `BuildConfig.DEBUG`
  (Perf bytecode instrumentation was adding seconds on software-rendered emulators).
- **Emulator image**: must be `google_apis_playstore` (Play Services). A vanilla
  AOSP/`default` image lacks GMS and Firebase Auth/Firestore logs hang.
  Installed here: `emulator 37.2.13`, `android-36;google_apis_playstore;x86_64 r7`,
  AVD `srl-avd` (pixel_6, x86_64). Boot on this NixOS host is blocked by missing
  FHS libs (`libX11`, `libpulse`, …) — use the README runbook on a normal dev
  machine (Android Studio Hedgehog+) to run `adb shell am start -W`.

## Deliberately NOT fixed (deferred polish)

- `build_log.txt` is stale — kept as-is (deleting history out of scope).
- Backup rules remain the sample defaults (offline-first restore) — note in README.

## Verification (Final Proof)

- `~/Android/Sdk/build-tools/36.0.0/aapt dump badging …/app-debug.apk` → package/version as above, permissions INTERNET/ACCESS_NETWORK_STATE/POST_NOTIFICATIONS/WAKE_LOCK/VIBRATE + FCM/AD_ID.
- `apkanalyzer manifest application-id/version-code/version-name` → `com.example.srlappexperiment`, `2`, `1.1.0`.
- `git status` → only the 3 files above changed; `local.properties` correctly ignored; no secrets in `git diff`.

## To rebuild on a fresh clone

```bash
gh repo clone bmo1177/SRLappMobile   # or git clone …
cd SRLappMobile
cp local.properties.example local.properties
# set sdk.dir, optionally GEMINI_API_KEY
# install: platform-tools, platforms;android-36, build-tools;36.0.0 (see README)
./gradlew assembleDebug            # or nix gradle fallback above
```
