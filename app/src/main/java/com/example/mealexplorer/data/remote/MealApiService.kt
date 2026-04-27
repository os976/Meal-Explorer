package com.example.mealexplorer.data.remote

import com.example.mealexplorer.data.remote.dto.CategoryListResponse
import com.example.mealexplorer.data.remote.dto.MealListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Retrofit interface mirroring the four endpoints listed in the project plan.
 *
 *  - search.php?s=Arrabiata     -> [searchMealsByName]
 *  - search.php?f=a             -> [searchMealsByFirstLetter]
 *  - lookup.php?i=52772         -> [lookupMealById]
 *  - categories.php             -> [getCategories]
 *  - filter.php?c=Seafood       -> [filterByCategory]
 */
interface MealApiService {

    @GET("search.php")
    suspend fun searchMealsByName(@Query("s") query: String): MealListResponse

    @GET("search.php")
    suspend fun searchMealsByFirstLetter(@Query("f") letter: String): MealListResponse

    @GET("lookup.php")
    suspend fun lookupMealById(@Query("i") mealId: String): MealListResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryListResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealListResponse

    companion object {
        /**
         * Lazily built singleton instance. We keep this here to avoid pulling in
         * a DI library for a small training project - one repository, one API,
         * lifetime equal to the process is fine.
         */
        val instance: MealApiService by lazy { build() }

        private fun build(): MealApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealApiService::class.java)
        }
    }
}
