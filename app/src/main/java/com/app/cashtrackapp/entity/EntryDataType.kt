package com.app.cashtrackapp.entity

data class EntryDataType(
    val id: String = "",
    val op: String = "",
    val opDate: Long = 0L, // epoch millis
    val opDescription: String = "",
    val opValue: Double = 0.0
)

object EntryTypes {
    const val CREDIT = "Crédito"
    const val DEBIT = "Débito"
    private const val LEGACY_EXPENSE = "Despesa"

    val available = listOf(CREDIT, DEBIT)

    fun normalize(type: String): String {
        return when (type.trim()) {
            CREDIT -> CREDIT
            DEBIT, LEGACY_EXPENSE -> DEBIT
            else -> ""
        }
    }

    fun isCredit(type: String): Boolean = normalize(type) == CREDIT

    fun isDebit(type: String): Boolean = normalize(type) == DEBIT
}
