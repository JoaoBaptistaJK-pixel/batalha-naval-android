package com.example.batalhanaval.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jogador1: String,
    val jogador2: String,
    val vencedor: String,
    val dataHoraMillis: Long = System.currentTimeMillis(),
    val duracaoSegundos: Int,
    val jogadasJogador1: Int,
    val jogadasJogador2: Int,
    val tamanhoTabuleiro: Int = 10,
    val modoJogo: String
)
