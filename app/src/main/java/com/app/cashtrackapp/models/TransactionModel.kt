package com.app.cashtrackapp.models

data class Transaction(
  val description: String,
  val value: Double,
  val type: String
)