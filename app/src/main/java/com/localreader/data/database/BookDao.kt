package com.localreader.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.localreader.data.model.Book
import com.localreader.data.model.BookFileType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY addedAt DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): Book?

    @Query("SELECT * FROM books WHERE fileType = :fileType")
    fun getBooksByType(fileType: BookFileType): Flow<List<Book>>

    @Query("SELECT COUNT(*) FROM books")
    fun getBookCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)

    @Query("SELECT * FROM books WHERE filePath = :filePath")
    suspend fun getBookByPath(filePath: String): Book?

    @Query("SELECT * FROM books WHERE title = :title AND author = :author")
    suspend fun getBookByTitleAndAuthor(title: String, author: String): Book?
}
