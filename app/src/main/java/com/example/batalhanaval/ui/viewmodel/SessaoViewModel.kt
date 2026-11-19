package com.example.batalhanaval.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessaoUiState(
    val ehAdmin: Boolean = false,
    val nomeJogador: String = "Jogador"
)

class SessaoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessaoUiState())
    val uiState: StateFlow<SessaoUiState> = _uiState.asStateFlow()

    fun entrarComoUsuario() {
        _uiState.value = _uiState.value.copy(ehAdmin = false)
    }

    fun entrarComoAdministrador() {
        _uiState.value = _uiState.value.copy(ehAdmin = true)
    }

    fun alterarNome(nome: String) {
        _uiState.value = _uiState.value.copy(nomeJogador = nome)
    }
}