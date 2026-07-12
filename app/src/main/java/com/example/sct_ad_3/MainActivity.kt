package com.example.sct_ad_3

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import com.example.sct_ad_3.databinding.ActivityMainBinding
import java.util.Locale

/**
 * MainActivity
 *
 * A simple, accurate Digital Stopwatch app.
 *
 * Uses:
 *  - ViewBinding for view access
 *  - Handler + Runnable loop to update the UI every 10ms (~centisecond precision)
 *  - SystemClock.elapsedRealtime() as the accurate time source (unaffected by
 *    system clock changes, unlike System.currentTimeMillis())
 *
 * Time is displayed in the format: HH:MM:SS:MS
 *
 * The stopwatch state (running/paused) and elapsed time survive screen
 * rotation via onSaveInstanceState().
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityMainBinding

    // Handler used to repeatedly post the update Runnable on the main thread
    private val handler = Handler(Looper.getMainLooper())

    // The elapsedRealtime() value at which the stopwatch was (re)started.
    // "Base" time used to calculate elapsed duration.
    private var startTimeMillis: Long = 0L

    // Total time accumulated from previous start/pause cycles.
    // This lets us pause and resume without losing progress.
    private var accumulatedTimeMillis: Long = 0L

    // Whether the stopwatch is currently running (ticking)
    private var isRunning: Boolean = false

    // Whether the stopwatch has ever been started (used to control button states)
    private var hasStarted: Boolean = false

    // Flag to prevent multiple Runnable loops being posted at the same time
    private var isHandlerActive: Boolean = false

    /**
     * Runnable that updates the timer TextView every 10 milliseconds
     * while the stopwatch is running.
     */
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentElapsed = accumulatedTimeMillis +
                        (SystemClock.elapsedRealtime() - startTimeMillis)
                binding.tvTimer.text = formatTime(currentElapsed)

                // Post again after 10ms for smooth, accurate updates
                handler.postDelayed(this, 10L)
            } else {
                isHandlerActive = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore previous state if the activity was recreated (e.g., rotation)
        savedInstanceState?.let { state ->
            accumulatedTimeMillis = state.getLong(KEY_ACCUMULATED_TIME, 0L)
            isRunning = state.getBoolean(KEY_IS_RUNNING, false)
            hasStarted = state.getBoolean(KEY_HAS_STARTED, false)

            if (isRunning) {
                // Resume the running clock from "now"
                startTimeMillis = SystemClock.elapsedRealtime()
                startTicking()
            }
        }

        updateTimerDisplay()
        updateButtonStates()

        setupButtonListeners()
    }

    /**
     * Wires up click listeners for Start, Pause, Resume, and Reset buttons.
     */
    private fun setupButtonListeners() {
        binding.btnStart.setOnClickListener {
            startStopwatch()
        }

        binding.btnPause.setOnClickListener {
            pauseStopwatch()
        }

        binding.btnResume.setOnClickListener {
            resumeStopwatch()
        }

        binding.btnReset.setOnClickListener {
            resetStopwatch()
        }
    }

    /**
     * Starts the stopwatch from zero.
     * Only usable when the stopwatch is not already running.
     */
    private fun startStopwatch() {
        if (isRunning) return // Prevent multiple timers from running

        accumulatedTimeMillis = 0L
        startTimeMillis = SystemClock.elapsedRealtime()
        isRunning = true
        hasStarted = true

        startTicking()
        updateButtonStates()
    }

    /**
     * Pauses the stopwatch, preserving the elapsed time so far.
     */
    private fun pauseStopwatch() {
        if (!isRunning) return

        // Save how much time has elapsed so far into the accumulated total
        accumulatedTimeMillis += SystemClock.elapsedRealtime() - startTimeMillis
        isRunning = false

        // Stop the tick loop
        handler.removeCallbacks(tickRunnable)
        isHandlerActive = false

        updateButtonStates()
    }

    /**
     * Resumes the stopwatch after it has been paused.
     */
    private fun resumeStopwatch() {
        if (isRunning || !hasStarted) return

        // Reset the base time to "now" so elapsed calculation stays accurate
        startTimeMillis = SystemClock.elapsedRealtime()
        isRunning = true

        startTicking()
        updateButtonStates()
    }

    /**
     * Resets the stopwatch back to 00:00:00:000 and stops any running timer.
     * Also triggers a short vibration as user feedback.
     */
    private fun resetStopwatch() {
        // Stop any running loop first
        handler.removeCallbacks(tickRunnable)
        isHandlerActive = false

        isRunning = false
        hasStarted = false
        accumulatedTimeMillis = 0L
        startTimeMillis = 0L

        updateTimerDisplay()
        updateButtonStates()
        vibrateOnReset()
    }

    /**
     * Begins the repeating Runnable loop, guarding against duplicate
     * handlers being posted at the same time.
     */
    private fun startTicking() {
        if (isHandlerActive) return // Prevent duplicate handler loops
        isHandlerActive = true
        handler.post(tickRunnable)
    }

    /**
     * Updates the timer TextView to reflect the current accumulated time
     * (used when not actively running, e.g. after reset or pause).
     */
    private fun updateTimerDisplay() {
        binding.tvTimer.text = formatTime(accumulatedTimeMillis)
    }

    /**
     * Enables/disables buttons based on the current stopwatch state:
     *  - Start: enabled only when stopwatch has never started (or after reset)
     *  - Pause: enabled only while running
     *  - Resume: enabled only when paused (started but not running)
     *  - Reset: enabled whenever there is elapsed time or a started state
     */
    private fun updateButtonStates() {
        binding.btnStart.isEnabled = !hasStarted
        binding.btnPause.isEnabled = isRunning
        binding.btnResume.isEnabled = hasStarted && !isRunning
        binding.btnReset.isEnabled = hasStarted || accumulatedTimeMillis > 0L
    }

    /**
     * Formats a millisecond duration into HH:MM:SS:MS (MS shown as 3-digit millis).
     */
    private fun formatTime(totalMillis: Long): String {
        val hours = (totalMillis / 3_600_000) % 100
        val minutes = (totalMillis / 60_000) % 60
        val seconds = (totalMillis / 1_000) % 60
        val millis = totalMillis % 1_000

        return String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d:%03d",
            hours, minutes, seconds, millis
        )
    }

    /**
     * Triggers a short vibration when the Reset button is pressed,
     * using the appropriate API depending on Android version.
     */
    private fun vibrateOnReset() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }

    /**
     * Saves the current stopwatch state so it survives configuration
     * changes such as screen rotation.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // If currently running, fold the elapsed time into accumulated total
        // so we save a stable snapshot value.
        val snapshotAccumulated = if (isRunning) {
            accumulatedTimeMillis + (SystemClock.elapsedRealtime() - startTimeMillis)
        } else {
            accumulatedTimeMillis
        }

        outState.putLong(KEY_ACCUMULATED_TIME, snapshotAccumulated)
        outState.putBoolean(KEY_IS_RUNNING, isRunning)
        outState.putBoolean(KEY_HAS_STARTED, hasStarted)
    }

    /**
     * Stops the tick loop when the activity is no longer visible to avoid
     * leaking work, but keeps the logical state (isRunning) intact so the
     * stopwatch can resume correctly (e.g. after rotation).
     */
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(tickRunnable)
        isHandlerActive = false
    }

    override fun onStart() {
        super.onStart()
        // If the stopwatch was running before onStop(), resume ticking.
        if (isRunning) {
            startTimeMillis = SystemClock.elapsedRealtime() - (accumulatedTimeMillis - accumulatedTimeMillis)
            // Recompute base so display doesn't jump; simplest correct approach:
            // treat "now" as continuation point using stored accumulated time.
            startTicking()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up to avoid memory leaks
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val KEY_ACCUMULATED_TIME = "key_accumulated_time"
        private const val KEY_IS_RUNNING = "key_is_running"
        private const val KEY_HAS_STARTED = "key_has_started"
    }
}
