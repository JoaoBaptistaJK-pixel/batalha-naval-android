package com.example.batalhanaval.ui.jogo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun TelaJogo(
    viewModel: JogoViewModel,
    nomeJogador: String,
    onVoltar: () -> Unit
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(nomeJogador){
        viewModel.iniciarSeNecessario(nomeJogador)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text(
            text = "Batalha Naval",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        PainelStatus(uiState)

        Text(text = uiState.mensagem, style = MaterialTheme.typography.bodyLarge)

        Tabuleiro(uiState = uiState, onCelulaClick = viewModel::atirar)

        if (uiState.status != StatusJogos.JOGANDO) {
            FimDeJogo(
                venceu = uiState.status == StatusJogos.VITORIA,
                pontuacao = uiState.pontuacao,
                onJogarNovamente = { viewModel.iniciarJogo(uiState.nomeJogador) },
                onVoltar = onVoltar
            )
        }

        Text(text = "Jogadas", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(uiState.historico, key = { it.numero }) { jogada -> JogadaItem(jogada) }
        }
    }
}


@Composable
private fun PainelStatus(uiState: JogoUiState){
    Card(modifier = Modifier.fillMaxWidth()){
        Row( modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Indicador("Tentativas", uiState.tentativasRestantes.toString())
            Indicador("Acertos", uiState.acertos.toString())
            Indicador("Navios", "${uiState.naviosAfundados}/${uiState.totalNavios}")
            Indicador("Pontos", uiState.pontuacao.toString())
        }
    }
}


@Composable
private fun Indicador(titulo: String, valor: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = titulo, style = MaterialTheme.typography.labelMedium)
        Text(text = valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}


@Composable
private fun Tabuleiro(
    uiState: JogoUiState,
    onCelulaClick: (Posicao) -> Unit
){
    val jogando = uiState.status == StatusJogos.JOGANDO

    LazyVerticalGrid(columns = GridCells.Fixed(uiState.tamanhoTabuleiro),
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ){
        items(items = uiState.tabuleiro, key = { "${it.posicao.linha}-${it.posicao.coluna}" }) { celula ->
            CelulaTabuleiro(celula = celula, habilitada = jogando && celula.estado == EstadoCelula.OCULTA,
                onClick = { onCelulaClick(celula.posicao) }
            )
        }
    }
}


@Composable
private fun CelulaTabuleiro(
    celula: CelulaUi,
    habilitada: Boolean,
    onClick: () -> Unit
) {
    val cor = when (celula.estado) {
        EstadoCelula.OCULTA -> Color(0xFF1E88E5)
        EstadoCelula.AGUA -> Color(0xFFB3E5FC)
        EstadoCelula.ACERTO -> Color(0xFFE53935)
        EstadoCelula.NAVIO_REVELADO -> Color(0xFF616161)
    }
    val simbolo = when (celula.estado) {
        EstadoCelula.AGUA -> "•"
        EstadoCelula.ACERTO -> "X"
        else -> ""
    }

    Box(
        modifier = Modifier.aspectRatio(1f).background(cor, RoundedCornerShape(4.dp)).clickable(enabled = habilitada, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = simbolo, color = Color.White, fontWeight = FontWeight.Bold)
    }
}


@Composable
private fun FimDeJogo(
    venceu: Boolean,
    pontuacao: Int,
    onJogarNovamente: () -> Unit,
    onVoltar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (venceu) "Vitória!" else "Derrota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Pontuação final: $pontuacao")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onJogarNovamente) { Text("Jogar novamente") }
                OutlinedButton(onClick = onVoltar) { Text("Voltar") }
            }
        }
    }
}


@Composable
private fun JogadaItem(jogada: JogadaUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "#${jogada.numero} - ${jogada.descricao}")
        Text(
            text = if (jogada.acertou) "Acerto" else "Água",
            color = if (jogada.acertou) Color(0xFFE53935) else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}
