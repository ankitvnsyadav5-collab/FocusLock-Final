# 🔒 FocusLock — Android App

> Stay focused. Achieve more.

A productivity app that blocks distracting apps & websites with a beautiful dark purple UI.

---

## ⚡ Get the APK (Easiest Way)

1. Go to the **Actions** tab above
2. Click the latest **"Build FocusLock APK"** workflow run
3. Scroll down to **Artifacts**
4. Download **FocusLock-Debug-APK**
5. Install on your Android phone

> **Allow Unknown Sources**: Settings → Security → Install Unknown Apps → allow your browser/Files app

---

## 🚀 Build It Yourself via GitHub (No Android Studio needed)

### Step 1 — Fork this repo
Click **Fork** (top right of this page)

### Step 2 — Add Firebase secret (optional but recommended)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create project → Add Android app → package: `com.focuslock`
3. Download `google-services.json`
4. In your forked repo: **Settings → Secrets and variables → Actions → New secret**
   - Name: `GOOGLE_SERVICES_JSON`
   - Value: paste the entire contents of `google-services.json`

> **Without this step**: The app still builds and runs — Firebase features (cloud sync, auth) just won't work.

### Step 3 — Trigger the build

**Option A — Auto**: Push any change (even edit README.md)

**Option B — Manual**:
1. Go to **Actions** tab
2. Click **"Build FocusLock APK"**
3. Click **"Run workflow"** → **"Run workflow"**
4. Wait ~5 minutes ☕
5. Download APK from **Artifacts**

---

## 📱 Features

| Feature | Status |
|---|---|
| App Blocking (Accessibility Service) | ✅ |
| Website Blocking | ✅ |
| Focus Mode with Timer | ✅ |
| Schedule Blocking | ✅ |
| Usage Statistics with Charts | ✅ |
| Premium Screen | ✅ |
| PIN Protection | ✅ |
| Strict Mode | ✅ |
| Room Database | ✅ |
| Firebase Auth + Cloud Sync | ✅ |
| Dark Purple Theme | ✅ |

---

## 🏗️ Tech Stack

- **Kotlin** + **Jetpack Compose**
- **Material 3** dark theme
- **Hilt** (dependency injection)
- **Room** (local database)
- **Firebase** Auth + Firestore
- **DataStore** (preferences)
- **Accessibility Service** (app blocking)
- **UsageStatsManager** (screen time)

---

## 🔧 First Launch — Required Permissions

The app will prompt you. Without these, blocking won't work:

1. **Accessibility Service** → Settings → Accessibility → FocusLock → Enable
2. **Usage Access** → Settings → Digital Wellbeing → FocusLock → Allow
3. **Display Over Other Apps** → Settings → Apps → FocusLock → Allow

---

## 📂 Project Structure

```
app/src/main/java/com/focuslock/
├── MainActivity.kt
├── FocusLockApp.kt
├── data/
│   ├── local/          # Room DB, DAOs, Entities
│   └── repository/     # FocusRepository, FirebaseSyncRepository
├── di/                 # Hilt AppModule
├── service/            # AccessibilityService, ForegroundService
├── ui/
│   ├── screens/        # 11 screens
│   ├── theme/          # Colors, Typography, Theme
│   ├── components/     # Shared Composables
│   └── navigation/     # NavHost, Routes
├── utils/              # PreferenceManager, AppInfo
└── viewmodel/          # MainViewModel
```

---

## 🔑 Release Build (Signed APK for Play Store)

1. Generate a keystore: `keytool -genkey -v -keystore focuslock.jks -keyalg RSA -keysize 2048 -validity 10000 -alias focuslock`
2. Add these GitHub Secrets:
   - `KEYSTORE_BASE64` → `base64 focuslock.jks | pbcopy`
   - `KEY_ALIAS` → your alias
   - `KEY_PASSWORD` → your key password
   - `STORE_PASSWORD` → your store password
3. Go to Actions → **"Build Release APK"** → Run workflow
