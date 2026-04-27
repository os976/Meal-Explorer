package com.example.mealexplorer.data.model

/**
 * UI model for a category chip on the Home screen.
 *
 * The API gives us idCategory, strCategory, strCategoryThumb and a description.
 * On Home we only need the name to display on the chip, but we keep the rest in
 * case the trainee wants to extend the UI later (e.g. show a thumbnail next to
 * the chip).
 */
data class Category(
    val id: String,
    val name: String,
    val imageUrl: String? = null
)
