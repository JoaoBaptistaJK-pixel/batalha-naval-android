package com.example.batalhanaval.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.batalhanaval.data.ConfiguracaoJogo
import com.example.batalhanaval.data.ConfiguracaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: ConfiguracaoRepository = ConfiguracaoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState(carregando = true))
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        iniciarSincronizacao()
    }

    private fun iniciarSincronizacao() {
        viewModelScope.launch {
            repository.observarConfiguracoes()
                .catch { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = "Erro na sincronização em tempo real: ${erro.message}"
                        )
                    }
                }
                .collect { listaAtualizada ->
                    _uiState.update {
                        it.copy(
                            configuracoes = listaAtualizada,
                            carregando = false,
                            mensagemErro = null
                        )
                    }
                }
        }
    }

    fun salvarConfiguracao(
        nome: String,
        tamanhoTabuleiroTexto: String,
        naviosTexto: String,
        tentativasTexto: String
    ) {
        val tamanho = tamanhoTabuleiroTexto.toIntOrNull()
        val tentativas = tentativasTexto.toIntOrNull()
        val listaNavios = naviosTexto.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it > 0 }

        if (nome.isBlank()) {
            _uiState.update { it.copy(mensagemErro = "Informe o nome da configuração.") }
            return
        }
        if (tamanho == null || tamanho !in 4..12) {
            _uiState.update { it.copy(mensagemErro = "O tabuleiro deve ter tamanho entre 4 e 12.") }
            return
        }
        if (listaNavios.isEmpty() || listaNavios.any { it > tamanho }) {
            _uiState.update { it.copy(mensagemErro = "Navios inválidos ou maiores que o tabuleiro ($tamanho).") }
            return
        }
        if (tentativas == null || tentativas <= 0 || tentativas > tamanho * tamanho) {
            _uiState.update { it.copy(mensagemErro = "Tentativas devem ser entre 1 e ${tamanho * tamanho}.") }
            return
        }

        val idAtual = _uiState.value.configEmEdicao?.id ?: ""
        val configParaSalvar = ConfiguracaoJogo(
            id = idAtual,
            nome = nome.trim(),
            tamanhoTabuleiro = tamanho,
            tamanhosNavios = listaNavios,
            tentativasMaximas = tentativas
        )

        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagemErro = null, mensagemSucesso = null) }
            repository.salvar(configParaSalvar)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            configEmEdicao = null,
                            mensagemSucesso = if (idAtual.isEmpty()) "Configuração cadastrada!" else "Configuração atualizada!"
                        )
                    }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = "Falha ao salvar no Firebase: ${erro.message}"
                        )
                    }
                }
        }
    }

    fun selecionarParaEdicao(config: ConfiguracaoJogo?) {
        _uiState.update {
            it.copy(configEmEdicao = config, mensagemErro = null, mensagemSucesso = null)
        }
    }

    fun excluirConfiguracao(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, mensagemErro = null, mensagemSucesso = null) }
            repository.excluir(id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemSucesso = "Configuração excluída com sucesso!"
                        )
                    }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = "Erro ao excluir: ${erro.message}"
                        )
                    }
                }
        }
    }

    class Factory(
        private val repository: ConfiguracaoRepository = ConfiguracaoRepository()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}