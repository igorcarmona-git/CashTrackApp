package com.app.cashtrackapp.screens.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.cashtrackapp.models.EntriesDataViewModel
import com.app.cashtrackapp.models.SubmitState
import com.app.cashtrackapp.screens.ui.components.DateField
import com.app.cashtrackapp.screens.ui.components.DescriptionField
import com.app.cashtrackapp.screens.ui.components.TypeSelector
import com.app.cashtrackapp.screens.ui.components.ValueField
import java.util.Calendar

@Composable
fun EntriesScreen(modifier: Modifier = Modifier, viewModel: EntriesDataViewModel = viewModel()) {
    // Monitora mudanças no estado do ViewModel
    // Sempre que submitState mudar, a tela atualiza automaticamente
    val submitState by viewModel.submitState.collectAsState()

    var valueText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(Calendar.getInstance().timeInMillis) }
    var type by remember { mutableStateOf("Crédito") }

    val snackbarHostState = remember { SnackbarHostState() }

    // REAGE ÀS MUDANÇAS DE ESTADO
    // Sempre que submitState muda, este bloco é executado
    LaunchedEffect(submitState) {
        when (submitState) {
            is SubmitState.Success -> {
                snackbarHostState.showSnackbar("Lançamento salvo com sucesso")
                viewModel.resetState()

                // Limpa os campos para nova entrada
                valueText = ""
                description = ""
                dateMillis = Calendar.getInstance().timeInMillis
                type = "Crédito"
            }

            // Estado: Erro ao tentar salvar
            is SubmitState.Error -> {
                val msg = (submitState as SubmitState.Error).message
                snackbarHostState.showSnackbar(msg)
                viewModel.resetState()
            }

            // Estados ‘Idle’ e Submitting: não precisa fazer nada especial aqui
            else -> {}
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        // Tipo
        TypeSelector(
            selectedType = type,
            onTypeSelected = { type = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Valor
        ValueField(
            value = valueText,
            onValueChange = { valueText = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Descrição
        DescriptionField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Data
        DateField(
            dateMillis = dateMillis,
            onDateSelected = { dateMillis = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val value = valueText.replace(',', '.').toDoubleOrNull() ?: -1.0

                    viewModel.submitEntry(type, value, description, dateMillis)
                    //A tela automaticamente reage às mudanças de estado
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Lançar")
            }

            Button(
                onClick = {
                    // Será implementado depois
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ver Tudo")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SnackbarHost(hostState = snackbarHostState)
    }
}



