package com.example.srlappexperiment.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.srlappexperiment.data.local.database.entities.NotificationTemplate
import kotlinx.coroutines.flow.Flow

/**
 * DAO for NotificationTemplate entity
 */
@Dao
interface NotificationTemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<NotificationTemplate>)

    @Query("SELECT * FROM notification_templates WHERE segment = :segment")
    fun getBySegment(segment: String): Flow<List<NotificationTemplate>>

    @Query("UPDATE notification_templates SET clickThroughRate = :rate WHERE id = :id")
    suspend fun updateClickThroughRate(id: String, rate: Float)
}

