package com.app.cashtrackapp.screens.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.cashtrackapp.models.Transaction
import com.app.cashtrackapp.screens.ui.transaction.components.TransactionItem

@Composable
fun TransactionsScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
  val transactions = listOf(
    Transaction("Salário", 4500.00, "Crédito"),
    Transaction("Freelance", 850.00, "Crédito"),
    Transaction("Venda de Notebook", 2200.00, "Crédito"),
    Transaction("Dividendos", 180.50, "Crédito"),

    Transaction("Aluguel", 1200.00, "Débito"),
    Transaction("Supermercado", 385.70, "Débito"),
    Transaction("Conta de Luz", 145.90, "Débito"),
    Transaction("Internet", 99.90, "Débito"),
    Transaction("Gasolina", 250.00, "Débito"),
    Transaction("Academia", 89.90, "Débito"),
    Transaction("Cinema", 72.00, "Débito"),
    Transaction("Restaurante", 138.50, "Débito"),
    Transaction("Spotify", 21.90, "Débito"),
    Transaction("Farmácia", 64.35, "Débito")
  )

  val totalCredit = transactions.filter { it.type == "Crédito" }.sumOf { it.value }

  val totalDebit = transactions.filter { it.type == "Débito" }.sumOf { it.value }

  Column(modifier = modifier.padding(16.dp)) {

    Card(
      modifier = modifier
        .fillMaxWidth()
    ) {
      Row(
        modifier = modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Column(
          modifier = modifier
            .weight(1F),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Credito")
          Text(text = "R$ %.2f".format(totalCredit))
        }
        Box(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(1.dp)
            .height(50.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Column(
          modifier = modifier
            .weight(1F),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Debito")
          Text(text = "R$ %.2f".format(totalDebit))
        }
      }
    }

    Text(
      text = "Lançamentos",
      modifier = Modifier.padding(top = 24.dp)
    )

    LazyColumn(
      modifier = Modifier
        .weight(1f)
    ) {
      items(transactions) { transaction ->
        TransactionItem(transaction)
      }
    }

    Button(
      onClick = onBackClick,
      modifier = modifier.fillMaxWidth()
        .padding(top = 16.dp)
      ) {
      Text("Voltar")
    }
  }
}
