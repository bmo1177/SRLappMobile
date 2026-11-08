package com.example.srlappexperiment

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp
import com.example.srlappexperiment.data.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class with Hilt dependency injection and WorkManager configuration
 * Initializes Timber logging (debug only) and Firebase Crashlytics
 */
@HiltAndroidApp
class SRLAppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging (debug builds only)
        try {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            } else {
                // In release builds, plant a tree that sends errors to Crashlytics
                 try {
                    if (FirebaseApp.getApps(this).isNotEmpty()) {
                        Timber.plant(CrashlyticsTree())
                    }
                } catch (e: Exception) {
                    // Fallback if Crashlytics fails
                }
            }
        } catch (e: Exception) {
            // Safety: failed to plant timber tree
        }
        
        // Configure Crashlytics
        try {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                FirebaseCrashlytics.getInstance().apply {
                    setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
                }
                // Speed up slow emulators: disable Analytics + Perf collection in debug.
                if (BuildConfig.DEBUG) {
                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
                    FirebasePerformance.getInstance().isPerformanceCollectionEnabled = false
                }
            }
        } catch (e: Exception) {
             Timber.e(e, "Failed to initialize Firebase Crashlytics")
        }

        // Schedule background tasks off the main thread so cold start is not blocked.
        // WorkManager.getInstance() opens its own DB — keep it off onCreate's main thread.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                com.example.srlappexperiment.data.worker.NotificationScheduler.scheduleDailyNotification(this@SRLAppApplication)
                SyncScheduler.schedulePeriodicSync(this@SRLAppApplication)
                Timber.d("Background tasks scheduled successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing background tasks")
            }
        }
    }
    
    /**
     * Custom Timber tree that logs errors and warnings to Firebase Crashlytics
     */
    private class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == android.util.Log.ERROR || priority == android.util.Log.WARN) {
                try {
                    FirebaseCrashlytics.getInstance().log("$tag: $message")
                    t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                } catch (e: Exception) {
                    // Ignored
                }
            }
        }
    }
}
