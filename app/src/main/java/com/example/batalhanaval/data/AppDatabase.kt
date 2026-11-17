package com.example.batalhanaval.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.batalhanaval.data.dao.ConfiguracaoDao
import com.example.batalhanaval.data.dao.PartidaDao
import com.example.batalhanaval.data.model.Configuracao

@Database(
    entities = [Partida::class, Configuracao::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun partidaDao(): PartidaDao
    abstract fun configuracaoDao(): ConfiguracaoDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obter(context: Context): AppDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "batalha_naval.db"
                ).build().also { INSTANCIA = it }
            }
    }
}
