package com.example.batalhanaval.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val colecao = firestore.collection("configuracoes_jogo")

    suspend fun salvarConfiguracao(config: ConfiguracaoJogo) {
        val docRef = if (config.id.isBlank()) {
            colecao.document()
        } else {
            colecao.document(config.id)
        }
        val configComId = config.copy(id = docRef.id)
        docRef.set(configComId).await()
    }

    suspend fun excluirConfiguracao(id: String) {
        colecao.document(id).delete().await()
    }

    fun observarConfiguracoes(): Flow<List<ConfiguracaoJogo>> = callbackFlow {
        val listener = colecao.addSnapshotListener { snapshot, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            val lista = snapshot?.toObjects(ConfiguracaoJogo::class.java) ?: emptyList()
            trySend(lista)
        }
        awaitClose { listener.remove() }
    }
}