# Annapurna — The Ultimate Culinary Heritage & Recipe Vault

[![Android CI](https://github.com/ShashankS1011/Annapurna/actions/workflows/android.yml/badge.svg)](https://github.com/ShashankS1011/Annapurna/actions/workflows/android.yml)
[![Download APK](https://img.shields.io/badge/Download_APK-v1.1-2EA44F?style=for-the-badge&logo=android&logoColor=white)](https://github.com/ShashankS1011/Annapurna/releases/download/v1.1/Annapurna.apk)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform: Android 7.0+](https://img.shields.io/badge/Platform-Android%207.0%2B-blue?logo=android)](https://www.android.com)

**Annapurna** — named after the Hindu Goddess of Food and Nourishment (*Anna* meaning food, and *Purna* meaning complete or perfect) — is an enterprise-grade, native Android application engineered to function as a permanent digital repository and active cooking assistant. The application honors the warmth, heritage, and emotional connection of home-cooked meals by providing a secure, lightning-fast, privacy-first space to safeguard treasured family recipes.

---

## 🌟 Key Commercial & Product Features

* **🏛️ Permanent Generational Vault:** Safeguard cherished family recipes, secret ingredient tweaks, and chef attributions (*Mom*, *Grandma*, *Chef Ranveer*) in a robust offline-first local Room SQLite database.
* **▶️ Smart Video Integration & Timestamp Resume:** Embed local videos, YouTube tutorials, or Instagram reels directly into recipes. The app automatically remembers your exact playback position so you can resume video steps right where you left off.
* **🍳 Active Kitchen Assistance Mode:** Automatically prevents screen sleep timeouts while viewing cooking instructions, keeping your display awake when your hands are busy in the kitchen.
* **🎨 5 Authentic Spice Theme Palettes:** Customize your culinary workspace with dynamic Material Design 3 themes inspired by Indian spices: **Kesar** (Saffron), **Tulsi** (Botanical Green), **Mitti** (Terracotta Brown), **Masala** (Crimson), and **Haldi** (Turmeric Yellow).
* **🔍 Real-Time Instant Search:** Instantly filter hundreds of recipes by title, chef, category, or occasion as you type.
* **📤 Structured Sharing & Backup:** Export your recipes into clean Markdown/text payloads to share instantly via WhatsApp, Telegram, or email.

---

## 🏗️ Architectural & Technical Specifications

* **Language & SDK:** 100% Java, Android SDK (Min SDK 24, Target SDK 37).
* **Architecture:** Model-View-ViewModel (MVVM) pattern enforcing strict separation of concerns, reactive `LiveData` streams, and robust lifecycle management.
* **Database Engine:** Room Persistence Library with compile-time SQL query validation and asynchronous background worker threads.
* **Media Engine:** Jetpack Media3 ExoPlayer integration with hardware-accelerated video rendering.
* **UI Framework:** Material Components (MD3) with dynamic day/night dark mode support and custom attribute-driven theme styling.

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
* Android Studio (Jellyfish / Koala or newer)
* JDK 17
* Android SDK (API 37)

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/ShashankS1011/Annapurna.git

# Navigate to project root
cd Annapurna

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

---

## 📋 GitHub Repository Description & Topics

To set up your GitHub repository metadata for maximum discoverability and professional presentation, use the following:

* **Description:** 
  > 🍲 Annapurna is an enterprise-grade native Android recipe vault & active kitchen assistant app built with Java, Room, MVVM, and Jetpack Media3, featuring multi-spice color themes and video timestamp resume.
* **Topics:** 
  `android`, `recipe-app`, `java`, `room-database`, `exoplayer`, `material-design-3`, `mvvm`, `cooking-assistant`, `offline-first`

---

## 📄 License

This project is open-source software licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
