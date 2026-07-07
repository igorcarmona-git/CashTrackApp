package com.app.cashtrackapp.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyInputFormatter {
    private val brazilianSymbols = DecimalFormatSymbols(Locale.forLanguageTag("pt-BR"))

    fun formatInput(rawValue: String): String {
        val sanitizedValue = sanitizeInput(rawValue)

        if (sanitizedValue.isBlank()) {
            return ""
        }

        val amount = sanitizedValue
            .replace(',', '.')
            .toBigDecimalOrNull()
            ?: return ""

        return formatAmount(amount)
    }

    fun formatValue(value: Double): String {
        return formatAmount(BigDecimal.valueOf(value))
    }

    fun rawValueFromValue(value: Double): String {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString()
            .replace('.', ',')
    }

    fun sanitizeInput(rawValue: String): String {
        val valueWithoutCurrencySymbol = rawValue
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")

        val result = StringBuilder()
        var hasDecimalSeparator = false
        var decimalDigits = 0

        valueWithoutCurrencySymbol.forEach { char ->
            when {
                char.isDigit() && !hasDecimalSeparator -> result.append(char)
                char.isDigit() && decimalDigits < 2 -> {
                    result.append(char)
                    decimalDigits++
                }
                (char == ',' || char == '.') && !hasDecimalSeparator -> {
                    if (result.isEmpty()) {
                        result.append('0')
                    }
                    result.append(',')
                    hasDecimalSeparator = true
                }
            }
        }

        return result.toString()
    }

    private fun formatAmount(amount: BigDecimal): String {
        return DecimalFormat("R$ #,##0.00", brazilianSymbols).format(amount)
    }
}
