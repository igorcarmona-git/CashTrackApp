package com.app.cashtrackapp.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun interface DatabaseSubscription {
    fun cancel()
}

object DatabaseHandler {
    private val database
        get() = FirebaseDatabase.getInstance()

    private fun reference(path: String) = database.getReference(path)

    fun <T> insert(
        path: String,
        buildData: (id: String) -> T,
        onComplete: (T?, Exception?) -> Unit
    ) {
        val newReference = reference(path).push()
        val newId = newReference.key

        if (newId.isNullOrBlank()) {
            onComplete(null, IllegalStateException("Não foi possível gerar o ID do registro"))
            return
        }

        val data = buildData(newId)

        newReference.setValue(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(data, null)
            } else {
                onComplete(null, task.exception)
            }
        }
    }

    fun update(path: String, updates: Map<String, Any>, onComplete: (Boolean, Exception?) -> Unit) {
        reference(path).updateChildren(updates).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        }
    }

    fun <T> observeList(
        path: String,
        clazz: Class<T>,
        mapper: (T, String) -> T = { item, _ -> item },
        onChange: (List<T>, Exception?) -> Unit
    ): DatabaseSubscription {
        val databaseReference = reference(path)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    child.getValue(clazz)?.let { item ->
                        mapper(item, child.key.orEmpty())
                    }
                }

                onChange(items, null)
            }

            override fun onCancelled(error: DatabaseError) {
                onChange(emptyList(), error.toException())
            }
        }

        databaseReference.addValueEventListener(listener)

        return DatabaseSubscription {
            databaseReference.removeEventListener(listener)
        }
    }
}
