package com.example.batalhanaval

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.batalhanaval.data.AppDatabase
import com.example.batalhanaval.data.PartidaRepository
import com.example.batalhanaval.ui.navigation.AppNavHost
import com.example.batalhanaval.ui.theme.BatalhaNavalTheme
import com.example.batalhanaval.ui.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val banco = AppDatabase.obter(applicationContext)
        val repository = PartidaRepository(banco.partidaDao(), banco.configuracaoDao())
        val factory = AppViewModelFactory(repository)

        setContent {
            BatalhaNavalTheme {
                AppNavHost(factory = factory)
            }
        }
    }
}