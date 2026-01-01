package com.example.srlappexperiment.data.repository

import com.example.srlappexperiment.domain.model.EngagementLevel
import com.example.srlappexperiment.domain.model.NotificationTemplate
import com.example.srlappexperiment.domain.repository.NotificationTemplateRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTemplateRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationTemplateRepository {

    override fun getTemplatesForSegment(segment: EngagementLevel): Flow<List<NotificationTemplate>> {
        return firestore.collection("notification_templates")
            .whereEqualTo("segment", segment.name.lowercase())
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    val ctrMap = doc.get("clickThroughRate") as? Map<String, Double> ?: emptyMap()
                    val domainCtr = ctrMap.mapKeys { 
                        EngagementLevel.fromString(it.key) 
                    }.mapValues { it.value.toFloat() }

                    NotificationTemplate(
                        id = doc.id,
                        text = doc.getString("text") ?: "",
                        segment = segment,
                        clickThroughRates = domainCtr,
                        lastUsedTimestamp = doc.getLong("lastUsedByUser")
                    )
                }
            }
    }

    override suspend fun recordClick(templateId: String, segment: EngagementLevel) {
        val segmentKey = segment.name.lowercase()
        firestore.collection("notification_templates").document(templateId)
            .update("clickThroughRate.$segmentKey", FieldValue.increment(0.01)) // Simplified CTR update
            .await()
    }

    override suspend fun markAsUsed(templateId: String, userId: String) {
        // In a real app, this might be per-user. The prompt suggests 'lastUsedByUser' field.
        firestore.collection("notification_templates").document(templateId)
            .update("lastUsedByUser", System.currentTimeMillis())
            .await()
    }

    override suspend fun syncTemplates() {
        // Logic to sync Firestore templates to local database if needed
    }
}
