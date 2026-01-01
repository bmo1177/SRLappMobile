package com.example.srlappexperiment.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Status of the synchronization process
 */
@Stable
sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(val result: SyncResult) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

/**
 * Result details of a synchronization operation
 */
@Immutable
data class SyncResult(
    val cardsSynced: Int = 0,
    val sessionsSynced: Int = 0,
    val progressSynced: Int = 0,
    val durationMillis: Long = 0
)
