package com.example.cle_bot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TramiteEntity::class, RequisitoEntity::class, PasoEntity::class, ProgresoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CleBotDatabase : RoomDatabase() {
    abstract fun tramiteDao(): TramiteDao

    companion object {
        @Volatile
        private var INSTANCE: CleBotDatabase? = null

        fun getDatabase(context: Context): CleBotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CleBotDatabase::class.java,
                    "clebot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
