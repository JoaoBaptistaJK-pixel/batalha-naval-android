package com.example.batalhanaval.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.batalhanaval.data.Partida
import com.example.batalhanaval.data.PartidaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoricoUiState(
    val partidas: List<Partida> = emptyList(),
    val carregando: Boolean = true,
    val total: Int = 0,
    val vitorias: Int = 0,
    val derrotas: Int = 0
)

class HistoricoViewModel(
    private val repository: PartidaRepository
) : ViewModel() {

    val uiState: StateFlow<HistoricoUiState> = repository.partidas
        .map { lista ->
            val vitorias = lista.count { it.venceu }
            HistoricoUiState(
                partidas = lista,
                carregando = false,
                total = lista.size,
                vitorias = vitorias,
                derrotas = lista.size - vitorias
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoricoUiState()
        )

    fun excluir(partida: Partida) {
        viewModelScope.launch { repository.excluirPartida(partida) }
    }

    fun limparHistorico() {
        viewModelScope.launch { repository.limparHistorico() }
    }

    companion object {
        fun factory(repository: PartidaRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { HistoricoViewModel(repository) }
            }
    }
}
