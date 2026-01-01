package com.example.srlappexperiment.data.local.database.dao

import androidx.room.*
import com.example.srlappexperiment.data.local.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Query("SELECT * FROM courses WHERE languageCode = :lang")
    fun getCoursesByLanguage(lang: String): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: String): Course?
}

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: Section)

    @Query("SELECT * FROM sections WHERE courseId = :courseId ORDER BY `order` ASC")
    fun getSectionsByCourse(courseId: String): Flow<List<Section>>
}

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Query("SELECT * FROM lessons WHERE sectionId = :sectionId ORDER BY `order` ASC")
    fun getLessonsBySection(sectionId: String): Flow<List<Lesson>>

    @Query("SELECT COUNT(*) FROM lessons")
    fun getTotalLessonCount(): Flow<Int>
}

@Dao
interface UserActivityLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: UserActivityLog)

    @Query("SELECT * FROM user_activity_logs ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<UserActivityLog>>

    @Query("SELECT * FROM user_activity_logs WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLogsByUser(userId: String): Flow<List<UserActivityLog>>
}
