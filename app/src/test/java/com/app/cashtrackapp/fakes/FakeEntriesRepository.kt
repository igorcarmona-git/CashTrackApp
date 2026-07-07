package com.app.cashtrackapp.fakes

import com.app.cashtrackapp.database.DatabaseSubscription
import com.app.cashtrackapp.database.classes.EntriesRepository
import com.app.cashtrackapp.entity.EntryDataType

class FakeEntriesRepository : EntriesRepository {
    private val entries = mutableListOf<EntryDataType>()
    private val observers = mutableSetOf<(List<EntryDataType>, Exception?) -> Unit>()

    var nextInsertError: Exception? = null
    var nextUpdateError: Exception? = null

    override fun inserirLancamento(
        entry: EntryDataType,
        onComplete: (EntryDataType?, Exception?) -> Unit
    ) {
        val error = nextInsertError
        if (error != null) {
            nextInsertError = null
            onComplete(null, error)
            return
        }

        val savedEntry = entry.copy(
            id = entry.id.ifBlank { "entry-${entries.size + 1}" }
        )

        entries += savedEntry
        notifyObservers()
        onComplete(savedEntry, null)
    }

    override fun atualizarLancamento(
        entry: EntryDataType,
        onComplete: (EntryDataType?, Exception?) -> Unit
    ) {
        val error = nextUpdateError
        if (error != null) {
            nextUpdateError = null
            onComplete(null, error)
            return
        }

        val entryIndex = entries.indexOfFirst { it.id == entry.id }
        if (entryIndex == -1) {
            onComplete(null, IllegalArgumentException("Lançamento não encontrado"))
            return
        }

        entries[entryIndex] = entry
        notifyObservers()
        onComplete(entry, null)
    }

    override fun observarLancamentos(
        onChange: (List<EntryDataType>, Exception?) -> Unit
    ): DatabaseSubscription {
        observers += onChange
        onChange(snapshot(), null)

        return DatabaseSubscription {
            observers -= onChange
        }
    }

    fun savedEntries(): List<EntryDataType> = entries.toList()

    fun emitError(error: Exception) {
        observers.toList().forEach { observer ->
            observer(emptyList(), error)
        }
    }

    private fun notifyObservers() {
        val currentSnapshot = snapshot()
        observers.toList().forEach { observer ->
            observer(currentSnapshot, null)
        }
    }

    private fun snapshot(): List<EntryDataType> {
        return entries.sortedByDescending { it.opDate }
    }
}
