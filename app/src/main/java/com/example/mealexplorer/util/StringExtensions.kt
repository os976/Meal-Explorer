package com.example.mealexplorer.util


fun String?.orFallback(fallback: String): String =
    if (this.isNullOrBlank()) fallback else this


fun String?.cleanText(): String {
    if (this.isNullOrBlank()) return ""
    return this
        .replace("\r\n", "\n")
        .replace(Regex("\n{3,}"), "\n\n")
        .trim()
}


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
