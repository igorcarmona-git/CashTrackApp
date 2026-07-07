package com.app.cashtrackapp.models

import com.app.cashtrackapp.entity.EntryTypes
import com.app.cashtrackapp.fakes.FakeEntriesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class EntriesDataViewModelTest {
    @Test
    fun submitEntry_withInvalidValue_emitsErrorAndDoesNotSave() {
        val repository = FakeEntriesRepository()
        val viewModel = EntriesDataViewModel(repository)

        viewModel.submitEntry(EntryTypes.CREDIT, 0.0, "Salário", 1L)

        val state = viewModel.submitState.value
        assertTrue(state is SubmitState.Error)
        assertEquals("Valor deve ser maior que 0", (state as SubmitState.Error).message)
        assertTrue(repository.savedEntries().isEmpty())
    }

    @Test
    fun submitEntry_withBlankDescription_emitsErrorAndDoesNotSave() {
        val repository = FakeEntriesRepository()
        val viewModel = EntriesDataViewModel(repository)

        viewModel.submitEntry(EntryTypes.CREDIT, 100.0, "   ", 1L)

        val state = viewModel.submitState.value
        assertTrue(state is SubmitState.Error)
        assertEquals("Descrição não pode ser vazia", (state as SubmitState.Error).message)
        assertTrue(repository.savedEntries().isEmpty())
    }

    @Test
    fun submitEntry_withLegacyExpenseType_savesNormalizedDebit() {
        val repository = FakeEntriesRepository()
        val viewModel = EntriesDataViewModel(repository)

        viewModel.submitEntry("Despesa", 30.0, "Mercado", 1L)

        assertSame(SubmitState.Success, viewModel.submitState.value)
        assertEquals(1, repository.savedEntries().size)
        assertEquals(EntryTypes.DEBIT, repository.savedEntries().first().op)
    }

    @Test
    fun submitEntry_whenRepositoryFails_emitsError() {
        val repository = FakeEntriesRepository().apply {
            nextInsertError = IllegalStateException("Firebase indisponível")
        }
        val viewModel = EntriesDataViewModel(repository)

        viewModel.submitEntry(EntryTypes.CREDIT, 100.0, "Salário", 1L)

        val state = viewModel.submitState.value
        assertTrue(state is SubmitState.Error)
        assertEquals("Firebase indisponível", (state as SubmitState.Error).message)
    }
}
