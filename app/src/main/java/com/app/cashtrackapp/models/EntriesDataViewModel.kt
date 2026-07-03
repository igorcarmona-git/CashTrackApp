package com.app.cashtrackapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cashtrackapp.database.classes.EntriesHandler
import com.app.cashtrackapp.entity.EntryDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * SubmitState representa todos os ESTADOS POSSÍVEIS ao tentar salvar um lançamento.
 *
 * É como um "semáforo" que avisa à tela o que está a acontecer:
 * - ‘Idle’ (Parado) = esperando o usuário fazer algo
 * - Submitting (Enviando) = dados estão a ser salvos no Firebase
 * - Success (sucesso) = dados foram salvos! Mostrar mensagem de sucesso
 * - Error (Erro) = algo deu errado. Mostrar mensagem de erro
 */
sealed class SubmitState {
    object Idle : SubmitState()
    object Submitting : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

class EntriesDataViewModel : ViewModel() {
    // StateFlow = variável "reativa" que monitora mudanças e avisa a UI
    // quando o estado muda, a tela atualiza automaticamente
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    fun submitEntry(type: String, value: Double, description: String, dateMillis: Long) {
        if (value <= 0.0) {
            _submitState.value = SubmitState.Error("Valor deve ser maior que 0")
            return
        }

        if (description.isBlank()) {
            _submitState.value = SubmitState.Error("Descrição não pode ser vazia")
            return
        }

        // ===== ENVIANDO PARA O FIREBASE =====
        // Avisa à tela: "estou a enviar, por favor mostre um loading"
        _submitState.value = SubmitState.Submitting

        // Cria o objeto que será salvo no Firebase
        val entry = EntryDataType(
            op = type,
            opDate = dateMillis,
            opDescription = description,
            opValue = value
        )

        EntriesHandler.inserirLancamento(entry) { success, error ->
            viewModelScope.launch {
                if (success) {
                    _submitState.value = SubmitState.Success
                } else {
                    _submitState.value =
                        SubmitState.Error(error?.localizedMessage ?: "Erro ao salvar")
                }
            }
        }
    }

    // Volta o estado para ‘IDLE’ (parado) para poder fazer nova tentativa
    fun resetState() {
        _submitState.value = SubmitState.Idle
    }
}