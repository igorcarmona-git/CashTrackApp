package com.app.cashtrackapp.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntryInputParserTest {
    @Test
    fun parseCurrency_acceptsBrazilianDecimalComma() {
        assertEquals(10.50, EntryInputParser.parseCurrency("10,50") ?: 0.0, 0.001)
    }

    @Test
    fun parseCurrency_acceptsBrazilianThousandSeparator() {
        assertEquals(1234.56, EntryInputParser.parseCurrency("R$ 1.234,56") ?: 0.0, 0.001)
    }

    @Test
    fun parseCurrency_acceptsDecimalDot() {
        assertEquals(99.90, EntryInputParser.parseCurrency("99.90") ?: 0.0, 0.001)
    }

    @Test
    fun parseCurrency_returnsNullForInvalidValue() {
        assertNull(EntryInputParser.parseCurrency("abc"))
    }
}
