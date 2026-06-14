# FocusLock — Android Studio Build Instructions

## Prerequisites
| Requirement | Version |
|---|---|
| Android Studio | Hedgehog 2023.1.1+ (or newer) |
| Android SDK | API 34 (compileSdk) |
| Min SDK | API 26 (Android 8.0) |
| JDK | 17 |
| Kotlin | 1.9.24 |

---

## Step 1 — Firebase Setup (Required)

1. Go to [Firebase Console](https://console.firebase.google.com) and create a new project called `FocusLock`.
2. Add an Android app with package name: **`com.focuslock`**
3. Download `google-services.json` and replace `app/google-services.json` with it.
4. In Firebase Console, enable:
   - **Authentication → Email/Password**
   - **Firestore Database** (start in test mode, then add security rules)

### Firestore Security Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## Step 2 — Open in Android Studio

1. Launch Android Studio → **Open** → select the `FocusLock/` folder
2. Wait for Gradle sync to complete (first sync downloads ~500MB of dependencies)
3. If prompted about "Trust Project", click **Trust**

---

## Step 3 — Android SDK Setup

In Android Studio: **File → Project Structure → SDK Location**
- Set your Android SDK path (usually `~/Library/Android/sdk` on Mac, `C:\Users\YOU\AppData\Local\Android\Sdk` on Windows)

Or edit `local.properties`:
```
sdk.dir=/YOUR/PATH/TO/android/sdk
```

---

## Step 4 — Build & Run

```bash
# From project root:
./gradlew assembleDebug          # Build debug APK
./gradlew installDebug           # Install on connected device/emulator
./gradlew assembleRelease        # Build release APK (needs signing config)
```

Or press ▶ **Run** in Android Studio.

---

## Step 5 — Required Permissions (First Launch)

The app will guide users to grant these — without them blocking won't work:

| Permission | How to Grant | Required For |
|---|---|---|
| **Accessibility Service** | Settings → Accessibility → FocusLock | App blocking |
| **Usage Access** | Settings → Digital Wellbeing / Usage Access → FocusLock | Usage stats |
| **Display Over Other Apps** | Settings → Apps → FocusLock → Display over other apps | Block screen overlay |

> **Tip**: Test on a physical device. Emulators don't fully support AccessibilityService + UsageStatsManager.

---

## Project Structure

```
FocusLock/
├── app/
│   ├── src/main/
│   │   ├── java/com/focuslock/
│   │   │   ├── FocusLockApp.kt              ← Hilt Application
│   │   │   ├── MainActivity.kt              ← Entry point
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/                 ← Room DAOs (5 files)
│   │   │   │   │   ├── database/            ← FocusLockDatabase.kt
│   │   │   │   │   └── entities/            ← Room entities (5 files)
│   │   │   │   └── repository/
│   │   │   │       ├── FocusRepository.kt   ← Local data access
│   │   │   │       └── FirebaseSyncRepository.kt ← Cloud sync
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt             ← Hilt DI providers
│   │   │   ├── service/
│   │   │   │   ├── FocusAccessibilityService.kt ← App blocking engine
│   │   │   │   ├── FocusMonitorService.kt   ← Foreground service
│   │   │   │   ├── BootReceiver.kt          ← Restart on boot
│   │   │   │   └── ScheduleAlarmReceiver.kt ← Schedule alarms
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   ├── CommonComponents.kt  ← Shared Composables
│   │   │   │   │   └── BottomNavBar.kt      ← Bottom navigation
│   │   │   │   ├── navigation/              ← NavHost + routes
│   │   │   │   ├── screens/
│   │   │   │   │   ├── dashboard/           ← Home screen
│   │   │   │   │   ├── blockapps/           ← Block Apps screen
│   │   │   │   │   ├── blockwebsites/       ← Block Websites screen
│   │   │   │   │   ├── focusmode/           ← Focus Mode + Set Duration
│   │   │   │   │   ├── schedule/            ← Schedules screen
│   │   │   │   │   ├── statistics/          ← Stats + charts
│   │   │   │   │   ├── settings/            ← Settings screen
│   │   │   │   │   ├── blockscreen/         ← Overlay shown on blocked app
│   │   │   │   │   ├── premium/             ← Premium subscription
│   │   │   │   │   └── pin/                 ← PIN protection
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt             ← Purple gradient palette
│   │   │   │       ├── Theme.kt             ← Material3 dark theme
│   │   │   │       └── Type.kt              ← Typography
│   │   │   ├── utils/
│   │   │   │   ├── AppInfo.kt               ← Package manager helpers
│   │   │   │   └── PreferenceManager.kt     ← DataStore preferences
│   │   │   └── viewmodel/
│   │   │       └── MainViewModel.kt         ← Single ViewModel (Hilt)
│   │   ├── res/
│   │   │   ├── drawable/                    ← Vector launcher icons
│   │   │   ├── mipmap-anydpi-v26/           ← Adaptive icons
│   │   │   ├── values/                      ← strings.xml, themes.xml
│   │   │   └── xml/
│   │   │       ├── accessibility_service_config.xml
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                     ← App-level Gradle
│   ├── google-services.json                 ← REPLACE with yours
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml                   ← Version catalog
├── build.gradle.kts                         ← Root Gradle
├── settings.gradle.kts
├── gradle.properties
└── BUILD_INSTRUCTIONS.md                    ← This file
```

---

## Database Schema

### `blocked_apps`
| Column | Type | Description |
|---|---|---|
| packageName (PK) | TEXT | e.g. `com.instagram.android` |
| appName | TEXT | Display name |
| isBlocked | BOOLEAN | Active block flag |
| dailyLimitMinutes | INT | 0 = unlimited |
| usedMinutesToday | INT | Reset daily |
| createdAt | LONG | Unix ms |

### `blocked_websites`
| Column | Type | Description |
|---|---|---|
| domain (PK) | TEXT | e.g. `youtube.com` |
| isBlocked | BOOLEAN | Active block flag |
| createdAt | LONG | Unix ms |

### `focus_sessions`
| Column | Type | Description |
|---|---|---|
| id (PK) | LONG | Auto-increment |
| startTime | LONG | Unix ms |
| endTime | LONG? | Nullable |
| durationMinutes | INT | Planned duration |
| blockedAppsCount | INT | Snapshot at start |
| blockedWebsitesCount | INT | Snapshot at start |
| isCompleted | BOOLEAN | Session finished |
| date | TEXT | "yyyy-MM-dd" |

### `schedules`
| Column | Type | Description |
|---|---|---|
| id (PK) | LONG | Auto-increment |
| name | TEXT | "Study Time" |
| daysOfWeek | TEXT | "1,2,3,4,5" (1=Mon) |
| startTime | TEXT | "HH:mm" |
| endTime | TEXT | "HH:mm" |
| isEnabled | BOOLEAN | On/off toggle |
| createdAt | LONG | Unix ms |

### `app_usage_stats`
| Column | Type | Description |
|---|---|---|
| id (PK) | LONG | Auto-increment |
| packageName | TEXT | App package |
| appName | TEXT | Display name |
| usageMinutes | INT | Daily usage |
| date | TEXT | "yyyy-MM-dd" |
| category | TEXT | Productive/Social/etc |

---

## Architecture

```
UI Layer (Compose) ←→ ViewModel (Hilt) ←→ Repository
                                              ↙        ↘
                                    Room DB        Firebase
                                  (local)         (cloud sync)
                                              ↕
                                    DataStore (prefs)
                                              ↕
                             AccessibilityService + ForegroundService
```

---

## Customization

### Change theme colors
Edit `ui/theme/Color.kt` — the key values are `Purple400`/`Purple500` for the brand color and `BackgroundDark` for the background.

### Add more blocked apps by default
In `FocusRepository`, seed the database on first launch using a `RoomDatabase.Callback`.

### Enable In-App Purchases (for real premium)
Integrate [Google Play Billing Library](https://developer.android.com/google/play/billing) and replace the `isPremium` DataStore flag with a verified purchase check.

---

## Common Issues

| Issue | Fix |
|---|---|
| Gradle sync fails | Check internet, invalidate caches (File → Invalidate Caches) |
| `google-services.json` error | Replace placeholder with real file from Firebase Console |
| AccessibilityService not triggering | Enable it manually in phone Settings → Accessibility |
| App blocking not working on emulator | Use physical device — emulators have limited accessibility support |
| Room migration error | Increase database version and add migration, or uninstall app |
