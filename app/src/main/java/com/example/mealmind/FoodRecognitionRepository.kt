package com.example.mealmind.data.repositories

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRecognitionRepository @Inject constructor() {

    suspend fun analyzeFood(bitmap: Bitmap): List<ImageLabel> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        return try {
            labeler.process(image).await()
        } catch (e: Exception) {
            emptyList()
        }
    }
}