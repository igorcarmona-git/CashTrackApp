package com.app.cashtrackapp.database

import com.google.firebase.database.FirebaseDatabase

object DatabaseHandler {
    private val database = FirebaseDatabase.getInstance()

    // Funções genéricas para CRUD operations no banco
    fun <T> insert(path: String, data: T, onComplete: (Boolean, Exception?) -> Unit) {
        database.getReference(path).push().setValue(data).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        }
    }

    fun update(path: String, updates: Map<String, Any>, onComplete: (Boolean, Exception?) -> Unit) {
        database.getReference(path).updateChildren(updates).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        }
    }

    fun delete(path: String, onComplete: (Boolean, Exception?) -> Unit) {
        database.getReference(path).removeValue().addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        }
    }

    fun <T> read(path: String, clazz: Class<T>, onComplete: (T?, Exception?) -> Unit) {
        database.getReference(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(task.result?.getValue(clazz), null)
            } else {
                onComplete(null, task.exception)
            }
        }
    }
}