package com.example.cle_bot.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE userId = :userId OR userId IS NULL ORDER BY timestamp ASC")
    suspend fun getMessagesForUser(userId: Int?): List<ChatMessageLocalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageLocalEntity)

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun clearHistory(userId: Int?)
}
