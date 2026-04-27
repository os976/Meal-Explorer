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

class HomeViewModel(
    private val repository: MealRepository = MealRepository()
) : ViewModel() {

    private val _categoriesState = MutableLiveData<ApiResult<List<Category>>>()
    val categoriesState: LiveData<ApiResult<List<Category>>> = _categoriesState

    private val _mealsState = MutableLiveData<ApiResult<List<Meal>>>()
    val mealsState: LiveData<ApiResult<List<Meal>>> = _mealsState

    var selectedCategoryId: String = ALL_CATEGORY
        private set

    private var searchQuery: String = ""
    private var searchJob: Job? = null

    init {
        loadCategories()
        loadMeals()
    }

    fun loadCategories() {
        _categoriesState.value = ApiResult.Loading
        viewModelScope.launch {
            _categoriesState.value = repository.getCategories()
        }
    }

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

        searchQuery = ""
        loadMeals()
    }


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
