package com.localreader.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.localreader.data.model.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {
    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY startedAt DESC")
    fun getSessionsByBookId(bookId: Long): Flow<List<ReadingSession>>

    @Query("SELECT * FROM reading_sessions WHERE endedAt IS NULL")
    suspend fun getActiveSession(): ReadingSession?

    @Query("SELECT SUM(durationMs) FROM reading_sessions WHERE bookId = :bookId AND endedAt IS NOT NULL")
    fun getTotalTimeByBookId(bookId: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReadingSession): Long

    @Update
    suspend fun updateSession(session: ReadingSession)

    @Query("UPDATE reading_sessions SET endedAt = :endedAt, durationMs = :durationMs WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endedAt: Long, durationMs: Long)
}
