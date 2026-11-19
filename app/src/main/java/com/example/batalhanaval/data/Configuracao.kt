package com.example.batalhanaval.data

import androidx.annotation.Keep

// Limites das regras de negócio (usados em validar()).
private const val TABULEIRO_MINIMO = 4
private const val TABULEIRO_MAXIMO = 10
private const val PERCENTUAL_MAXIMO_OCUPADO = 40

/**
 * MODELO: uma configuração de partida que o administrador cadastra.
 *
 * Os campos espelham os parâmetros de JogoViewModel.iniciarJogo(), então o
 * jogo pode usar uma configuração diretamente:
 *   iniciarJogo(nome, cfg.tamanhoTabuleiro, cfg.tamanhosNavios, cfg.tentativasMaximas)
 *
 * IMPORTANTE para o Firestore: todos os campos têm valor padrão. Isso faz o Kotlin
 * gerar um construtor vazio, que o Firestore exige para transformar o documento
 * da nuvem de volta em objeto.
 *
 * @Keep evita que o R8 (build release) renomeie os campos e quebre o mapeamento.
 */
@Keep
data class Configuracao(
    val id: String = "",
    val nome: String = "",
    val tamanhoTabuleiro: Int = 6,
    val tamanhosNavios: List<Int> = listOf(3, 2, 2),
    val tentativasMaximas: Int = 20,
    val ativa: Boolean = false
) {
    /**
     * Valida as regras de negócio. Retorna a mensagem de erro, ou null se estiver tudo certo.
     *
     * Por que tantas regras? O JogoViewModel.gerarNavios() sorteia posições num laço
     * "while" e ficaria preso para sempre se os navios não coubessem no tabuleiro.
     * Validar aqui, antes de salvar, protege o jogo de configurações impossíveis.
     */
    fun validar(): String? {
        if (nome.isBlank()) return "Informe um nome para a configuração."
        if (tamanhoTabuleiro !in TABULEIRO_MINIMO..TABULEIRO_MAXIMO) {
            return "O tabuleiro deve ter entre $TABULEIRO_MINIMO e $TABULEIRO_MAXIMO casas por lado."
        }
        if (tamanhosNavios.isEmpty()) return "Informe pelo menos um navio."
        if (tamanhosNavios.any { it < 1 || it > tamanhoTabuleiro }) {
            return "Cada navio deve ter entre 1 e $tamanhoTabuleiro casas."
        }
        val casasDeNavios = tamanhosNavios.sum()
        val totalDeCasas = tamanhoTabuleiro * tamanhoTabuleiro
        if (casasDeNavios * 100 > totalDeCasas * PERCENTUAL_MAXIMO_OCUPADO) {
            return "Navios demais para esse tabuleiro (máximo de $PERCENTUAL_MAXIMO_OCUPADO% das casas)."
        }
        if (tentativasMaximas < casasDeNavios) {
            return "As tentativas devem ser pelo menos $casasDeNavios (total de casas dos navios), senão não dá para vencer."
        }
        if (tentativasMaximas > totalDeCasas) {
            return "As tentativas não podem passar de $totalDeCasas (total de casas do tabuleiro)."
        }
        return null
    }
}
