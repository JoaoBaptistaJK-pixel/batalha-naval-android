package com.example.batalhanaval.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Perfil { NENHUM, USUARIO, ADMINISTRADOR }

data class SessaoUiState(
    val perfil: Perfil = Perfil.NENHUM,
    val nomeJogador: String = "Jogador"
)

class SessaoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessaoUiState())
    val uiState: StateFlow<SessaoUiState> = _uiState.asStateFlow()

    fun entrarComoUsuario() {
        _uiState.value = _uiState.value.copy(perfil = Perfil.USUARIO)
    }

    fun entrarComoAdministrador() {
        _uiState.value = _uiState.value.copy(perfil = Perfil.ADMINISTRADOR)
    }

    fun alterarNome(nome: String) {
        _uiState.value = _uiState.value.copy(nomeJogador = nome)
    }
}