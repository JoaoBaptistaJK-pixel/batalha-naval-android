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

    fun atirar(posicao: Posicao){
        val atual = _uiState.value
        if (atual.status != StatusJogos.JOGANDO) return

        if(posicao in atual.disparos){
            _uiState.value = atual.copy(mensagem = "Você já atirou nessa posição!")
            return
        }

        val disparos = atual.disparos + posicao
        val acertou = identificarAcerto(atual.navios, posicao)
        val afundou = acertou && atual.navios.first {posicao in it.posicoes}.estaAfundado(disparos)

        val restantes = atual.tentativasMaximas - disparos.size
        val status = when {
            contarAfundados(atual.navios, disparos) == atual.totalNavios -> StatusJogos.VITORIA
            restantes <= 0 -> StatusJogos.DERROTA
            else -> StatusJogos.JOGANDO
        }

        val mensagem = when {
            status == StatusJogos.VITORIA -> "Parabéns! Você afundou todos os navios!"
            status == StatusJogos.DERROTA -> "Fim das tentativas! Você Perdeu, Game Over."
            afundou -> "Navio afundado!"
            acertou -> "Acertou um navio!"
            else -> "Água!"
        }
        
        val novoEstado = atualizarApresentacao(atual.copy(disparos = disparos, status = status, mensagem = mensagem))
        _uiState.value = novoEstado

        if(status != StatusJogos.JOGANDO) finalizarPartida(novoEstado)
    }

    private fun identificarAcerto(navios: List<Navio>, posicao: Posicao): Boolean = navios.any { navio -> posicao in navio.posicoes}

    private fun contarAfundados(navios: List<Navio>, disparos: List<Posicao>): Int = navios.count { it.estaAfundado(disparos) }

    private fun finalizarPartida(estado: JogoUiState){
        viewModelScope.launch{
            try{
                repository.salvarPartida(
                    Partida(
                        jogador = estado.nomeJogador,
                        tentativas = estado.disparos.size,
                        acertos = estado.acertos,
                        pontuacao = estado.pontuacao,
                        venceu = estado.status == StatusJogos.VITORIA
                    )
                )
            } catch (e: Exception){
                _uiState.update { it.copy(mensagem = "Erro ao salvar a partida: ${e.message}") }
            }
        }
    }

    private fun atualizarApresentacao(state: JogoUiState){
        val acertos = state.disparos.filter { identificarAcerto(state.navios, it) }.size
        val afundados = contarAfundados(state.navios, state.disparos)
        val restantes = state.tentativasMaximas - state.disparos.size

        val pontuacao = acertos * PONTOS_ACERTO + afundados * PONTOS_NAVIO_AFUNDADO + (if (state.status == StatusJogos.state.VITORIA ) restantes * PONTOS_TENTATIVA_RESTANTE else 0)

        val tabuleiro = (0 until state.tamanhoTabuleiro).flatMap {linha -> (0 until state.tamanhoTabuleiro).map { coluna -> Posicao(linha, coluna)}}
        .map { posicao -> CelulaUi(posicao, calcularEstadoCelula(state, posicao))}

        val historico = state.disparos.mapIndexed {indice, posicao -> JogadaUi (
            numero = indice + 1,
            descricao = "Disparo em ${'A' + posicao.coluna}${posicao.linha + 1}",
            acertou = identificarAcerto(state.navios, posicao)
        )}.sortedBy { -it.numero }

        return state.copy(
            tentativasRestantes = restantes,
            acertos = acertos,
            naviosAfundados = afundados,
            pontuacao = pontuacao, 
            tabuleiro = tabuleiro,
            historico = historico
        )
    }

    private fun calcularEstadoCelula(state: JogoUiState, posicao: Posicao): EstadoCelula{
        val temNavio = identificarAcerto(state.navios, posicao)
        return when {
            posicao in state.disparos && temNavio -> EstadoCelula.ACERTO
            posicao in state.disparos -> EstadoCelula.AGUA
            temNavio && state.status == StatusJogos.DERROTA -> EstadoCelula.NAVIO_REVELADO else -> EstadoCelula.OCULTA
        }
    }

    private companion object {
        const val PONTOS_ACERTO = 10
        const val PONTOS_NAVIO_AFUNDADO = 50
        const val PONTOS_TENTATIVA_RESTANTE = 5
    }
}