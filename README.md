# FreshTrack 🥬⏱️

FreshTrack (visually styled as **FreshTrak**) is an award-winning, premium, privacy-first offline grocery and expiry tracker designed for Android. It helps users organize household pantries, monitor food freshness to reduce waste, and manage grocery budgets with local, high-fidelity statistics and zero data leak.

---

## 🌟 Core Brand Philosophy

Unlike typical modern utility apps that force users to register, create cloud accounts, and undergo constant behavioral tracking, **FreshTrack operates under a strict, 100% offline-first privacy model**. 

We believe household data is sacred. Who you buy from, what you consume, and what you spend on food shouldn't be analyzed on remote advertising servers.

*   **100% Local Sovereignty:** All inventory records, budget transactions, item notes, and captured photos are stored directly in your device’s local secure storage using SQLite via Room.
*   **No forced logins:** Zero accounts to sign up for. The app is fully accessible immediately upon install.
*   **Zero Beacons or Tracking:** There are absolutely no analytics beacons, diagnostic trackers, or advertisement SDKs embedded in the code.
*   **Secure System Backups:** Fully integrates with native Android Backup Services (via encrypted, user-managed Google One backups) and supports manual, offline JSON import/export workflows.

---

## 📱 Google Play Store Listing Assets

### 🏷️ Short Description (80 Characters Max)
Privacy-first offline list to track food expiry, manage kitchen budgets, and stop waste.

### 📝 Long Description
**Take back control of your kitchen, budget, and privacy with FreshTrack.**

FreshTrack is a kitchen companion engineered to solve food waste and financial leaks without compromising your personal details. Designed with a clean, pistachio-mint aesthetic and Material 3 design spacing, FreshTrak gives you a comprehensive, real-time breakdown of your household pantry, upcoming item expirations, and monthly household grocery spending.

#### Why Choose FreshTrack?
* **Food Lifespan Tracking:** Keep log tables of your household items, default shelf-lives, and exact expiration metrics. No more guessing if the milk is safe to consume.
* **Smart Alert Alarms:** Receive precise, local push notification warnings days before items spoil. Alerts are handled fully offline on your device using Android’s Alarm Manager—never scheduled on a server.
* **Local Interactive Spending Analytics:** Check monthly and weekly budget meters. Spot exact categories (Dairy, Grains, Produce, Meats, etc.) that contribute to waste.
* **Built-in Receipt & Product Camera:** Quickly snap pictures of your purchases or receipts to log records. Photos are securely cached in your app's isolated local folder.
* **10 Indian Regional Languages + Dual English/Hindi Modes:** Designed for global accessibility, supporting major localized regional inputs seamlessly.

#### 🔏 Premium Security & Device Audit
FreshTrack features an in-app **Device Security Status panel** that verifies standard operating systems, inspects official developer signatures to protect against altered packages, and monitors runtime integrity to ensure your household data runs in a fully insulated sandbox.

*Note: FreshTrack is a visual and helper utility. It does not possess chemical or biological sensors. Always physically inspect original manufacturer packaging print, inspect food color, texture, and odor before consuming.*

---

## 🏗️ Technical Architecture & Under-the-hood

FreshTrack is built using modern Android Jetpack libraries and Clean Architecture (MVVM) patterns:

```
[ UI Layer: Jetpack Compose ] ──► [ ViewModel & LiveState ]
                                         │
        ┌────────────────────────────────┴────────────────────────┐
        ▼                                                         ▼
[ Core Alarm Scheduler ]                                 [ Local Repository ]
(AlarmManager & Local Notifications)                             │
                                                                 ▼
                                                        [ Room DB: SQLite ]
```

### 📦 Key Tech Stack Components
1. **User Interface:** Written 100% in declarative **Jetpack Compose** with elegant edge-to-edge screens, fluid transitions, accessibility touch-targets (min 48dp), and a cohesive adaptive dual-theme palette.
2. **Local Persistence:** Uses **Room Database** layered with SQLite to manage relational tables safely, perform fast lookups, and ensure bulletproof transactional ACID properties.
3. **Budget Engine:** Real-time state flow trackers matching the set currency symbols scales monthly and weekly budget limits dynamically.
4. **Local Alert Reminders:** Hooks directly into Android's native `AlarmManager` and `BroadcastReceiver` layers so scheduled notifications fire reliably at your designated time even if the app process is closed.
5. **No-Lag Image Capture:** Interfaces with Android's system camera to deliver immediate, optimized image snapshots, compressing and copying resources to local cache.

---

## 🛠️ Build & Installation Instructions

This project uses the modern Kotlin DSL Gradle build script ecosystem (`build.gradle.kts`).

### Prerequisites
* **Android Studio Ladybug (2024.1+)** or newer.
* **JDK 17** configured in your system and Android Studio environment.
* An Android device or emulator running **API 26 (Android 8.0 Oreo)** or higher.

### Command Line Build
To compile the debug APK manually, execute the following from the root directory:

```bash
gradle assembleDebug
```

To run the local unit and Robolectric test suite:

```bash
gradle :app:testDebugUnitTest
```

---

## 📄 Licensing & Creator Credits

* **Developer:** Developed with care by **Vivek Jha**.
* **License:** Released under personal, non-commercial utility terms. All graphical leaf-clock logo styles and typography are locked brand assets.
