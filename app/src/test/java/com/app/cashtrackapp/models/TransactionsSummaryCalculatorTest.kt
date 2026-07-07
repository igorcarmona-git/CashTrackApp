package com.app.cashtrackapp.models

import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.entity.EntryTypes
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionsSummaryCalculatorTest {
    @Test
    fun calculate_sumsCreditDebitAndBalance() {
        val summary = EntriesSummaryCalculator.calculate(
            listOf(
                EntryDataType(op = EntryTypes.CREDIT, opValue = 100.0),
                EntryDataType(op = EntryTypes.CREDIT, opValue = 50.0),
                EntryDataType(op = EntryTypes.DEBIT, opValue = 40.0)
            )
        )

        assertEquals(150.0, summary.totalCredit, 0.001)
        assertEquals(40.0, summary.totalDebit, 0.001)
        assertEquals(110.0, summary.balance, 0.001)
    }

    @Test
    fun calculate_treatsLegacyExpenseAsDebit() {
        val summary = EntriesSummaryCalculator.calculate(
            listOf(EntryDataType(op = "Despesa", opValue = 25.0))
        )

        assertEquals(0.0, summary.totalCredit, 0.001)
        assertEquals(25.0, summary.totalDebit, 0.001)
    }
}
