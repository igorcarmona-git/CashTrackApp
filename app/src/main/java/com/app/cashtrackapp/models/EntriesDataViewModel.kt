package com.app.cashtrackapp.models

import androidx.lifecycle.ViewModel
import com.app.cashtrackapp.database.classes.EntriesHandler
import com.app.cashtrackapp.database.classes.EntriesRepository
import com.app.cashtrackapp.entity.EntryDataType
import com.app.cashtrackapp.entity.EntryTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

class EntriesDataViewModel(
    private val entriesRepository: EntriesRepository = EntriesHandler
) : ViewModel() {

    // 1. O "_" é uma convenção (Backing Property) para uma variável privada e mutável.
    // Só este ViewModel pode alterar o valor de _submitState. Isso garante que a
    // lógica de mudança de estado esteja centralizada aqui, evitando ‘bugs’.
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)

    // 2. Esta é a versão pública e imutável (apenas leitura).
    // A UI (Compose) observa este StateFlow, mas não consegue alterá-lo diretamente.
    // Isso protege o estado do seu ‘app’ contra modificações indevidas vindas de fora.
    val submitState: StateFlow<SubmitState> = _submitState

    fun submitEntry(type: String, value: Double, description: String, dateMillis: Long) {
        if (_submitState.value is SubmitState.Submitting) {
            return
        }

        val normalizedType = EntryTypes.normalize(type)

        if (normalizedType.isBlank()) {
            _submitState.value = SubmitState.Error("Tipo de lançamento inválido")
            return
        }

        if (value <= 0.0) {
            _submitState.value = SubmitState.Error("Valor deve ser maior que 0")
            return
        }

        if (description.isBlank()) {
            _submitState.value = SubmitState.Error("Descrição não pode ser vazia")
            return
        }

        if (dateMillis <= 0L) {
            _submitState.value = SubmitState.Error("Data de lançamento inválida")
            return
        }

        // Enviando, estado de loading
        _submitState.value = SubmitState.Submitting

        // Cria o objeto que será salvo no Firebase
        val entry = EntryDataType(
            op = normalizedType,
            opDate = dateMillis,
            opDescription = description.trim(),
            opValue = value
        )

        entriesRepository.inserirLancamento(entry) { savedEntry, error ->
            if (savedEntry != null && error == null) {
                _submitState.value = SubmitState.Success
            } else {
                _submitState.value =
                    SubmitState.Error(error?.localizedMessage ?: "Erro ao salvar")
            }
        }
    }

    // Volta o estado para ‘IDLE’ (parado) para poder fazer nova tentativa
    fun resetState() {
        _submitState.value = SubmitState.Idle
    }
}
