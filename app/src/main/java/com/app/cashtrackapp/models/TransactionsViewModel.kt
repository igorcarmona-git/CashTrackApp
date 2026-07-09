package com.app.cashtrackapp.models

import androidx.lifecycle.ViewModel
import com.app.cashtrackapp.database.DatabaseSubscription
import com.app.cashtrackapp.database.classes.EntriesHandler
import com.app.cashtrackapp.database.classes.EntriesRepository
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.entity.EntryTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class TransactionsState {
    object Loading : TransactionsState()
    data class Content(
        val entries: List<EntryDataType>,
        val summary: TransactionsSummary
    ) : TransactionsState()

    data class Error(val message: String) : TransactionsState()
}

data class TransactionsSummary(
    val totalCredit: Double = 0.0,
    val totalDebit: Double = 0.0
) {
    val balance: Double
        get() = totalCredit - totalDebit
}

object EntriesSummaryCalculator {
    fun calculate(entries: List<EntryDataType>): TransactionsSummary {
        val totalCredit = entries
            .filter { EntryTypes.isCredit(it.op) }
            .sumOf { it.opValue }

        val totalDebit = entries
            .filter { EntryTypes.isDebit(it.op) }
            .sumOf { it.opValue }

        return TransactionsSummary(
            totalCredit = totalCredit,
            totalDebit = totalDebit
        )
    }
}

class TransactionsViewModel(
    private val entriesRepository: EntriesRepository = EntriesHandler
) : ViewModel() {
    private var subscription: DatabaseSubscription? = null

    private val _uiState = MutableStateFlow<TransactionsState>(TransactionsState.Loading)
    val uiState: StateFlow<TransactionsState> = _uiState

    init {
        observeEntries()
    }

    fun refresh() {
        observeEntries()
    }

    fun updateEntry(entry: EntryDataType, onComplete: (Boolean, String?) -> Unit) {
        validateEntry(entry)?.let { validationMessage ->
            onComplete(false, validationMessage)
            return
        }

        entriesRepository.atualizarLancamento(entry) { updatedEntry, error ->
            if (updatedEntry != null && error == null) {
                onComplete(true, null)
            } else {
                onComplete(false, error?.localizedMessage ?: "Erro ao atualizar lançamento")
            }
        }
    }

    private fun observeEntries() {
        subscription?.cancel()
        _uiState.value = TransactionsState.Loading

        subscription = entriesRepository.observarLancamentos { entries, error ->
            if (error != null) {
                _uiState.value =
                    TransactionsState.Error(
                        error.localizedMessage ?: "Erro ao carregar lançamentos"
                    )
            } else {
                val sortedEntries = entries.sortedByDescending { it.opDate }

                _uiState.value = TransactionsState.Content(
                    entries = sortedEntries,
                    summary = EntriesSummaryCalculator.calculate(sortedEntries)
                )
            }
        }
    }

    private fun validateEntry(entry: EntryDataType): String? {
        return when {
            entry.id.isBlank() -> "ID do lançamento não informado"
            EntryTypes.normalize(entry.op).isBlank() -> "Tipo de lançamento inválido"
            entry.opValue <= 0.0 -> "Valor deve ser maior que 0"
            entry.opDescription.isBlank() -> "Descrição não pode ser vazia"
            entry.opDate <= 0L -> "Data de lançamento inválida"
            else -> null
        }
    }

    override fun onCleared() {
        subscription?.cancel()
        subscription = null
    }
}
