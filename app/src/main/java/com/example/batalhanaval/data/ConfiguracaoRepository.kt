package com.example.batalhanaval.data

import kotlinx.coroutines.flow.Flow

class ConfiguracaoRepository(
    private val firebaseService: FirebaseService = FirebaseService()
) {
    fun observarConfiguracoes(): Flow<List<ConfiguracaoJogo>> {
        return firebaseService.observarConfiguracoes()
    }

    suspend fun salvar(config: ConfiguracaoJogo): Result<Unit> {
        return try {
            firebaseService.salvarConfiguracao(config)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun excluir(id: String): Result<Unit> {
        return try {
            firebaseService.excluirConfiguracao(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}