package com.example.batalhanaval.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuracoes")
data class Configuracao(
    @PrimaryKey
    val id: Int = ID_UNICO,
    val nomeJogador: String = "Jogador 1",
    val tamanhoTabuleiro: Int = 10,
    val dificuldade: String = "MEDIO",
    val somAtivado: Boolean = true,
    val vibracaoAtivada: Boolean = true
) {
    companion object {
        const val ID_UNICO = 1
    }
}
