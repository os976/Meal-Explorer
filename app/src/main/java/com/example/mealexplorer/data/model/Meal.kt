package com.example.mealexplorer.data.model

/**
 * UI model for a meal card on the Home screen.
 *
 * Some endpoints (filter.php) return only the basic info, while others (search.php)
 * return the full meal. We keep this UI model lean and treat [category] as nullable
 * because filter.php does not return it - in that case we fill it from the chip
 * selection at the UI layer when needed.
 */
data class Meal(
    val id: String,
    val name: String,
    val category: String?,
    val imageUrl: String
)
