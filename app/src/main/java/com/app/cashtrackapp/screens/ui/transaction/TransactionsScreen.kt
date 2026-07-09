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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.models.TransactionsState
import com.app.cashtrackapp.models.TransactionsSummary
import com.app.cashtrackapp.models.TransactionsViewModel
import com.app.cashtrackapp.screens.ui.transaction.components.EditTransactionDialog
import com.app.cashtrackapp.screens.ui.transaction.components.TransactionItem
import com.app.cashtrackapp.utils.CurrencyInputFormatter
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val summary = (uiState as? TransactionsState.Content)?.summary ?: TransactionsSummary()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var entryBeingEdited by remember { mutableStateOf<EntryDataType?>(null) }
    var isSavingEdit by remember { mutableStateOf(false) }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(16.dp)) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1F),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Crédito")
                    Text(text = formatMoney(summary.totalCredit))
                }
                Box(
                    modifier = Modifier
                      .padding(horizontal = 16.dp)
                      .width(1.dp)
                      .height(50.dp)
                      .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Column(
                    modifier = Modifier
                        .weight(1F),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Débito")
                    Text(text = formatMoney(summary.totalDebit))
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
            when (val state = uiState) {
                is TransactionsState.Loading -> {
                    item {
                        Text(
                            text = "Carregando lançamentos...",
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                is TransactionsState.Error -> {
                    item {
                        Column(modifier = Modifier.padding(vertical = 16.dp)) {
                            Text(text = state.message)
                            Button(
                                onClick = viewModel::refresh,
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }

                is TransactionsState.Content -> {
                    if (state.entries.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum lançamento encontrado",
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(state.entries) { entry ->
                            TransactionItem(
                                entry = entry,
                                onEditClick = {
                                    entryBeingEdited = it
                                    editErrorMessage = null
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp)
        ) {
            Text("Voltar")
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    entryBeingEdited?.let { selectedEntry ->
        EditTransactionDialog(
            entry = selectedEntry,
            isSaving = isSavingEdit,
            saveErrorMessage = editErrorMessage,
            onDismiss = {
                entryBeingEdited = null
                editErrorMessage = null
            },
            onSave = { updatedEntry ->
                isSavingEdit = true
                editErrorMessage = null

                viewModel.updateEntry(updatedEntry) { success, errorMessage ->
                    isSavingEdit = false

                    if (success) {
                        entryBeingEdited = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Lançamento atualizado com sucesso")
                        }
                    } else {
                        editErrorMessage = errorMessage ?: "Erro ao atualizar lançamento"
                    }
                }
            }
        )
    }
}

private fun formatMoney(value: Double): String {
    return CurrencyInputFormatter.formatValue(value)
}
