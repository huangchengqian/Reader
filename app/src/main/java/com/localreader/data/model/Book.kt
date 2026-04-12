package com.localreader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val filePath: String,
    val coverPath: String?,
    val fileType: BookFileType,
    val fileSize: Long,
    val addedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val totalChapters: Int = 0,
)

enum class BookFileType {
    EPUB, MOBI, AZW3
}
