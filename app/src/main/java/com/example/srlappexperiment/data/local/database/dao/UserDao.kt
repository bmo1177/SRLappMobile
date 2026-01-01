package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.srlappexperiment.data.local.database.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User entity
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: String): Flow<User?>

    @Query("SELECT * FROM users WHERE synced = 0")
    fun getUnsyncedUsers(): Flow<List<User>>

    @androidx.room.Delete
    suspend fun delete(user: User)
}

