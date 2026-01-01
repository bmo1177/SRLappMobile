package com.example.srlappexperiment.data.local.database.entities

import androidx.room.*
import java.util.Date

@Entity(tableName = "teacher_domains")
data class TeacherDomain(
    @PrimaryKey val domain_id: Int,
    val name: String
)

@Entity(
    tableName = "teacher_competences",
    foreignKeys = [ForeignKey(
        entity = TeacherDomain::class,
        parentColumns = ["domain_id"],
        childColumns = ["domain_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherCompetence(
    @PrimaryKey val competence_id: Int,
    val domain_id: Int,
    val name: String
)

@Entity(
    tableName = "teacher_abilities",
    foreignKeys = [ForeignKey(
        entity = TeacherCompetence::class,
        parentColumns = ["competence_id"],
        childColumns = ["competence_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherAbility(
    @PrimaryKey val ability_id: Int,
    val competence_id: Int,
    val name: String
)

@Entity(
    tableName = "teacher_challenges",
    foreignKeys = [ForeignKey(
        entity = TeacherCompetence::class,
        parentColumns = ["competence_id"],
        childColumns = ["competence_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherChallenge(
    @PrimaryKey val challenge_id: Int,
    val competence_id: Int,
    val description: String
)

@Entity(
    tableName = "teacher_attempts",
    foreignKeys = [ForeignKey(
        entity = TeacherChallenge::class,
        parentColumns = ["challenge_id"],
        childColumns = ["challenge_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherAttempt(
    @PrimaryKey val attempt_id: Int,
    val challenge_id: Int,
    val student_name: String,
    val response: String?,
    val attempt_date: String? // Use String for DATE to keep it simple with teacher's format
)

@Entity(
    tableName = "teacher_scores",
    foreignKeys = [ForeignKey(
        entity = TeacherAttempt::class,
        parentColumns = ["attempt_id"],
        childColumns = ["attempt_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherScore(
    @PrimaryKey val score_id: Int,
    val attempt_id: Int,
    val level: Int,
    val points: Int
)

@Entity(tableName = "teacher_user_accounts")
data class TeacherUserAccount(
    @PrimaryKey val user_id: Int,
    val username: String,
    val password_hash: String,
    val full_name: String?,
    val role: String = "student"
)

@Entity(
    tableName = "teacher_resources",
    foreignKeys = [ForeignKey(
        entity = TeacherCompetence::class,
        parentColumns = ["competence_id"],
        childColumns = ["competence_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherResource(
    @PrimaryKey val resource_id: Int,
    val competence_id: Int,
    val title: String,
    val type: String?,
    val url: String?
)

@Entity(
    tableName = "teacher_user_resource_logs",
    foreignKeys = [
        ForeignKey(
            entity = TeacherUserAccount::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeacherResource::class,
            parentColumns = ["resource_id"],
            childColumns = ["resource_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeacherUserResourceLog(
    @PrimaryKey val log_id: Int,
    val user_id: Int,
    val resource_id: Int?,
    val action: String,
    val action_date: String, // TIMESTAMP
    val duration_minutes: Int?
)

@Entity(
    tableName = "teacher_activities",
    foreignKeys = [ForeignKey(
        entity = TeacherCompetence::class,
        parentColumns = ["competence_id"],
        childColumns = ["competence_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeacherActivity(
    @PrimaryKey val activity_id: Int,
    val competence_id: Int,
    val type: String,
    val title: String?,
    val max_points: Int = 0
)

@Entity(
    tableName = "teacher_student_activities",
    foreignKeys = [
        ForeignKey(
            entity = TeacherUserAccount::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeacherActivity::class,
            parentColumns = ["activity_id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeacherStudentActivity(
    @PrimaryKey val student_activity_id: Int,
    val user_id: Int,
    val activity_id: Int,
    val status: String?,
    val start_time: String?,
    val end_time: String?,
    val points_obtained: Int = 0
)

@Entity(
    tableName = "teacher_course_grades",
    foreignKeys = [
        ForeignKey(
            entity = TeacherUserAccount::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeacherCompetence::class,
            parentColumns = ["competence_id"],
            childColumns = ["competence_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeacherCourseGrade(
    @PrimaryKey val grade_id: Int,
    val user_id: Int,
    val competence_id: Int,
    val grade: Float,
    val last_update: String // TIMESTAMP
)
