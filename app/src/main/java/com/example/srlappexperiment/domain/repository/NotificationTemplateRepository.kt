package com.example.srlappexperiment.domain.repository

import com.example.srlappexperiment.domain.model.EngagementLevel
import com.example.srlappexperiment.domain.model.NotificationTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification templates stored in Firestore/Room
 */
interface NotificationTemplateRepository {
    fun getTemplatesForSegment(segment: EngagementLevel): Flow<List<NotificationTemplate>>
    suspend fun recordClick(templateId: String, segment: EngagementLevel)
    suspend fun markAsUsed(templateId: String, userId: String)
    suspend fun syncTemplates()
}
