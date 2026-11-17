package com.example.batalhanaval.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.batalhanaval.data.model.Partida
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    @Insert
    suspend fun inserir(partida: Partida): Long

    @Delete
    suspend fun excluir(partida: Partida)

    @Query("DELETE FROM partidas")
    suspend fun excluirTodas()

    @Query("SELECT * FROM partidas ORDER BY dataHoraMillis DESC")
    fun observarTodas(): Flow<List<Partida>>

    @Query("SELECT * FROM partidas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Partida?
}
