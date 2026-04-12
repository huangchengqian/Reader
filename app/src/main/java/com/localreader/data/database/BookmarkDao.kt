package com.localreader.data.database

import androidx.room.*
import com.localreader.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getBookmarksByBookId(bookId: Long): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Long): Bookmark?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long
    
    @Update
    suspend fun updateBookmark(bookmark: Bookmark)
    
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
    
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
    
    @Query("DELETE FROM bookmarks WHERE bookId = :bookId")
    suspend fun deleteBookmarksByBookId(bookId: Long)
}
