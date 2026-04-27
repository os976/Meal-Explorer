package com.example.mealexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealexplorer.data.model.Category
import com.example.mealexplorer.data.model.Meal
import com.example.mealexplorer.data.remote.ApiResult
import com.example.mealexplorer.data.repository.MealRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Drives the Home screen.
 *
 * Two LiveData streams are exposed:
 *  - [categoriesState]: state of the chip list (loaded once on screen open).
 *  - [mealsState]: state of the meal grid below the chips.
 *
 * The "current selection" is owned here so configuration changes (rotation)
 * keep the same chip / search applied:
 *  - [selectedCategoryId] = "all"        -> default meals
 *  - [selectedCategoryId] = "<name>"     -> filter.php?c=<name>
 *  - [searchQuery]   non-empty           -> search.php?s=<query>  (overrides chip)
 *
 * A small debounce is applied to search so we don't hit the API on every
 * keystroke.
 */
class HomeViewModel(
    private val repository: MealRepository = MealRepository()
) : ViewModel() {

    private val _categoriesState = MutableLiveData<ApiResult<List<Category>>>()
    val categoriesState: LiveData<ApiResult<List<Category>>> = _categoriesState

    private val _mealsState = MutableLiveData<ApiResult<List<Meal>>>()
    val mealsState: LiveData<ApiResult<List<Meal>>> = _mealsState

    /** "all" means show default meals. Otherwise it's a category name. */
    var selectedCategoryId: String = ALL_CATEGORY
        private set

    private var searchQuery: String = ""
    private var searchJob: Job? = null

    init {
        loadCategories()
        loadMeals()
    }

    // ------------------------------------------------------------------
    // Public actions
    // ------------------------------------------------------------------

    fun loadCategories() {
        _categoriesState.value = ApiResult.Loading
        viewModelScope.launch {
            _categoriesState.value = repository.getCategories()
        }
    }

    /**
     * Reloads meals based on current state (search query takes priority over
     * the selected category). Called on initial load and from "Retry".
     */
    fun loadMeals() {
        val query = searchQuery
        val category = selectedCategoryId

        _mealsState.value = ApiResult.Loading
        viewModelScope.launch {
            val result = when {
                query.isNotBlank() -> repository.searchMeals(query)
                category == ALL_CATEGORY -> repository.getDefaultMeals()
                else -> repository.getMealsByCategory(category)
            }
            _mealsState.value = result
        }
    }

    fun onCategorySelected(categoryId: String) {
        if (selectedCategoryId == categoryId) return
        selectedCategoryId = categoryId
        // Selecting a category clears any active search so the user sees
        // results from the new chip immediately.
        searchQuery = ""
        loadMeals()
    }

    /**
     * Called from the search EditText. Debounces by [SEARCH_DEBOUNCE_MS] so
     * fast typing doesn't trigger a request per character.
     */
    fun onSearchQueryChanged(query: String) {
        val trimmed = query.trim()
        if (trimmed == searchQuery) return
        searchQuery = trimmed

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _mealsState.value = ApiResult.Loading
            val result = if (trimmed.isBlank()) {
                // Empty search -> fall back to the active chip selection.
                if (selectedCategoryId == ALL_CATEGORY) {
                    repository.getDefaultMeals()
                } else {
                    repository.getMealsByCategory(selectedCategoryId)
                }
            } else {
                repository.searchMeals(trimmed)
            }
            _mealsState.value = result
        }
    }

    companion object {
        const val ALL_CATEGORY = "all"
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
