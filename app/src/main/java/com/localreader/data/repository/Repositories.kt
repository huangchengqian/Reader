package com.localreader.data.repository

import com.localreader.data.database.BookDao
import com.localreader.data.database.ReadingProgressDao
import com.localreader.data.database.ReadingSessionDao
import com.localreader.data.database.UserProfileDao
import com.localreader.data.model.Book
import com.localreader.data.model.BookFileType
import com.localreader.data.model.ReadingProgress
import com.localreader.data.model.ReadingSession
import com.localreader.data.model.UserProfile
import com.localreader.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val progressDao: ReadingProgressDao,
    private val sessionDao: ReadingSessionDao
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(bookId: Long): Book? = bookDao.getBookById(bookId)

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    suspend fun getBookByPath(filePath: String): Book? = bookDao.getBookByPath(filePath)

    suspend fun startReadingSession(bookId: Long): Long {
        val session = ReadingSession(
            bookId = bookId,
            startedAt = System.currentTimeMillis(),
            endedAt = null
        )
        return sessionDao.insertSession(session)
    }

    suspend fun endReadingSession(sessionId: Long) {
        val endedAt = System.currentTimeMillis()
        val session = sessionDao.getActiveSession()
        session?.let {
            val durationMs = endedAt - it.startedAt
            sessionDao.endSession(sessionId, endedAt, durationMs)
        }
    }

    fun getTotalReadTimeMs(): Flow<Long?> = progressDao.getTotalReadTimeMs()

    fun getProgressByReadTime(): Flow<List<ReadingProgress>> = progressDao.getProgressByReadTime()

    suspend fun saveProgress(progress: ReadingProgress) {
        val existing = progressDao.getProgressByBookIdSync(progress.bookId)
        if (existing != null) {
            progressDao.updateProgress(progress.copy(id = existing.id))
        } else {
            progressDao.insertProgress(progress)
        }
    }

    fun getProgressByBookId(bookId: Long): Flow<ReadingProgress?> = progressDao.getProgressByBookId(bookId)

    fun getTotalTimeByBookId(bookId: Long): Flow<Long?> = sessionDao.getTotalTimeByBookId(bookId)
}

class SettingsRepository {
    private val themeModeFlow = kotlinx.coroutines.flow.MutableStateFlow(ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        themeModeFlow.value = mode
    }

    val themeMode: kotlinx.coroutines.flow.Flow<ThemeMode> = themeModeFlow
}

class UserProfileRepository(
    private val profileDao: UserProfileDao
) {
    fun getUserProfile(): Flow<UserProfile> = profileDao.getUserProfile()

    suspend fun updateNickname(nickname: String) {
        val profile = profileDao.getUserProfileSync() ?: UserProfile()
        profileDao.updateProfile(profile.copy(nickname = nickname))
    }

    suspend fun updateAvatarPath(avatarPath: String?) {
        val profile = profileDao.getUserProfileSync() ?: UserProfile()
        profileDao.updateProfile(profile.copy(avatarPath = avatarPath))
    }

    suspend fun initProfile() {
        if (profileDao.getUserProfileSync() == null) {
            profileDao.insertProfile(UserProfile())
        }
    }
}
