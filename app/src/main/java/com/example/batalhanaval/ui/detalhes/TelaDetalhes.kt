package com.example.batalhanaval.ui.detalhes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.batalhanaval.ui.historico.HistoricoViewModel

@Composable
fun TelaDetalhes(
    viewModel: HistoricoViewModel,
    id: Long,
    onVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    val partida = estado.partidas.find { it.id == id }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalhes da partida")
        if (partida == null) {
            Text("Partida não encontrada.")
        } else {
            Text(if (partida.venceu) "Vitória" else "Derrota")
            Text("Jogador: ${partida.jogador}")
            Text("Pontuação: ${partida.pontuacao}")
            Text("Tentativas: ${partida.tentativas}")
            Text("Acertos: ${partida.acertos}")
        }
        Button(onClick = onVoltar) { Text("Voltar") }
    }
}