package com.example.srlappexperiment.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.srlappexperiment.domain.notifications.NotificationPersonalizationEngine
import com.example.srlappexperiment.presentation.ui.notifications.NotificationManagerHelper
import com.example.srlappexperiment.domain.repository.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val engine: NotificationPersonalizationEngine,
    private val authRepository: AuthRepository,
    private val notificationHelper: NotificationManagerHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val user = authRepository.getCurrentUser().first()
        val userId = user?.id ?: return Result.failure()

        // 1. Select optimal template
        val template = engine.selectOptimalNotification(userId).first() ?: return Result.success()

        // 2. Identify optimal time
        val sendTime = engine.getOptimalSendTime(userId).first()
        val now = System.currentTimeMillis()
        val delay = (sendTime - now).coerceAtLeast(0)

        // 3. Schedule the actual display worker with delay
        val displayWork = OneTimeWorkRequestBuilder<DisplayNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "user_id" to userId,
                "template_id" to template.id,
                "segment" to template.segment.name,
                "notification_text" to template.text
            ))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "display_notification_${template.id}",
            ExistingWorkPolicy.REPLACE,
            displayWork
        )

        return Result.success()
    }
}

@HiltWorker
class DisplayNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationManagerHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("user_id") ?: ""
        val templateId = inputData.getString("template_id") ?: ""
        val segment = inputData.getString("segment") ?: "LOW"
        val text = inputData.getString("notification_text") ?: return Result.failure()

        notificationHelper.showNotification(
            title = "SRL Practice", 
            message = text,
            userId = userId,
            templateId = templateId,
            segment = segment
        )
        return Result.success()
    }
}
