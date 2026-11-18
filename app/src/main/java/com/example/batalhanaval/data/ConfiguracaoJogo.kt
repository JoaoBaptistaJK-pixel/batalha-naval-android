package com.example.batalhanaval.data

data class ConfiguracaoJogo(
    val id: String = "",
    val nome: String = "",
    val tamanhoTabuleiro: Int = 6,
    val tamanhosNavios: List<Int> = listOf(3, 2, 2),
    val tentativasMaximas: Int = 20
)