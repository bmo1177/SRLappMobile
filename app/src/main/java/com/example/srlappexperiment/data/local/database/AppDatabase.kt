package com.example.srlappexperiment.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.srlappexperiment.data.local.database.entities.*
import com.example.srlappexperiment.data.local.database.dao.*
import com.example.srlappexperiment.util.Constants

/**
 * Room Database for offline-first storage
 * Version 1.0 - Initial schema
 * No migration support yet - using fallbackToDestructiveMigration in development
 */
@Database(
    entities = [
        VocabularyCard::class,
        StudySession::class,
        DailyProgress::class,
        User::class,
        NotificationTemplate::class,
        GameResult::class,
        Course::class,
        Section::class,
        Lesson::class,
        UserActivityLog::class,
        TeacherDomain::class,
        TeacherCompetence::class,
        TeacherAbility::class,
        TeacherChallenge::class,
        TeacherAttempt::class,
        TeacherScore::class,
        TeacherUserAccount::class,
        TeacherResource::class,
        TeacherUserResourceLog::class,
        TeacherActivity::class,
        TeacherStudentActivity::class,
        TeacherCourseGrade::class
    ],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyCardDao(): VocabularyCardDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun dailyProgressDao(): DailyProgressDao
    abstract fun userDao(): UserDao
    abstract fun notificationTemplateDao(): NotificationTemplateDao
    abstract fun gameResultDao(): GameResultDao
    abstract fun courseDao(): CourseDao
    abstract fun sectionDao(): SectionDao
    abstract fun lessonDao(): LessonDao
    abstract fun userActivityLogDao(): UserActivityLogDao
    abstract fun teacherDao(): TeacherDao
}

