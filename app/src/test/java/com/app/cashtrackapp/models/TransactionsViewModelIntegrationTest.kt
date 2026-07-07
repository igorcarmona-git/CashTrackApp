package com.app.cashtrackapp.models

import com.app.cashtrackapp.entity.EntryTypes
import com.app.cashtrackapp.fakes.FakeEntriesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionsViewModelIntegrationTest {
    @Test
    fun savingEntriesThroughFormUpdatesTransactionsListAndSummary() {
        val repository = FakeEntriesRepository()
        val transactionsViewModel = TransactionsViewModel(repository)
        val entriesViewModel = EntriesDataViewModel(repository)

        entriesViewModel.submitEntry(EntryTypes.CREDIT, 100.0, "Salário", 1_000L)
        entriesViewModel.submitEntry(EntryTypes.DEBIT, 35.50, "Mercado", 2_000L)

        val state = transactionsViewModel.uiState.value
        assertTrue(state is TransactionsState.Content)

        val content = state as TransactionsState.Content
        assertEquals(2, content.entries.size)
        assertEquals("Mercado", content.entries.first().opDescription)
        assertEquals(100.0, content.summary.totalCredit, 0.001)
        assertEquals(35.50, content.summary.totalDebit, 0.001)
        assertEquals(64.50, content.summary.balance, 0.001)
    }

    @Test
    fun repositoryErrorMovesTransactionsListToErrorState() {
        val repository = FakeEntriesRepository()
        val transactionsViewModel = TransactionsViewModel(repository)

        repository.emitError(IllegalStateException("Falha ao ler banco"))

        val state = transactionsViewModel.uiState.value
        assertTrue(state is TransactionsState.Error)
        assertEquals("Falha ao ler banco", (state as TransactionsState.Error).message)
    }

    @Test
    fun updateEntryUpdatesTransactionsListAndSummary() {
        val repository = FakeEntriesRepository()
        val transactionsViewModel = TransactionsViewModel(repository)
        val entriesViewModel = EntriesDataViewModel(repository)

        entriesViewModel.submitEntry(EntryTypes.DEBIT, 50.0, "Mercado", 1_000L)
        val originalEntry = repository.savedEntries().first()

        var updateSuccess = false
        var updateError: String? = null

        transactionsViewModel.updateEntry(
            originalEntry.copy(
                op = EntryTypes.CREDIT,
                opValue = 80.0,
                opDescription = "Reembolso",
                opDate = 2_000L
            )
        ) { success, errorMessage ->
            updateSuccess = success
            updateError = errorMessage
        }

        val state = transactionsViewModel.uiState.value as TransactionsState.Content
        assertTrue(updateSuccess)
        assertEquals(null, updateError)
        assertEquals("Reembolso", state.entries.first().opDescription)
        assertEquals(80.0, state.summary.totalCredit, 0.001)
        assertEquals(0.0, state.summary.totalDebit, 0.001)
    }

    @Test
    fun updateEntryWithRepositoryErrorReturnsErrorMessage() {
        val repository = FakeEntriesRepository()
        val transactionsViewModel = TransactionsViewModel(repository)
        val entriesViewModel = EntriesDataViewModel(repository)

        entriesViewModel.submitEntry(EntryTypes.DEBIT, 50.0, "Mercado", 1_000L)
        repository.nextUpdateError = IllegalStateException("Falha ao atualizar banco")

        var updateSuccess = true
        var updateError: String? = null

        transactionsViewModel.updateEntry(
            repository.savedEntries().first().copy(opValue = 80.0)
        ) { success, errorMessage ->
            updateSuccess = success
            updateError = errorMessage
        }

        assertTrue(!updateSuccess)
        assertEquals("Falha ao atualizar banco", updateError)
    }
}
