package com.example.batalhanaval.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.batalhanaval.data.ConfiguracaoJogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdmin(
    viewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory()),
    onVoltar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var nome by remember(state.configEmEdicao) {
        mutableStateOf(state.configEmEdicao?.nome ?: "")
    }
    var tamanhoTabuleiro by remember(state.configEmEdicao) {
        mutableStateOf((state.configEmEdicao?.tamanhoTabuleiro ?: 6).toString())
    }
    var tamanhosNavios by remember(state.configEmEdicao) {
        mutableStateOf(state.configEmEdicao?.tamanhosNavios?.joinToString(", ") ?: "3, 2, 2")
    }
    var tentativasMaximas by remember(state.configEmEdicao) {
        mutableStateOf((state.configEmEdicao?.tentativasMaximas ?: 20).toString())
    }

    var configParaExcluir by remember { mutableStateOf<ConfiguracaoJogo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administração - Configurações") },
                navigationIcon = {
                    TextButton(onClick = onVoltar) { Text("Voltar") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (state.configEmEdicao == null) "Nova Configuração" else "Editando: ${state.configEmEdicao?.nome}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do modo (ex: Difícil)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tamanhoTabuleiro,
                    onValueChange = { tamanhoTabuleiro = it },
                    label = { Text("Tabuleiro (NxN)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = tentativasMaximas,
                    onValueChange = { tentativasMaximas = it },
                    label = { Text("Tentativas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = tamanhosNavios,
                onValueChange = { tamanhosNavios = it },
                label = { Text("Tamanhos dos navios (separados por vírgula)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.salvarConfiguracao(nome, tamanhoTabuleiro, tamanhosNavios, tentativasMaximas)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.carregando
                ) {
                    Text(if (state.configEmEdicao == null) "Cadastrar" else "Salvar Alterações")
                }

                if (state.configEmEdicao != null) {
                    OutlinedButton(
                        onClick = { viewModel.selecionarParaEdicao(null) }
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            state.mensagemErro?.let { erro ->
                Text(text = erro, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            state.mensagemSucesso?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Configurações Sincronizadas (Nuvem)", fontWeight = FontWeight.Bold)
                if (state.carregando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.configuracoes, key = { it.id }) { config ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(config.nome, fontWeight = FontWeight.Bold)
                                Text(
                                    "Tabuleiro: ${config.tamanhoTabuleiro}x${config.tamanhoTabuleiro} | Tentativas: ${config.tentativasMaximas}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Navios: ${config.tamanhosNavios.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row {
                                TextButton(onClick = { viewModel.selecionarParaEdicao(config) }) {
                                    Text("Editar")
                                }
                                TextButton(onClick = { configParaExcluir = config }) {
                                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    configParaExcluir?.let { alvo ->
        AlertDialog(
            onDismissRequest = { configParaExcluir = null },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja realmente excluir a configuração \"${alvo.nome}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluirConfiguracao(alvo.id)
                    configParaExcluir = null
                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { configParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}