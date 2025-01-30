package com.example.mealmind.domain

import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import javax.inject.Inject

class CustomModelManager @Inject constructor() {
    private var isModelDownloaded = false

    fun downloadModel(onComplete: (Boolean) -> Unit) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("foodRecognition1", DownloadType.LOCAL_MODEL, conditions)
            .addOnCompleteListener { task ->
                isModelDownloaded = task.isSuccessful
                onComplete(task.isSuccessful)
            }
    }

    fun isModelReady() = isModelDownloaded
}