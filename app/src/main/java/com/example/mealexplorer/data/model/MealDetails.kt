package com.example.mealexplorer.data.model

/**
 * UI model for the Meal Details screen.
 *
 * The API stores ingredients in 20 separate string fields (strIngredient1..20)
 * with matching measure fields (strMeasure1..20). The repository flattens those
 * into a clean list of [Ingredient] items so the UI does not have to deal with
 * 40 nullable strings.
 */
data class MealDetails(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val measure: String
)
