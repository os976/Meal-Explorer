package com.example.mealexplorer.util

/**
 * Small string helpers used across the project.
 */

/**
 * Returns [this] if non-null and non-blank, otherwise [fallback].
 */
fun String?.orFallback(fallback: String): String =
    if (this.isNullOrBlank()) fallback else this

/**
 * Cleans up whitespace artifacts coming back from TheMealDB instructions:
 * collapses runs of \r\n\r\n into double newlines and trims edges.
 */
fun String?.cleanText(): String {
    if (this.isNullOrBlank()) return ""
    return this
        .replace("\r\n", "\n")
        .replace(Regex("\n{3,}"), "\n\n")
        .trim()
}

/**
 * Joins an ingredient name and measure into one display row, e.g.
 * "1 cup &middot; flour".
 */
fun joinMeasureAndName(name: String, measure: String): String {
    val n = name.trim()
    val m = measure.trim()
    return when {
        n.isEmpty() && m.isEmpty() -> ""
        m.isEmpty() -> n
        n.isEmpty() -> m
        else -> "$m  ·  $n"
    }
}
