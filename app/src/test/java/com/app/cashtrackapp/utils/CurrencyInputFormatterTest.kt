package com.app.cashtrackapp.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyInputFormatterTest {
    @Test
    fun formatInput_formatsRawValueAsBrl() {
        assertEquals("R$ 1,00", CurrencyInputFormatter.formatInput("1"))
        assertEquals("R$ 25,00", CurrencyInputFormatter.formatInput("25"))
        assertEquals("R$ 1.234,00", CurrencyInputFormatter.formatInput("1234"))
    }

    @Test
    fun formatInput_keepsEmptyValueEmpty() {
        assertEquals("", CurrencyInputFormatter.formatInput(""))
    }

    @Test
    fun formatValue_formatsDoubleAsBrl() {
        assertEquals("R$ 1.234,56", CurrencyInputFormatter.formatValue(1234.56))
    }

    @Test
    fun sanitizeInput_keepsOnlyOneDecimalSeparatorAndTwoDecimals() {
        assertEquals("25,50", CurrencyInputFormatter.sanitizeInput("R$ 25,509"))
        assertEquals("0,25", CurrencyInputFormatter.sanitizeInput(",25"))
    }

    @Test
    fun rawValueFromValue_returnsEditableBrazilianDecimal() {
        assertEquals("25,50", CurrencyInputFormatter.rawValueFromValue(25.5))
    }
}
