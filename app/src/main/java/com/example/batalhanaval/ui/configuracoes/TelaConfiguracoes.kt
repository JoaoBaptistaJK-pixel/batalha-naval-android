package com.example.batalhanaval.ui.configuracoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.batalhanaval.ui.admin.AdminViewModel
import com.example.batalhanaval.ui.viewmodel.SessaoViewModel

@Composable
fun TelaConfiguracoes(
    sessaoViewModel: SessaoViewModel,
    adminViewModel: AdminViewModel
) {
    val sessao by sessaoViewModel.uiState.collectAsState()
    val admin by adminViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Configurações", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = sessao.nomeJogador,
            onValueChange = { sessaoViewModel.alterarNome(it) },
            label = { Text("Nome do jogador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Modos de jogo disponíveis", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(admin.configuracoes, key = { it.id }) { config ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(config.nome, style = MaterialTheme.typography.titleSmall)
                        Text("Tabuleiro ${config.tamanhoTabuleiro}x${config.tamanhoTabuleiro} • ${config.tentativasMaximas} tentativas")
                    }
                }
            }
        }
    }
}