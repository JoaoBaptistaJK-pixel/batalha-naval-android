package com.example.batalhanaval.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.batalhanaval.data.PartidaRepository
import com.example.batalhanaval.ui.historico.HistoricoViewModel
import com.example.batalhanaval.ui.jogo.JogoViewModel

class AppViewModelFactory(
    private val repository: PartidaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            JogoViewModel::class.java -> JogoViewModel(repository) as T
            HistoricoViewModel::class.java -> HistoricoViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel desconhecido")
        }
    }
}