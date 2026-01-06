package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.StudySessionDao
import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VocabularyRepository using Room database
 * Includes in-memory caching with TTL for performance optimization
 */
@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val vocabularyCardDao: VocabularyCardDao,
    private val studySessionDao: StudySessionDao
) : VocabularyRepository {

    // Cache configuration
    private companion object {
        const val CACHE_TTL_MS = 30_000L // 30 seconds cache TTL
    }

    // In-memory cache for due cards
    private data class CacheEntry<T>(
        val data: T,
        val timestamp: Long
    ) {
        fun isValid(): Boolean = System.currentTimeMillis() - timestamp < CACHE_TTL_MS
    }

    private var dueCardsCache: CacheEntry<List<VocabularyCard>>? = null
    private var newCardsCache: CacheEntry<List<VocabularyCard>>? = null
    private var reviewQueueCountCache: CacheEntry<Int>? = null
    private val cacheMutex = Mutex()

    override fun getDueCards(limit: Int): Flow<List<VocabularyCard>> = flow {
        // Check cache first
        cacheMutex.withLock {
            dueCardsCache?.let { cache ->
                if (cache.isValid()) {
                    emit(cache.data.take(limit))
                    return@flow
                }
            }
        }

        // Fetch from database
        val now = System.currentTimeMillis()
        vocabularyCardDao.getDueCards(now, limit).collect { cards ->
            cacheMutex.withLock {
                dueCardsCache = CacheEntry(cards, System.currentTimeMillis())
            }
            emit(cards)
        }
    }

    override fun getNewCards(limit: Int): Flow<List<VocabularyCard>> = flow {
        // Check cache first
        cacheMutex.withLock {
            newCardsCache?.let { cache ->
                if (cache.isValid()) {
                    emit(cache.data.take(limit))
                    return@flow
                }
            }
        }

        // Fetch from database
        vocabularyCardDao.getNewCards(limit).collect { cards ->
            cacheMutex.withLock {
                newCardsCache = CacheEntry(cards, System.currentTimeMillis())
            }
            emit(cards)
        }
    }

    override fun getAllCards(): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getAllCards()
    }

    override suspend fun updateCard(card: VocabularyCard) {
        vocabularyCardDao.update(card)
        // Invalidate caches on card update for data consistency
        invalidateCache()
    }

    override suspend fun saveStudySession(session: StudySession): Long {
        return studySessionDao.insert(session)
    }

    override suspend fun getReviewQueueCount(): Int {
        // Check cache first
        cacheMutex.withLock {
            reviewQueueCountCache?.let { cache ->
                if (cache.isValid()) {
                    return cache.data
                }
            }
        }

        // Fetch from database
        val now = System.currentTimeMillis()
        val count = vocabularyCardDao.getDueCards(now, Int.MAX_VALUE).first().size
        
        cacheMutex.withLock {
            reviewQueueCountCache = CacheEntry(count, System.currentTimeMillis())
        }
        
        return count
    }

    override fun getUnsyncedCards(): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getUnsyncedCards()
    }

    override fun getTotalCardsLearned(): Flow<Int> {
        return vocabularyCardDao.getTotalCardsLearned()
    }

    /**
     * Invalidate all caches. Called when data is modified.
     */
    suspend fun invalidateCache() {
        cacheMutex.withLock {
            dueCardsCache = null
            newCardsCache = null
            reviewQueueCountCache = null
        }
    }
}
