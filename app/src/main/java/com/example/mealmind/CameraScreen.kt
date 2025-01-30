package com.example.mealmind.ui.screens.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.media.Image
import com.google.gson.Gson
import java.net.URLEncoder
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mealmind.BitmapUtils
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors


@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CameraViewModel = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            imageCapture = imageCapture,
            lifecycleOwner = lifecycleOwner
        )

        FloatingActionButton(
            onClick = {
                viewModel.captureImage(
                    imageCapture = imageCapture,
                    executor = cameraExecutor,
                    onSuccess = { bitmap ->
                        bitmap?.let {
                            val processedBitmap = BitmapUtils.rotateAndFlip(bitmap)
                            viewModel.analyzeFood(processedBitmap) { results ->
                                if (results.isNotEmpty()) {
                                    val json = Gson().toJson(results)
                                    val encoded = URLEncoder.encode(json, "UTF-8")
                                    navController.navigate("result/$encoded")
                                } else {
                                    // Show error message
                                }
                            }
                        }
                    },
                    onError = { Log.e("CameraScreen", "Capture failed", it) }
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Icon(Icons.Default.Camera, "Capture")
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = modifier,
        update = { previewView ->
            val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch(exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

// Extension function to convert ImageProxy to Bitmap
@OptIn(ExperimentalGetImage::class)
private fun ImageProxy.toBitmap(): Bitmap {
    val image = this.image ?: throw IllegalStateException("Image is null")
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val matrix = Matrix().apply {
        postRotate(this@toBitmap.imageInfo.rotationDegrees.toFloat())
        if (this@toBitmap.imageInfo.rotationDegrees == 90 ||
            this@toBitmap.imageInfo.rotationDegrees == 270) {
            postScale(-1f, 1f)
        }
    }

    return Bitmap.createBitmap(
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size),
        0, 0,
        this.width,
        this.height,
        matrix,
        true
    )
}