package com.example.batalhanaval.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow


class JogoViewModel(
    private val repository: PartidaRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    fun iniciarSeNecessario(nomeJogador: String){
        if (_uiState.value.navios.isEmpty()) iniciarJogo(nomeJogador)
    }

    fun iniciarJogo(
        nomeJogador: String,
        tamanhoTabuleiro: Int = 6,
        tamanhosNavios: List<Int> = listOf(3, 2, 2),
        tentativasMaximas: Int = 20
    ){
        val navios = gerarNavios(tamanhoTabuleiro, tamanhosNavios)

        val inicial = JogoUiState(
            nomeJogador = nomeJogador,
            tamanhoTabuleiro = tamanhoTabuleiro,
            navios = navios,
            tentativasMaximas = tentativasMaximas,
            tentativasRestantes = tentativasMaximas,
            totalNavios = navios.size,
            mensagem = "Jogo iniciado! Encontre todos os navios."
        )
    }
}