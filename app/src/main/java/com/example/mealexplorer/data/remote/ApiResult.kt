package com.example.mealexplorer.data.remote

/**
 * Wrapper for a one-shot API result observed by the UI layer.
 *
 * Using a sealed class keeps state handling explicit:
 *  - Loading    -> show progress
 *  - Success    -> show data
 *  - Empty      -> show empty state (e.g. no search hits)
 *  - Error      -> show error state with retry
 *
 * The repository never emits Loading - that is a UI/ViewModel concern. The
 * repository returns Success / Empty / Error only.
 */
sealed class ApiResult<out T> {
    data object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data object Empty : ApiResult<Nothing>()
    data class Error(val message: String) : ApiResult<Nothing>()
}
