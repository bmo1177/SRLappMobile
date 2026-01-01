package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.srlappexperiment.data.local.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomains(domains: List<TeacherDomain>)

    @Query("SELECT * FROM teacher_domains")
    fun getAllDomains(): Flow<List<TeacherDomain>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetences(competences: List<TeacherCompetence>)

    @Query("SELECT * FROM teacher_competences WHERE domain_id = :domainId")
    fun getCompetencesByDomain(domainId: Int): Flow<List<TeacherCompetence>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbilities(abilities: List<TeacherAbility>)

    @Query("SELECT * FROM teacher_abilities WHERE competence_id = :competenceId")
    fun getAbilitiesByCompetence(competenceId: Int): Flow<List<TeacherAbility>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<TeacherChallenge>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: TeacherAttempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: TeacherScore)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: TeacherUserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: TeacherResource)

    @Query("SELECT * FROM teacher_resources WHERE competence_id = :competenceId")
    fun getResourcesByCompetence(competenceId: Int): Flow<List<TeacherResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserResourceLog(log: TeacherUserResourceLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: TeacherActivity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentActivity(studentActivity: TeacherStudentActivity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseGrade(courseGrade: TeacherCourseGrade)
}
