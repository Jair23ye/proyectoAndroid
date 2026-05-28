package com.example.cle_bot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TramiteEntity::class,
        RequisitoEntity::class,
        PasoEntity::class,
        ProgresoEntity::class,
        UserEntity::class,
        ChatMessageLocalEntity::class,
        CalificacionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CleBotDatabase : RoomDatabase() {
    abstract fun tramiteDao(): TramiteDao
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun kardexDao(): KardexDao

    companion object {
        @Volatile
        private var INSTANCE: CleBotDatabase? = null

        fun getDatabase(context: Context): CleBotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CleBotDatabase::class.java,
                    "clebot_database"
                )
                .fallbackToDestructiveMigration() // Útil durante el desarrollo
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
