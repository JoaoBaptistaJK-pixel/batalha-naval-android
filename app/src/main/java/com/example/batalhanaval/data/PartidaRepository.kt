package com.example.batalhanaval.data

import com.example.batalhanaval.data.dao.ConfiguracaoDao
import com.example.batalhanaval.data.dao.PartidaDao
import com.example.batalhanaval.data.model.Configuracao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartidaRepository(
    private val partidaDao: PartidaDao,
    private val configuracaoDao: ConfiguracaoDao
) {

    val partidas: Flow<List<Partida>> = partidaDao.observarTodas()

    suspend fun salvarPartida(partida: Partida): Long = partidaDao.inserir(partida)

    suspend fun excluirPartida(partida: Partida) = partidaDao.excluir(partida)

    suspend fun limparHistorico() = partidaDao.excluirTodas()

    suspend fun buscarPartida(id: Long): Partida? = partidaDao.buscarPorId(id)

    val configuracao: Flow<Configuracao> =
        configuracaoDao.observar().map { it ?: Configuracao() }

    suspend fun obterConfiguracao(): Configuracao =
        configuracaoDao.obter() ?: Configuracao()

    suspend fun salvarConfiguracao(configuracao: Configuracao) =
        configuracaoDao.salvar(configuracao)
}
