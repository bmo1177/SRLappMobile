package com.example.srlappexperiment.data.remote.firebase

import com.example.srlappexperiment.domain.model.NotificationPreferences
import com.example.srlappexperiment.domain.model.User
import com.example.srlappexperiment.util.Constants
import com.example.srlappexperiment.util.auth.AuthValidator
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase Authentication Manager
 * Handles all authentication operations with Firebase Auth and Firestore
 */
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    /**
     * Registers a new user with email and password
     * Automatically creates user document in Firestore
     */
    fun registerWithEmail(email: String, password: String): Flow<Result<User>> = flow {
        try {
            // Validate inputs (Basic validation as requested)
            val emailError = AuthValidator.getEmailErrorMessage(email)
            if (emailError != null) {
                emit(Result.failure(IllegalArgumentException(emailError)))
                return@flow
            }

            if (password.length < 8) {
                emit(Result.failure(IllegalArgumentException("Password must be at least 8 characters")))
                return@flow
            }

            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("User creation failed")

            // Create user profile in Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
                createdAt = System.currentTimeMillis(),
                lastActive = System.currentTimeMillis(),
                engagementLevel = "medium",
                notificationPreferences = NotificationPreferences()
            )

            // Save to Firestore
            saveUserToFirestore(user)

            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Logs in user with email and password
     */
    fun loginWithEmail(email: String, password: String): Flow<Result<User>> = flow {
        try {
            if (password.isBlank()) {
                emit(Result.failure(IllegalArgumentException("Password cannot be empty")))
                return@flow
            }

            // Sign in with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Login failed")

            // Get user from Firestore
            val user = getUserFromFirestore(firebaseUser.uid) ?: run {
                // Should not happen normally if registration was successful
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                saveUserToFirestore(newUser)
                newUser
            }

            // Update last active
            updateLastActive(firebaseUser.uid)

            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Logs in user with Google Sign-In
     */
    fun loginWithGoogle(idToken: String): Flow<Result<User>> = flow {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Google sign-in failed")

            // Get or create user in Firestore
            val user = getUserFromFirestore(firebaseUser.uid) ?: run {
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                saveUserToFirestore(newUser)
                newUser
            }

            // Update last active
            updateLastActive(firebaseUser.uid)

            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Logs out the current user
     */
    fun logout(): Flow<Result<Unit>> = flow {
        try {
            auth.signOut()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Sends password reset email
     */
    fun resetPassword(email: String): Flow<Result<Unit>> = flow {
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Gets current authenticated user
     * Observes auth state changes
     */
    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Fetch user from Firestore using scoped coroutine
                scope.launch {
                    try {
                        val user = getUserFromFirestore(firebaseUser.uid)
                        trySend(user)
                    } catch (e: Exception) {
                        trySend(null)
                    }
                }
            } else {
                trySend(null)
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
            scope.cancel() // Cancel scope when flow is closed
        }
    }

    /**
     * Updates user profile
     */
    fun updateUserProfile(displayName: String): Flow<Result<Unit>> = flow {
        try {
            val firebaseUser = auth.currentUser
                ?: throw IllegalStateException("No user logged in")

            // Update Firebase Auth profile
            val profileBuilder = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileBuilder).await()

            // Update Firestore
            val updates = hashMapOf<String, Any>(
                "displayName" to displayName,
                "lastActive" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .update(updates)
                .await()

            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Saves the assessed user level to Firestore
     */
    fun saveUserLevel(level: String): Flow<Result<Unit>> = flow {
        try {
            val firebaseUser = auth.currentUser
                ?: throw IllegalStateException("No user logged in")

            db.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .update("level", level)
                .await()

            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Saves user to Firestore
     */
    private suspend fun saveUserToFirestore(user: User) {
        val userMap = hashMapOf<String, Any>(
            "email" to user.email,
            "userType" to user.userType,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "lastActive" to com.google.firebase.Timestamp.now(),
            "engagementLevel" to user.engagementLevel,
            "notificationPreferences" to mapOf(
                "enabled" to (user.notificationPreferences?.enabled ?: true),
                "quietHoursStart" to (user.notificationPreferences?.quietHoursStart ?: "22:00"),
                "quietHoursEnd" to (user.notificationPreferences?.quietHoursEnd ?: "08:00"),
                "frequency" to (user.notificationPreferences?.frequency ?: "daily")
            )
        )

        user.displayName?.let { userMap["displayName"] = it }
        user.photoUrl?.let { userMap["photoURL"] = it }
        user.targetScore?.let { userMap["targetScore"] = it }

        db.collection(Constants.COLLECTION_USERS)
            .document(user.id)
            .set(userMap)
            .await()
    }

    /**
     * Gets user from Firestore (suspend)
     */
    private suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            val document = db.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                documentToUser(document)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gets user from Firestore (non-blocking for callback)
     */
    private suspend fun getUserFromFirestoreAsync(userId: String): User? {
        return getUserFromFirestore(userId)
    }

    /**
     * Converts Firestore document to User domain model
     */
    private fun documentToUser(document: com.google.firebase.firestore.DocumentSnapshot): User {
        val data = document.data ?: return User(id = document.id)

        val notificationPrefs = data["notificationPreferences"] as? Map<*, *>
        val notificationPreferences = notificationPrefs?.let {
            NotificationPreferences(
                enabled = it["enabled"] as? Boolean ?: true,
                quietHoursStart = it["quietHoursStart"] as? String ?: "22:00",
                quietHoursEnd = it["quietHoursEnd"] as? String ?: "08:00",
                frequency = it["frequency"] as? String ?: "daily"
            )
        }

        val createdAtTimestamp = data["createdAt"] as? com.google.firebase.Timestamp
        val lastActiveTimestamp = data["lastActive"] as? com.google.firebase.Timestamp
        val examDateTimestamp = data["examDate"] as? com.google.firebase.Timestamp

        return User(
            id = document.id,
            email = data["email"] as? String ?: "",
            displayName = data["displayName"] as? String,
            photoUrl = data["photoURL"] as? String,
            userType = data["userType"] as? String ?: "general",
            targetScore = (data["targetScore"] as? Number)?.toInt(),
            examDate = examDateTimestamp?.let { it.seconds * 1000 + it.nanoseconds / 1000000 },
            createdAt = createdAtTimestamp?.let { it.seconds * 1000 + it.nanoseconds / 1000000 } ?: System.currentTimeMillis(),
            lastActive = lastActiveTimestamp?.let { it.seconds * 1000 + it.nanoseconds / 1000000 } ?: System.currentTimeMillis(),
            engagementLevel = data["engagementLevel"] as? String ?: "medium",
            level = data["level"] as? String,
            notificationPreferences = notificationPreferences
        )
    }

    /**
     * Updates last active timestamp
     */
    private suspend fun updateLastActive(userId: String) {
        try {
            db.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update("lastActive", com.google.firebase.Timestamp.now())
                .await()
        } catch (e: Exception) {
            // Silently fail - not critical
        }
    }
}

