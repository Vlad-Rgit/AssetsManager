package com.kazanneft.assetsmanager.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Checks if charSequence corresponds ISO_LOCAL_DATE format
 */
fun tryParseDate(date: CharSequence): LocalDate? {
    return try {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/M/d"))
    }
    catch (ex: DateTimeParseException){
        null
    }
}