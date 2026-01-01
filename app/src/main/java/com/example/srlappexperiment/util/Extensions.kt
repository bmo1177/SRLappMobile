package com.example.srlappexperiment.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension function to format Date to String
 */
fun Date.toDateString(pattern: String = Constants.DATE_FORMAT_PATTERN): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

/**
 * Extension function to parse String to Date
 */
fun String.toDate(pattern: String = Constants.DATE_FORMAT_PATTERN): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

/**
 * Extension function to get days difference between dates
 */
fun Date.daysUntil(other: Date): Long {
    val diff = other.time - this.time
    return diff / (1000 * 60 * 60 * 24)
}

