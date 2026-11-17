package com.example.batalhanaval.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jogador: String,
    val tentativas: Int,
    val acertos: Int,
    val pontuacao: Int,
    val venceu: Boolean,
    val dataHoraMillis: Long = System.currentTimeMillis()
)
