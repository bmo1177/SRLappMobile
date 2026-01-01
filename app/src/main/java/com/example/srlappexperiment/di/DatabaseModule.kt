package com.example.srlappexperiment.di

import android.content.Context
import androidx.room.Room
import com.example.srlappexperiment.data.local.database.AppDatabase
import com.example.srlappexperiment.data.local.database.dao.DailyProgressDao
import com.example.srlappexperiment.data.local.database.dao.GameResultDao
import com.example.srlappexperiment.data.local.database.dao.NotificationTemplateDao
import com.example.srlappexperiment.data.local.database.dao.StudySessionDao
import com.example.srlappexperiment.data.local.database.dao.UserDao
import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database dependency injection module
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideVocabularyCardDao(db: AppDatabase): VocabularyCardDao = db.vocabularyCardDao()

    @Provides
    fun provideStudySessionDao(db: AppDatabase): StudySessionDao = db.studySessionDao()

    @Provides
    fun provideDailyProgressDao(db: AppDatabase): DailyProgressDao = db.dailyProgressDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideNotificationTemplateDao(db: AppDatabase): NotificationTemplateDao = db.notificationTemplateDao()

    @Provides
    fun provideGameResultDao(db: AppDatabase): GameResultDao = db.gameResultDao()

    @Provides
    fun provideTeacherDao(db: AppDatabase): com.example.srlappexperiment.data.local.database.dao.TeacherDao = db.teacherDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Pre-populate teacher's data
                    try {
                        // Domains
                        db.execSQL("INSERT INTO teacher_domains (domain_id, name) VALUES (1, 'Informatique')")
                        db.execSQL("INSERT INTO teacher_domains (domain_id, name) VALUES (2, 'Mathématiques')")
                        
                        // Competences
                        db.execSQL("INSERT INTO teacher_competences (competence_id, domain_id, name) VALUES (1, 1, 'Programmation Python')")
                        db.execSQL("INSERT INTO teacher_competences (competence_id, domain_id, name) VALUES (2, 1, 'Analyse de données')")
                        db.execSQL("INSERT INTO teacher_competences (competence_id, domain_id, name) VALUES (3, 2, 'Algèbre')")
                        
                        // Abilities
                        db.execSQL("INSERT INTO teacher_abilities (ability_id, competence_id, name) VALUES (1, 1, 'Écrire des boucles')")
                        db.execSQL("INSERT INTO teacher_abilities (ability_id, competence_id, name) VALUES (2, 1, 'Utiliser les fonctions')")
                        db.execSQL("INSERT INTO teacher_abilities (ability_id, competence_id, name) VALUES (3, 2, 'Nettoyer les données')")
                        db.execSQL("INSERT INTO teacher_abilities (ability_id, competence_id, name) VALUES (4, 3, 'Résoudre des équations')")
                        
                        // Challenges
                        db.execSQL("INSERT INTO teacher_challenges (challenge_id, competence_id, description) VALUES (1, 1, 'Écrire un programme qui affiche les nombres pairs de 1 à 10')")
                        db.execSQL("INSERT INTO teacher_challenges (challenge_id, competence_id, description) VALUES (2, 2, 'Nettoyer un dataset avec des valeurs manquantes')")
                        
                        // UserAccount
                        db.execSQL("INSERT INTO teacher_user_accounts (user_id, username, password_hash, full_name, role) VALUES (1, 'alice123', 'hash_password1', 'Alice Dupont', 'student')")
                        db.execSQL("INSERT INTO teacher_user_accounts (user_id, username, password_hash, full_name, role) VALUES (2, 'bob456', 'hash_password2', 'Bob Martin', 'student')")
                        
                        // Resources
                        db.execSQL("INSERT INTO teacher_resources (resource_id, competence_id, title, type, url) VALUES (1, 1, 'Boucles en Python - Vidéo', 'video', 'http://example.com/video1')")
                        db.execSQL("INSERT INTO teacher_resources (resource_id, competence_id, title, type, url) VALUES (2, 2, 'Nettoyage de données - Tutoriel', 'video', 'http://example.com/video2')")
                        
                        // Activities
                        db.execSQL("INSERT INTO teacher_activities (activity_id, competence_id, type, title, max_points) VALUES (1, 1, 'video', 'Boucles en Python - Vidéo', 0)")
                        db.execSQL("INSERT INTO teacher_activities (activity_id, competence_id, type, title, max_points) VALUES (2, 2, 'reading', 'Nettoyage de données - Chapitre 1', 0)")
                        db.execSQL("INSERT INTO teacher_activities (activity_id, competence_id, type, title, max_points) VALUES (3, 1, 'discussion', 'Discussion sur les boucles', 0)")
                        db.execSQL("INSERT INTO teacher_activities (activity_id, competence_id, type, title, max_points) VALUES (4, 1, 'assignment', 'Devoir Python - Exercice 1', 10)")
                    } catch (e: Exception) {
                        // Log error but don't crash database creation
                        android.util.Log.e("DatabaseModule", "Error pre-populating database", e)
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }
}

