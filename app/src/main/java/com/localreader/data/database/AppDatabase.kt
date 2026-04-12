package com.localreader.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.localreader.data.model.Book
import com.localreader.data.model.Bookmark
import com.localreader.data.model.ReadingProgress
import com.localreader.data.model.ReadingSession
import com.localreader.data.model.UserProfile

@Database(
    entities = [Book::class, Bookmark::class, ReadingProgress::class, ReadingSession::class, UserProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localreader_database"
                )
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
