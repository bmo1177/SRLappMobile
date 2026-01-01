package com.example.srlappexperiment.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.srlappexperiment.data.local.database.AppDatabase
import com.example.srlappexperiment.data.local.database.entities.DailyProgress
import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.model.SyncResult
import com.example.srlappexperiment.domain.model.SyncStatus
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.perf.FirebasePerformance

@Singleton
class SyncEngine @Inject constructor(
    private val localDb: AppDatabase,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val firebasePerformance: FirebasePerformance,
    @ApplicationContext private val context: Context
) {
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observeSyncStatus(): Flow<SyncStatus> = syncStatus

    suspend fun syncAll(): Result<SyncResult> {
        if (!isOnline()) return Result.failure(Exception("No internet connection"))
        
        val userId = authRepository.getCurrentUser().firstOrNull()?.id 
            ?: return Result.failure(Exception("User not authenticated"))

        _syncStatus.value = SyncStatus.Syncing
        val trace = firebasePerformance.newTrace("sync_all_trace")
        trace.start()
        val startTime = System.currentTimeMillis()

        return try {
            val cardsResult = syncVocabularyCards(userId)
            val sessionsResult = syncStudySessions(userId)
            val progressResult = syncProgress(userId)

            val finalResult = SyncResult(
                cardsSynced = cardsResult,
                sessionsSynced = sessionsResult,
                progressSynced = progressResult,
                durationMillis = System.currentTimeMillis() - startTime
            )

            _syncStatus.value = SyncStatus.Success(finalResult)
            trace.stop()
            Result.success(finalResult)
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error(e.message ?: "Unknown sync error")
            trace.stop()
            Result.failure(e)
        }
    }

    suspend fun syncVocabularyCards(userId: String): Int {
        val cardDao = localDb.vocabularyCardDao()
        var syncedCount = 0

        // 1. Upload unsynced
        val unsynced = cardDao.getUnsyncedCards().first()
        unsynced.forEach { card ->
            firestore.collection("users").document(userId)
                .collection("vocabulary_cards").document(card.id)
                .set(card).await()
            syncedCount++
        }
        if (unsynced.isNotEmpty()) {
            cardDao.markSynced(unsynced.map { it.id })
        }

        // 2. Download updates (Server Wins)
        val snapshot = firestore.collection("users").document(userId)
            .collection("vocabulary_cards")
            .get().await()
        
        val remoteCards = snapshot.toObjects(VocabularyCard::class.java)
        if (remoteCards.isNotEmpty()) {
            // "Server Wins" logic: Replace all local with remote if they exist in remote
            // In a more complex app, we'd check timestamps, but here we follow server authority.
            val updatedRemoteCards = remoteCards.map { it.copy(synced = true) }
            cardDao.insertAll(updatedRemoteCards)
        }

        return syncedCount
    }

    suspend fun syncStudySessions(userId: String): Int {
        val sessionDao = localDb.studySessionDao()
        var syncedCount = 0

        val unsynced = sessionDao.getUnsyncedSessions().first()
        unsynced.forEach { session ->
            firestore.collection("users").document(userId)
                .collection("study_sessions").document(session.id.toString())
                .set(session).await()
            syncedCount++
        }
        if (unsynced.isNotEmpty()) {
            sessionDao.markSynced(unsynced.map { it.id })
        }

        return syncedCount
    }

    suspend fun syncProgress(userId: String): Int {
        val progressDao = localDb.dailyProgressDao()
        var syncedCount = 0

        // 1. Upload unsynced
        val unsynced = progressDao.getUnsyncedProgress().first()
        unsynced.forEach { progress ->
            firestore.collection("users").document(userId)
                .collection("daily_progress").document(progress.date)
                .set(progress).await()
            syncedCount++
        }
        if (unsynced.isNotEmpty()) {
            progressDao.markSynced(unsynced.map { it.date })
        }

        // 2. Download (Protect local newer data)
        val snapshot = firestore.collection("users").document(userId)
            .collection("daily_progress").get().await()
        
        val remoteProgress = snapshot.toObjects(DailyProgress::class.java)
        remoteProgress.forEach { remote ->
            val local = progressDao.getByDate(remote.date).first()
            if (local == null || remote.updatedAt > local.updatedAt) {
                progressDao.insert(remote.copy(synced = true))
            }
        }

        return syncedCount
    }

    private fun isOnline(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
