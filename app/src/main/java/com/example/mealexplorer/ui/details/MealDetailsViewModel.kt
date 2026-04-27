package com.example.mealexplorer.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealexplorer.data.model.MealDetails
import com.example.mealexplorer.data.remote.ApiResult
import com.example.mealexplorer.data.repository.MealRepository
import kotlinx.coroutines.launch

/**
 * Loads a single meal by id via lookup.php.
 *
 * The fragment passes the meal id once via [load]. The ViewModel keeps the id
 * so a "Retry" tap can re-issue the same request without the fragment having
 * to remember it.
 */
class MealDetailsViewModel(
    private val repository: MealRepository = MealRepository()
) : ViewModel() {

    private val _state = MutableLiveData<ApiResult<MealDetails>>()
    val state: LiveData<ApiResult<MealDetails>> = _state

    private var currentMealId: String? = null

    fun load(mealId: String) {
        currentMealId = mealId
        fetch()
    }

    fun retry() {
        if (currentMealId != null) fetch()
    }

    private fun fetch() {
        val id = currentMealId ?: return
        _state.value = ApiResult.Loading
        viewModelScope.launch {
            _state.value = repository.getMealDetails(id)
        }
    }
}
