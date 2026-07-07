package com.app.cashtrackapp.utils

object EntryInputParser {
    fun parseCurrency(rawValue: String): Double? {
        val compactValue = rawValue
            .trim()
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")

        if (compactValue.isBlank()) {
            return null
        }

        val normalizedValue = if (
            compactValue.contains(',') &&
            compactValue.lastIndexOf(',') > compactValue.lastIndexOf('.')
        ) {
            compactValue.replace(".", "").replace(',', '.')
        } else {
            compactValue.replace(",", "")
        }

        return normalizedValue.toDoubleOrNull()
    }
}
