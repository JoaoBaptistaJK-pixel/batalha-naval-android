package com.example.batalhanaval.ui.historico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.batalhanaval.data.model.Partida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel,
    onVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (estado.partidas.isNotEmpty()) {
                        IconButton(onClick = { mostrarConfirmacao = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Limpar histórico")
                        }
                    }
                }
            )
        }
    ) { paddingInterno ->
        Box(modifier = Modifier.padding(paddingInterno).fillMaxSize()) {
            when {
                estado.carregando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                estado.partidas.isEmpty() -> Text(
                    text = "Nenhuma partida jogada ainda.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )

                else -> HistoricoConteudo(
                    estado = estado,
                    onExcluir = viewModel::excluir
                )
            }
        }
    }

    if (mostrarConfirmacao) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacao = false },
            title = { Text("Limpar histórico?") },
            text = { Text("Todas as partidas salvas serão apagadas. Isso não pode ser desfeito.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.limparHistorico()
                    mostrarConfirmacao = false
                }) { Text("Apagar tudo") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun HistoricoConteudo(
    estado: HistoricoUiState,
    onExcluir: (Partida) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${estado.total} partidas • ${estado.vitorias} vitórias • ${estado.derrotas} derrotas",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(items = estado.partidas, key = { it.id }) { partida ->
            PartidaItem(partida = partida, onExcluir = { onExcluir(partida) })
        }
    }
}

@Composable
private fun PartidaItem(
    partida: Partida,
    onExcluir: () -> Unit
) {
    val dataFormatada = remember(partida.dataHoraMillis) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            .format(Date(partida.dataHoraMillis))
    }
    val venceu = partida.vencedor == partida.jogador1

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (venceu) "Vitória" else "Derrota",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (venceu) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                )
                Text("${partida.jogador1} x ${partida.jogador2}")
                Text("Vencedor: ${partida.vencedor}")
                Text(
                    "$dataFormatada • ${formatarDuracao(partida.duracaoSegundos)} • " +
                        "${partida.jogadasJogador1} tiros",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onExcluir) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir partida")
            }
        }
    }
}

private fun formatarDuracao(segundos: Int): String =
    "%02d:%02d".format(segundos / 60, segundos % 60)
