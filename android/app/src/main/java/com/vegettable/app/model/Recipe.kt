package com.vegettable.app.model

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("ingredients") val ingredients: List<String>? = null,
    @SerializedName("difficulty") val difficulty: String?, // "easy" | "medium" | "hard"
    @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int
)
