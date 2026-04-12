package com.localreader.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId")]
)
data class ReadingProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val currentChapter: Int = 0,
    val currentPosition: Int = 0,
    val totalPositions: Int = 0,
    val progressPercent: Float = 0f,
    val lastReadAt: Long = System.currentTimeMillis(),
    val totalReadTimeMs: Long = 0,
)
