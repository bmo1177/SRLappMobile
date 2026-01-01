package com.example.srlappexperiment.di

import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.repository.FirebaseAuthRepositoryImpl
import com.example.srlappexperiment.data.repository.ProgressRepositoryImpl
import com.example.srlappexperiment.data.repository.StudySessionRepositoryImpl
import com.example.srlappexperiment.data.repository.VocabularyRepositoryImpl
import com.example.srlappexperiment.data.repository.UserRepositoryImpl
import com.example.srlappexperiment.data.repository.NotificationTemplateRepositoryImpl
import com.example.srlappexperiment.data.repository.GameRepositoryImpl
import com.example.srlappexperiment.data.repository.PreferencesRepositoryImpl
import com.example.srlappexperiment.data.remote.firebase.FirebaseAuthManager
import com.example.srlappexperiment.domain.repository.AuthRepository
import com.example.srlappexperiment.domain.repository.ProgressRepository
import com.example.srlappexperiment.domain.repository.StudySessionRepository
import com.example.srlappexperiment.domain.repository.VocabularyRepository
import com.example.srlappexperiment.domain.repository.UserRepository
import com.example.srlappexperiment.domain.repository.NotificationTemplateRepository
import com.example.srlappexperiment.domain.repository.GameRepository
import com.example.srlappexperiment.domain.repository.PreferencesRepository
import com.example.srlappexperiment.domain.usecase.SpacedRepetitionEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository dependency injection module
 * Provides use cases and repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVocabularyRepository(
        vocabularyRepositoryImpl: VocabularyRepositoryImpl
    ): VocabularyRepository

    @Binds
    @Singleton
    abstract fun bindStudySessionRepository(
        studySessionRepositoryImpl: StudySessionRepositoryImpl
    ): StudySessionRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindNotificationTemplateRepository(
        notificationTemplateRepositoryImpl: NotificationTemplateRepositoryImpl
    ): NotificationTemplateRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideSpacedRepetitionEngine(
            vocabularyCardDao: VocabularyCardDao
        ): SpacedRepetitionEngine {
            return SpacedRepetitionEngine(vocabularyCardDao)
        }

        @Provides
        @Singleton
        fun provideGeminiManager(): com.example.srlappexperiment.data.remote.ai.GeminiManager {
            return com.example.srlappexperiment.data.remote.ai.GeminiManager()
        }

    }
}
