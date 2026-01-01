package com.example.srlappexperiment

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.example.srlappexperiment.data.sync.SyncScheduler
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
            }
        } catch (e: Exception) {
             Timber.e(e, "Failed to initialize Firebase Crashlytics")
        }
        
        try {
            // Schedule background tasks - wrapped in try-catch to prevent crashes on startup
            com.example.srlappexperiment.data.worker.NotificationScheduler.scheduleDailyNotification(this)
            SyncScheduler.schedulePeriodicSync(this)
            Timber.d("Background tasks scheduled successfully")
        } catch (e: Exception) {
            // Log error but don't crash the app
            Timber.e(e, "Error initializing background tasks")
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
