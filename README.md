# FreshTrack 🥬⏱️

![FreshTrack Logo](assets/freshtrack_logo.png)

FreshTrack is an award-winning, premium, privacy-first offline grocery and expiry tracker designed for Android. It helps users organize household pantries, monitor food freshness to reduce waste, and manage grocery budgets with local, high-fidelity statistics and zero data leak.

---

## 🌟 App Description

Take back control of your kitchen, budget, and privacy with FreshTrack.

FreshTrack is a kitchen companion engineered to solve food waste and financial leaks without compromising your personal details. Designed with a clean, pistachio-mint aesthetic and Material 3 design spacing, FreshTrack gives you a comprehensive, real-time breakdown of your household pantry, upcoming item expirations, and monthly household grocery spending.

## ✨ Features

* **Food Lifespan Tracking:** Keep log tables of your household items, default shelf-lives, and exact expiration metrics. No more guessing if the milk is safe to consume.
* **Shopping List Module:** Create multiple shopping lists, add items with quantities, and check them off. Automatically migrate completed items directly to your pantry inventory with one tap.
* **Smart Expiry Reminder:** Receive precise, local push notification warnings days before items spoil. Alerts are handled fully offline on your device using Android’s Alarm Manager—never scheduled on a server.
* **Local Interactive Spending Analytics:** Check monthly and weekly budget meters. Spot exact categories (Dairy, Grains, Produce, Meats, etc.) that contribute to waste.
* **Backup & Restore:** Fully integrates with native Android Backup Services (via encrypted, user-managed Google One backups) and supports manual, offline JSON import/export workflows to ensure your data is always safe.
* **Multi-Language Support:** 10 Indian Regional Languages + Dual English/Hindi Modes. Designed for global accessibility, supporting major localized regional inputs seamlessly.
* **Offline-First Architecture:** 100% Local Sovereignty. All inventory records, budget transactions, item notes, and captured photos are stored directly in your device’s local secure storage using SQLite via Room. No forced logins and zero beacons or tracking.

## 📸 Screenshots

*(Add screenshots here)*

## 🔏 Privacy Information

Unlike typical modern utility apps that force users to register, create cloud accounts, and undergo constant behavioral tracking, **FreshTrack operates under a strict, 100% offline-first privacy model**. 

We believe household data is sacred. Who you buy from, what you consume, and what you spend on food shouldn't be analyzed on remote advertising servers. There are absolutely no analytics beacons, diagnostic trackers, or advertisement SDKs embedded in the code.

FreshTrack features an in-app **Device Security Status panel** that verifies standard operating systems, inspects official developer signatures to protect against altered packages, and monitors runtime integrity to ensure your household data runs in a fully insulated sandbox.

## 📥 APK Download

Download the latest version of FreshTrack directly from the [Releases](https://github.com/your-username/freshtrack/releases) page.
- [Latest Release APK](https://github.com/your-username/freshtrack/releases/latest)
- [Latest Debug APK](https://github.com/your-username/freshtrack/releases/latest)

## 🛠️ Installation Instructions

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

## 📄 Licensing & Creator Credits

* **Developer:** Developed with care by **Vivek Jha**.
* **License:** Released under personal, non-commercial utility terms. All graphical leaf-clock logo styles and typography are locked brand assets.
