package com.example.mealmind.data

import com.google.gson.Gson

data class FoodData(
    val name: String,
    val calories: Int,
    val proteins: Double,
    val carbs: Double,
    val fats: Double
) {
    fun toJson(): String = Gson().toJson(this)
}

data class FoodHistoryItem(
    val id: String = "",
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis()
)