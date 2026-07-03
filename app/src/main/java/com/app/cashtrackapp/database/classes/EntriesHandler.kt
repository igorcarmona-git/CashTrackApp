package com.app.cashtrackapp.database.classes

import com.app.cashtrackapp.database.DatabaseHandler
import com.app.cashtrackapp.entity.EntryDataType

object EntriesHandler {
    private const val ENTRIES_PATH = "entries"

    fun inserirLancamento(entry: EntryDataType, onComplete: (Boolean, Exception?) -> Unit) {
        DatabaseHandler.insert(ENTRIES_PATH, entry) { success, error ->
            onComplete(success, error)
        }
    }

    fun atualizarLancamento(
        id: String,
        updates: Map<String, Any>,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        DatabaseHandler.update("$ENTRIES_PATH/$id", updates) { success, error ->
            onComplete(success, error)
        }
    }

    fun deletarLancamento(id: String, onComplete: (Boolean, Exception?) -> Unit) {
        DatabaseHandler.delete("$ENTRIES_PATH/$id") { success, error ->
            onComplete(success, error)
        }
    }

    fun obterLancamento(id: String, onComplete: (EntryDataType?, Exception?) -> Unit) {
        DatabaseHandler.read("$ENTRIES_PATH/$id", EntryDataType::class.java) { entry, error ->
            onComplete(entry, error)
        }
    }
}



