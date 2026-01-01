package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.StudySessionDao
import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.StudySession
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VocabularyRepository using Room database
 */
@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val vocabularyCardDao: VocabularyCardDao,
    private val studySessionDao: StudySessionDao
) : VocabularyRepository {

    override fun getDueCards(limit: Int): Flow<List<VocabularyCard>> {
        val now = System.currentTimeMillis()
        return vocabularyCardDao.getDueCards(now, limit)
    }

    override fun getNewCards(limit: Int): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getNewCards(limit)
    }

    override fun getAllCards(): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getAllCards()
    }

    override suspend fun updateCard(card: VocabularyCard) {
        vocabularyCardDao.update(card)
    }

    override suspend fun saveStudySession(session: StudySession): Long {
        return studySessionDao.insert(session)
    }

    override suspend fun getReviewQueueCount(): Int {
        val now = System.currentTimeMillis()
        val dueCards = vocabularyCardDao.getDueCards(now, Int.MAX_VALUE).first()
        return dueCards.size
    }

    override fun getUnsyncedCards(): Flow<List<VocabularyCard>> {
        return vocabularyCardDao.getUnsyncedCards()
    }

    override fun getTotalCardsLearned(): Flow<Int> {
        return vocabularyCardDao.getTotalCardsLearned()
    }
}
