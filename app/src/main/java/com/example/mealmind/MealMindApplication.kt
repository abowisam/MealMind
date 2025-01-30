package com.example.mealmind

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MealMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase should auto-initialize if google-services.json is properly set up
    }
}