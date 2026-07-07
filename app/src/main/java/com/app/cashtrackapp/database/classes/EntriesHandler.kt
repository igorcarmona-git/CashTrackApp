package com.app.cashtrackapp.database.classes

import com.app.cashtrackapp.database.DatabaseHandler
import com.app.cashtrackapp.database.DatabaseSubscription
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.entity.EntryTypes

interface EntriesRepository {
    fun inserirLancamento(entry: EntryDataType, onComplete: (EntryDataType?, Exception?) -> Unit)
    fun atualizarLancamento(entry: EntryDataType, onComplete: (EntryDataType?, Exception?) -> Unit)
    fun observarLancamentos(onChange: (List<EntryDataType>, Exception?) -> Unit): DatabaseSubscription
}

object EntriesHandler : EntriesRepository {
    private const val ENTRIES_PATH = "entries"

    override fun inserirLancamento(
        entry: EntryDataType,
        onComplete: (EntryDataType?, Exception?) -> Unit
    ) {
        DatabaseHandler.insert(
            path = ENTRIES_PATH,
            buildData = { id -> prepareEntryForSave(entry, id) }
        ) { savedEntry, error ->
            if (savedEntry != null) {
                onComplete(savedEntry, null)
            } else {
                onComplete(null, error ?: Exception("Erro ao salvar lançamento"))
            }
        }
    }

    override fun atualizarLancamento(
        entry: EntryDataType,
        onComplete: (EntryDataType?, Exception?) -> Unit
    ) {
        if (entry.id.isBlank()) {
            onComplete(null, IllegalArgumentException("ID do lançamento não informado"))
            return
        }

        val entryToUpdate = prepareEntryForSave(entry, entry.id)

        DatabaseHandler.update(
            path = entryPath(entryToUpdate.id),
            updates = entryToUpdate.toUpdateMap()
        ) { success, error ->
            if (success) {
                onComplete(entryToUpdate, null)
            } else {
                onComplete(null, error ?: Exception("Erro ao atualizar lançamento"))
            }
        }
    }

    override fun observarLancamentos(
        onChange: (List<EntryDataType>, Exception?) -> Unit
    ): DatabaseSubscription {
        return DatabaseHandler.observeList(
            path = ENTRIES_PATH,
            clazz = EntryDataType::class.java,
            mapper = ::prepareEntryFromDatabase
        ) { entries, error ->
            onChange(entries.sortedByDescending { it.opDate }, error)
        }
    }

    private fun prepareEntryForSave(entry: EntryDataType, id: String): EntryDataType {
        return entry.copy(
            id = id,
            op = EntryTypes.normalize(entry.op),
            opDescription = entry.opDescription.trim()
        )
    }

    private fun prepareEntryFromDatabase(entry: EntryDataType, firebaseId: String): EntryDataType {
        val normalizedType = EntryTypes.normalize(entry.op).ifBlank { entry.op }

        return entry.copy(
            id = entry.id.ifBlank { firebaseId },
            op = normalizedType
        )
    }

    private fun EntryDataType.toUpdateMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "op" to op,
            "opDate" to opDate,
            "opDescription" to opDescription,
            "opValue" to opValue
        )
    }

    private fun entryPath(id: String): String = "$ENTRIES_PATH/$id"
}
