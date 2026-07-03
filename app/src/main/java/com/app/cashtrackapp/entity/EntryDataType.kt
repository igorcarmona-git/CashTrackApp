package com.app.cashtrackapp.entity

// Usar tipos primitivos e valores ‘default’ facilita a serializer com o Firebase
data class EntryDataType(
    val op: String = "",
    val opDate: Long = 0L, // epoch millis
    val opDescription: String = "",
    val opValue: Double = 0.0
)
