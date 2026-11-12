package com.example.batalhanaval.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.batalhanaval.data.Partida
import com.example.batalhanaval.data.PartidaRepository
import kotlin.random.Random


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

    private fun gerarNavios(tamanhoTabuleiro: Int, tamanhosNavios: List<Int>): List<Navio>{
        val ocupadas = mutableSetOf<Posicao>()
        val navios = mutableSetOf<Navio>()

        tamanhosNavios.forEach { tamanho -> 
            require(tamanho <= tamanhoTabuleiro) { "Navio maior que o tabuleiro" }
            var posicionado = false

            while (!posicionado){
                val horizontal = Random.nextBoolean()
                
                val maxLinha = if (horizontal) tamanhoTabuleiro else tamanhoTabuleiro - tamanho + 1
                val maxColuna = if (horizontal) tamanhoTabuleiro - tamanho + 1 else tamanhoTabuleiro

                val linha = Random.nextInt(maxLinha)
                val coluna = Random.nextInt(maxColuna)
                
                val posicoes = (0 until tamanho).map { i -> 
                    if (horizontal) Posicao(linha, coluna + i) else Posicao(linha + i, coluna)
                }

                if (posicoes.none { it in ocupadas }){
                    ocupadas.addAll(posicoes)
                    navios.add(Navio(posicoes))
                    posicionado = true
                }
            }
        }
        return navios.toList()
    }
}