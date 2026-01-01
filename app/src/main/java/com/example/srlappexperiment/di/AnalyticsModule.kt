package com.example.srlappexperiment.di

import android.content.Context
import com.example.srlappexperiment.data.analytics.AnalyticsManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.ktx.performance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance {
        return Firebase.performance
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(analytics: FirebaseAnalytics): AnalyticsManager {
        return AnalyticsManager(analytics)
    }
}
