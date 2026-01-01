package com.example.srlappexperiment.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.srlappexperiment.domain.notifications.NotificationPersonalizationEngine
import com.example.srlappexperiment.domain.model.EngagementLevel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationClickReceiver : BroadcastReceiver() {

    @Inject
    lateinit var engine: NotificationPersonalizationEngine

    override fun onReceive(context: Context, intent: Intent) {
        val userId = intent.getStringExtra("user_id") ?: return
        val templateId = intent.getStringExtra("template_id") ?: return
        val segmentName = intent.getStringExtra("segment") ?: "LOW"
        val segment = EngagementLevel.fromString(segmentName)

        // Record the click asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            engine.recordNotificationClick(userId, templateId, segment).first()
        }
        
        // Optionally launch the app's main activity
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }
}
