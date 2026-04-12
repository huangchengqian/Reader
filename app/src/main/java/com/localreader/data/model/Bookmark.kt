package com.localreader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val chapterIndex: Int,
    val pageIndex: Int,
    val chapterTitle: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
)
