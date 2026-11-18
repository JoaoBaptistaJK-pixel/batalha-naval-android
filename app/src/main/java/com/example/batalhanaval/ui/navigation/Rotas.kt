package com.example.batalhanaval.ui.navigation

object Rotas {
    const val INICIO = "inicio"
    const val USUARIO = "usuario"
    const val ADMIN = "admin"

    const val JOGO = "jogo"
    const val HISTORICO = "historico"
    const val CONFIGURACOES = "configuracoes"
    const val DETALHES = "detalhes/{id}"

    fun detalhes(id: Long): String = "detalhes/$id"
}