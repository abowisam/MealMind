package com.example.mealmind.ui.screens.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    fun captureImage(
        imageCapture: ImageCapture,
        executor: Executor,
        onSuccess: (Bitmap?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        onSuccess(image.toBitmap())
                    } catch (e: Exception) {
                        onError(e)
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    fun analyzeFood(
        bitmap: Bitmap,
        onResult: (List<Pair<String, Float>>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                val labels = labeler.process(image).await()

                // Convert to simple pairs
                val results = labels.map {
                    Pair(it.text ?: "Unknown", it.confidence ?: 0f)
                }

                onResult(results)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }
}