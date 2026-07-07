package com.app.cashtrackapp.screens.ui.transaction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.screens.ui.components.DateField
import com.app.cashtrackapp.screens.ui.components.DescriptionField
import com.app.cashtrackapp.screens.ui.components.TypeSelector
import com.app.cashtrackapp.screens.ui.components.ValueField
import com.app.cashtrackapp.utils.CurrencyInputFormatter
import com.app.cashtrackapp.utils.EntryInputParser

@Composable
fun EditTransactionDialog(
    entry: EntryDataType,
    isSaving: Boolean,
    saveErrorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (EntryDataType) -> Unit
) {
    var type by remember(entry.id) { mutableStateOf(entry.op) }
    var valueText by remember(entry.id) {
        mutableStateOf(CurrencyInputFormatter.rawValueFromValue(entry.opValue))
    }
    var description by remember(entry.id) { mutableStateOf(entry.opDescription) }
    var dateMillis by remember(entry.id) { mutableLongStateOf(entry.opDate) }
    var validationMessage by remember(entry.id) { mutableStateOf<String?>(null) }

    val displayedError = validationMessage ?: saveErrorMessage

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = { Text("Editar lançamento") },
        text = {
            Column {
                TypeSelector(
                    selectedType = type,
                    onTypeSelected = {
                        type = it
                        validationMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ValueField(
                    value = valueText,
                    onValueChange = {
                        valueText = it
                        validationMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                DescriptionField(
                    value = description,
                    onValueChange = {
                        description = it
                        validationMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                DateField(
                    dateMillis = dateMillis,
                    onDateSelected = {
                        dateMillis = it
                        validationMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (!displayedError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = displayedError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                onClick = {
                    val value = EntryInputParser.parseCurrency(valueText)

                    if (value == null || value <= 0.0) {
                        validationMessage = "Valor deve ser maior que 0"
                        return@Button
                    }

                    if (description.isBlank()) {
                        validationMessage = "Descrição não pode ser vazia"
                        return@Button
                    }

                    onSave(
                        entry.copy(
                            op = type,
                            opValue = value,
                            opDescription = description,
                            opDate = dateMillis
                        )
                    )
                }
            ) {
                Text(if (isSaving) "Salvando..." else "Salvar")
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSaving,
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
