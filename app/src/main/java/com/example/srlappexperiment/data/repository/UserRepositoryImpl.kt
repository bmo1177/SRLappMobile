package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.data.local.database.dao.UserDao
import com.example.srlappexperiment.data.local.database.entities.User
import com.example.srlappexperiment.domain.model.EngagementLevel
import com.example.srlappexperiment.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUser(userId: String): Flow<User?> {
        return userDao.getById(userId)
    }

    override suspend fun saveUser(user: User) {
        userDao.insert(user)
        firestore.collection("users").document(user.id).set(user).await()
    }

    override suspend fun updateEngagementLevel(userId: String, level: EngagementLevel) {
        val currentUser: User? = userDao.getById(userId).firstOrNull()
        currentUser?.let { user ->
            val updatedUser = user.copy(engagementLevel = level.name, synced = false)
            userDao.update(updatedUser)
            firestore.collection("users").document(userId)
                .update("engagementLevel", level.name).await()
        }
    }

    override suspend fun updateLastActive(userId: String) {
        val now = System.currentTimeMillis()
        firestore.collection("users").document(userId)
            .update("lastActive", now).await()
    }

    override suspend fun updateProfile(
        userId: String,
        displayName: String?,
        userType: String,
        targetScore: Int?,
        examDate: Long?
    ) {
        val currentUser: User? = userDao.getById(userId).firstOrNull()
        currentUser?.let { user ->
            val updatedUser = user.copy(
                displayName = displayName,
                userType = userType,
                targetScore = targetScore,
                examDate = examDate,
                synced = false
            )
            userDao.update(updatedUser)
            val updates = mapOf(
                "displayName" to displayName,
                "userType" to userType,
                "targetScore" to targetScore,
                "examDate" to examDate
            )
            firestore.collection("users").document(userId).update(updates).await()
        }
    }

    override suspend fun deleteAccount(userId: String) {
        // Delete from Firestore
        firestore.collection("users").document(userId).delete().await()
        // Local cleanup is handled by Room cascading or manual delete if needed
        // For now, we delete the user record
        val user: User? = userDao.getById(userId).firstOrNull()
        user?.let { existingUser -> userDao.delete(existingUser) }
    }
}
