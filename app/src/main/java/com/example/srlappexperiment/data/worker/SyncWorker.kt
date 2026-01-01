package com.example.srlappexperiment.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.srlappexperiment.data.sync.SyncEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.google.firebase.perf.FirebasePerformance

/**
 * WorkManager worker for background data synchronization
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncEngine: SyncEngine,
    private val firebasePerformance: FirebasePerformance
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val trace = firebasePerformance.newTrace("sync_worker_duration")
        trace.start()
        return try {
            val result = syncEngine.syncAll()
            trace.stop()
            if (result.isSuccess) {
                Result.success()
            } else {
                // Return retry to use exponential backoff defined in work request
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
