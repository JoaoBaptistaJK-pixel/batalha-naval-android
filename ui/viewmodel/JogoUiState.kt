package com.example.batalhanaval.ui.jogo


data class Posicao(val linha: Int, val coluna: Int)

data class Navio(val posicoes: List<Posicao>){
    fun estaAfundado(disparos: List<Posicao>): Boolean = posicoes.all { it in disparos }
}

enum class EstadoCelula { OCULTA, AGUA, ACERTO, NAVIO_REVELADO}

enum class StatusJogos { JOGANDO, VITORIA, DERROTA }

data class CelulaUi(val posicao: Posicao, val estado: EstadoCelula)

data class JogadaUi(val numero: Int, val descricao: String, val acertou: Boolean)

data class JogoUiState(
    val nomeJogador: String = "",
    val tamanhoTabuleiro: Int = 6,
    val navios: List<Navio> = emptyList(),
    val disparos: List<Posicao> = emptyList(),
    val tentativasMaximas: Int = 20,
    val tentativasRestantes: Int = 20,
    val acertos: Int = 0,
    val naviosAfundados: Int = 0,
    val totalNavios: Int = 0,
    val pontuacao: Int = 0,
    val status: StatusJogos = StatusJogos.JOGANDO,
    val tabuleiro: List<CelulaUi> = emptyList(),
    val historico: List<JogadaUi> = emptyList(),
    val mensagem: String = ""
)