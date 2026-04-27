package com.example.mealexplorer.data.remote

sealed class ApiResult<out T> {
    data object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data object Empty : ApiResult<Nothing>()
    data class Error(val message: String) : ApiResult<Nothing>()
}
