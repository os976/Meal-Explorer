package com.example.mealexplorer.data.remote

/**
 * Constants for TheMealDB API.
 *
 * The plan asks us to use the developer test key "1" - this key is part of the
 * public path of every endpoint, e.g. https://www.themealdb.com/api/json/v1/1/...
 */
object ApiConstants {
    const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    // Image hosts used by the API for thumbnails. Plain URLs come back from the
    // server already, so this is here only for reference.
    const val MEAL_IMAGE_HOST = "https://www.themealdb.com/images/media/meals/"
}
