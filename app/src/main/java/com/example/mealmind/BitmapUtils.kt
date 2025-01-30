package com.example.mealmind

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtils {
    fun rotateAndFlip(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postRotate(90f)
            postScale(-1f, 1f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}