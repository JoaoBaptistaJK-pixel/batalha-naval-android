package com.example.batalhanaval.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.batalhanaval.data.model.Configuracao
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(configuracao: Configuracao)

    @Query("SELECT * FROM configuracoes WHERE id = 1")
    fun observar(): Flow<Configuracao?>

    @Query("SELECT * FROM configuracoes WHERE id = 1")
    suspend fun obter(): Configuracao?
}
