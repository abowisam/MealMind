package com.example.mealmind.ui.screens.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder

@Composable
fun ResultScreen(encodedLabels: String?) {
    val results = remember(encodedLabels) {
        try {
            encodedLabels?.let {
                val decoded = URLDecoder.decode(it, "UTF-8")
                val type = object : TypeToken<List<Pair<String, Float>>>() {}.type
                Gson().fromJson<List<Pair<String, Float>>>(decoded, type)
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (results.isEmpty()) {
            Text("No food items detected")
        } else {
            results.forEach { (label, confidence) ->
                Text(
                    text = "$label (${"%.1f".format(confidence * 100)}%)",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

// Add this in a separate file or at the bottom
data class ImageLabel(
    val text: String,
    val confidence: Float,
    val index: Int
)