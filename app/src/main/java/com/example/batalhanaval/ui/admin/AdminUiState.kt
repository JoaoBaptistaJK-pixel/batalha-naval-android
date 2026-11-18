package com.example.batalhanaval.ui.admin

import com.example.batalhanaval.data.ConfiguracaoJogo

data class AdminUiState(
    val configuracoes: List<ConfiguracaoJogo> = emptyList(),
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val mensagemSucesso: String? = null,
    val configEmEdicao: ConfiguracaoJogo? = null
)