package com.example.batalhanaval.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.batalhanaval.ui.admin.AdminViewModel
import com.example.batalhanaval.ui.admin.TelaAdmin
import com.example.batalhanaval.ui.configuracoes.TelaConfiguracoes
import com.example.batalhanaval.ui.detalhes.TelaDetalhes
import com.example.batalhanaval.ui.historico.HistoricoScreen
import com.example.batalhanaval.ui.historico.HistoricoViewModel
import com.example.batalhanaval.ui.inicio.TelaInicio
import com.example.batalhanaval.ui.jogo.JogoViewModel
import com.example.batalhanaval.ui.jogo.TelaJogo
import com.example.batalhanaval.ui.viewmodel.AppViewModelFactory
import com.example.batalhanaval.ui.viewmodel.SessaoViewModel

@Composable
fun AppNavHost(factory: AppViewModelFactory) {
    val navController = rememberNavController()
    val sessaoViewModel: SessaoViewModel = viewModel()
    val sessao by sessaoViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = Rotas.INICIO) {

        composable(Rotas.INICIO) {
            TelaInicio(
                onUsuario = {
                    sessaoViewModel.entrarComoUsuario()
                    navController.navigate(Rotas.USUARIO)
                },
                onAdministrador = {
                    sessaoViewModel.entrarComoAdministrador()
                    navController.navigate(Rotas.ADMIN)
                }
            )
        }

        composable(Rotas.USUARIO) {
            TelaUsuario(factory = factory, sessaoViewModel = sessaoViewModel)
        }

        composable(Rotas.ADMIN) {
            if (sessao.ehAdmin) {
                TelaAdmin(
                    onVoltar = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun TelaUsuario(factory: AppViewModelFactory, sessaoViewModel: SessaoViewModel) {
    val navInterno = rememberNavController()
    val entradaAtual by navInterno.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val jogoViewModel: JogoViewModel = viewModel(factory = factory)
    val historicoViewModel: HistoricoViewModel = viewModel(factory = factory)
    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory())
    val sessao by sessaoViewModel.uiState.collectAsState()

    LaunchedEffect(sessao.nomeJogador) {
        jogoViewModel.iniciarJogo(sessao.nomeJogador)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = rotaAtual == Rotas.JOGO,
                    onClick = { navInterno.navigate(Rotas.JOGO) },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Jogo") },
                    label = { Text("Jogo") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.HISTORICO,
                    onClick = { navInterno.navigate(Rotas.HISTORICO) },
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Histórico") },
                    label = { Text("Histórico") }
                )
                NavigationBarItem(
                    selected = rotaAtual == Rotas.CONFIGURACOES,
                    onClick = { navInterno.navigate(Rotas.CONFIGURACOES) },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Config.") },
                    label = { Text("Config.") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navInterno,
            startDestination = Rotas.JOGO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.JOGO) {
                TelaJogo(
                    viewModel = jogoViewModel,
                    nomeJogador = sessao.nomeJogador,
                    onVoltar = { navInterno.navigate(Rotas.HISTORICO) }
                )
            }
            composable(Rotas.HISTORICO) {
                HistoricoScreen(
                    viewModel = historicoViewModel,
                    onVoltar = { navInterno.navigate(Rotas.JOGO) }
                )
            }
            composable(Rotas.CONFIGURACOES) {
                TelaConfiguracoes(
                    sessaoViewModel = sessaoViewModel,
                    adminViewModel = adminViewModel
                )
            }
            composable(
                route = Rotas.DETALHES,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                val id = entrada.arguments?.getLong("id") ?: 0L
                TelaDetalhes(
                    viewModel = historicoViewModel,
                    id = id,
                    onVoltar = { navInterno.popBackStack() }
                )
            }
        }
    }
}