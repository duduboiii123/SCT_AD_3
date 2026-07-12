# Stopwatch App

## 📌 Project Title
**SCT_AD_3 - Stopwatch App**

## 📖 Description
A beginner-friendly Android application built using **Kotlin** and **XML** that implements
a fully functional digital stopwatch. The app accurately tracks elapsed time using
`SystemClock.elapsedRealtime()` combined with a `Handler`/`Runnable` loop, and displays
the time in **HH:MM:SS:MS** format.

This project was developed as part of an Android Development internship task.

## ✨ Features
- ✅ Digital stopwatch with Start, Pause, Resume, and Reset controls
- ✅ Time displayed as `HH : MM : SS : MS`
- ✅ Accurate timing using `Handler` + `SystemClock.elapsedRealtime()`
- ✅ Prevents multiple timers from running simultaneously
- ✅ Proper lifecycle handling (`onStart`, `onStop`, `onDestroy`)
- ✅ Elapsed time and running state survive screen rotation (`onSaveInstanceState`)
- ✅ Start button disabled while running / after started
- ✅ Resume button enabled only after Pause
- ✅ Short vibration feedback on Reset
- ✅ Clean, modern Material Design UI with rounded buttons
- ✅ Responsive, centered, portrait-optimized layout

## 🛠️ Technologies Used
- **Language:** Kotlin
- **UI:** XML with Material Design 3 Components
- **Architecture:** Single Activity (no MVVM)
- **View Binding:** Enabled
- **Timing:** `Handler`, `Runnable`, `SystemClock.elapsedRealtime()`
- **Min SDK:** 24
- **Target SDK:** 34
- **Compile SDK:** 34


## 🚀 How to Run
1. Clone or download this repository.
2. Open **Android Studio**.
3. Select **Open an Existing Project** and choose the `SCT_AD_3` folder.
4. Let Gradle sync complete.
5. Connect an emulator or physical device (Android 7.0 / API 24 or higher).
6. Click **Run ▶️** to build and launch the app.

## 📱 How to Use
1. Tap **Start** to begin the stopwatch.
2. Tap **Pause** to temporarily stop timing.
3. Tap **Resume** to continue from where you left off.
4. Tap **Reset** to clear the timer back to `00:00:00:000` (device will vibrate briefly).

## 📂 Project Structure
SCT_AD_3
│
├── app
│   ├── manifests
│   │      AndroidManifest.xml
│   │
│   ├── java
│   │      MainActivity.kt
│   │
│   ├── res
│   │      layout/activity_main.xml
│   │      values/colors.xml
│   │      values/strings.xml
│   │      values/themes.xml
│   │
│   └── build.gradle
│
├── README.md
└── .gitignore

## 🔮 Future Improvements
- Add Lap/Split time recording with a RecyclerView list
- Add sound feedback on Start/Pause
- Add dark mode toggle
- Persist stopwatch state using SharedPreferences or a foreground Service
  so timing continues accurately even if the app is killed
- Add landscape-optimized layout variant

## 👤 Author
**Maharudra**
Internship Task — SCT_AD_3
