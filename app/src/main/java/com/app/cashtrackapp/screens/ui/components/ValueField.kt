package com.app.cashtrackapp.screens.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardType
import com.app.cashtrackapp.utils.CurrencyInputFormatter
import com.app.cashtrackapp.utils.CurrencyVisualTransformation

@Composable
fun ValueField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedValue = CurrencyInputFormatter.formatInput(value)

    OutlinedTextField(
        value = value,
        onValueChange = { rawValue ->
            onValueChange(CurrencyInputFormatter.sanitizeInput(rawValue))
        },
        label = { Text("Valor (R$)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        visualTransformation = CurrencyVisualTransformation(),
        modifier = modifier.semantics {
            stateDescription = formattedValue
        }
    )
}
