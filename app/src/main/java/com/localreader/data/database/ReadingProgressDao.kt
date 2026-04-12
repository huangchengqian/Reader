package com.localreader.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.localreader.data.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    fun getProgressByBookId(bookId: Long): Flow<ReadingProgress?>

    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun getProgressByBookIdSync(bookId: Long): ReadingProgress?

    @Query("SELECT * FROM reading_progress ORDER BY lastReadAt DESC")
    fun getAllProgress(): Flow<List<ReadingProgress>>

    @Query("SELECT SUM(totalReadTimeMs) FROM reading_progress")
    fun getTotalReadTimeMs(): Flow<Long?>

    @Query("SELECT * FROM reading_progress ORDER BY totalReadTimeMs DESC")
    fun getProgressByReadTime(): Flow<List<ReadingProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ReadingProgress): Long

    @Update
    suspend fun updateProgress(progress: ReadingProgress)

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteProgressByBookId(bookId: Long)
}
