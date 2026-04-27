package com.example.mealexplorer.data.repository

import com.example.mealexplorer.data.model.Category
import com.example.mealexplorer.data.model.Ingredient
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.data.model.MealDetails
import com.example.mealexplorer.data.remote.ApiResult
import com.example.mealexplorer.data.remote.MealApiService
import com.example.mealexplorer.data.remote.dto.MealDto
import com.example.mealexplorer.util.cleanText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Single source of truth for meal/category data.
 *
 * Responsibilities:
 *  - Calls the API via [api].
 *  - Catches network/parse errors and converts them into [ApiResult.Error].
 *  - Maps the raw DTOs into the lean UI models that [Meal], [MealDetails], and
 *    [Category] expose.
 *
 * Repositories never expose Loading - that is a UI concern handled in the
 * ViewModel.
 */
class MealRepository(
    private val api: MealApiService = MealApiService.instance
) {

    suspend fun getCategories(): ApiResult<List<Category>> = safeCall {
        val response = api.getCategories()
        val mapped = response.categories
            ?.mapNotNull { dto ->
                val id = dto.idCategory ?: return@mapNotNull null
                val name = dto.strCategory ?: return@mapNotNull null
                Category(id = id, name = name, imageUrl = dto.strCategoryThumb)
            }
            .orEmpty()
        if (mapped.isEmpty()) ApiResult.Empty else ApiResult.Success(mapped)
    }

    /** Default first load - "search.php?f=a" returns meals starting with 'a'. */
    suspend fun getDefaultMeals(): ApiResult<List<Meal>> = safeCall {
        val response = api.searchMealsByFirstLetter("a")
        toMealsResult(response.meals)
    }

    /** Search by meal name: search.php?s=query. */
    suspend fun searchMeals(query: String): ApiResult<List<Meal>> = safeCall {
        val response = api.searchMealsByName(query)
        toMealsResult(response.meals)
    }

    /**
     * Filter by category: filter.php?c=Category.
     *
     * filter.php only returns id/name/thumb, so we backfill [Meal.category] with
     * the chip the user selected - that way the meal cards still show a
     * category label.
     */
    suspend fun getMealsByCategory(category: String): ApiResult<List<Meal>> = safeCall {
        val response = api.filterByCategory(category)
        val meals = response.meals
            ?.mapNotNull { dto ->
                val id = dto.idMeal ?: return@mapNotNull null
                val name = dto.strMeal ?: return@mapNotNull null
                val thumb = dto.strMealThumb.orEmpty()
                Meal(id = id, name = name, category = category, imageUrl = thumb)
            }
            .orEmpty()
        if (meals.isEmpty()) ApiResult.Empty else ApiResult.Success(meals)
    }

    /** Full meal details: lookup.php?i=mealId. */
    suspend fun getMealDetails(mealId: String): ApiResult<MealDetails> = safeCall {
        val response = api.lookupMealById(mealId)
        val dto = response.meals?.firstOrNull()
            ?: return@safeCall ApiResult.Empty
        ApiResult.Success(toMealDetails(dto))
    }

    // ------------------------------------------------------------------
    // Mapping helpers
    // ------------------------------------------------------------------

    private fun toMealsResult(dtos: List<MealDto>?): ApiResult<List<Meal>> {
        val meals = dtos
            ?.mapNotNull { dto ->
                val id = dto.idMeal ?: return@mapNotNull null
                val name = dto.strMeal ?: return@mapNotNull null
                Meal(
                    id = id,
                    name = name,
                    category = dto.strCategory,
                    imageUrl = dto.strMealThumb.orEmpty()
                )
            }
            .orEmpty()
        return if (meals.isEmpty()) ApiResult.Empty else ApiResult.Success(meals)
    }

    private fun toMealDetails(dto: MealDto): MealDetails {
        val ingredients = dto.ingredientsZipped()
            .mapNotNull { (name, measure) ->
                val cleanName = name?.trim().orEmpty()
                if (cleanName.isEmpty()) return@mapNotNull null
                Ingredient(
                    name = cleanName,
                    measure = measure?.trim().orEmpty()
                )
            }
        return MealDetails(
            id = dto.idMeal.orEmpty(),
            name = dto.strMeal.orEmpty(),
            category = dto.strCategory.orEmpty(),
            area = dto.strArea.orEmpty(),
            instructions = dto.strInstructions.cleanText(),
            imageUrl = dto.strMealThumb.orEmpty(),
            ingredients = ingredients
        )
    }

    // ------------------------------------------------------------------
    // Common error wrapping
    // ------------------------------------------------------------------

    private suspend inline fun <T> safeCall(
        crossinline block: suspend () -> ApiResult<T>
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (io: IOException) {
            ApiResult.Error("No internet connection. Please try again.")
        } catch (t: Throwable) {
            ApiResult.Error(t.localizedMessage ?: "Unexpected error")
        }
    }
}
