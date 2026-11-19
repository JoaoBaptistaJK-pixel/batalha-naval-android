package com.example.batalhanaval.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Perfil { NENHUM, USUARIO, ADMINISTRADOR }

data class SessaoUiState(
    val perfil: Perfil = Perfil.NENHUM
)

class SessaoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessaoUiState())
    val uiState: StateFlow<SessaoUiState> = _uiState.asStateFlow()

    fun entrarComoUsuario() {
        _uiState.value = SessaoUiState(perfil = Perfil.USUARIO)
    }

    fun entrarComoAdministrador() {
        _uiState.value = SessaoUiState(perfil = Perfil.ADMINISTRADOR)
    }

    fun sair() {
        _uiState.value = SessaoUiState(perfil = Perfil.NENHUM)
    }
}