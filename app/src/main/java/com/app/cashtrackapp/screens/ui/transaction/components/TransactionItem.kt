package com.app.cashtrackapp.screens.ui.transaction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.entity.EntryTypes
import com.app.cashtrackapp.utils.CurrencyInputFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
  entry: EntryDataType,
  onEditClick: (EntryDataType) -> Unit,
  modifier: Modifier = Modifier
) {
  val dateText = remember(entry.opDate) {
    if (entry.opDate > 0L) {
      SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(entry.opDate))
    } else {
      ""
    }
  }
  val valueColor = if (EntryTypes.isDebit(entry.op)) {
    MaterialTheme.colorScheme.error
  } else {
    MaterialTheme.colorScheme.primary
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .weight(1F),
      ) {
        Text(text = entry.op.ifBlank { "Sem tipo" })
        Text(text = entry.opDescription.ifBlank { "Sem descrição" })
        if (dateText.isNotBlank()) {
          Text(text = dateText)
        }
      }
      Column(
        modifier = Modifier
          .weight(1F),
        horizontalAlignment = Alignment.End
      ) {
        Text(
          text = CurrencyInputFormatter.formatValue(entry.opValue),
          color = valueColor
        )
        TextButton(onClick = { onEditClick(entry) }) {
          Text("Editar")
        }
      }
    }
  }
}
