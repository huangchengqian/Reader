package com.localreader.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.localreader.data.model.Bookmark;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BookmarkDao_Impl implements BookmarkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Bookmark> __insertionAdapterOfBookmark;

  private final EntityDeletionOrUpdateAdapter<Bookmark> __deletionAdapterOfBookmark;

  private final EntityDeletionOrUpdateAdapter<Bookmark> __updateAdapterOfBookmark;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookmarkById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookmarksByBookId;

  public BookmarkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookmark = new EntityInsertionAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bookmarks` (`id`,`bookId`,`chapterIndex`,`pageIndex`,`chapterTitle`,`content`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getChapterIndex());
        statement.bindLong(4, entity.getPageIndex());
        statement.bindString(5, entity.getChapterTitle());
        statement.bindString(6, entity.getContent());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfBookmark = new EntityDeletionOrUpdateAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bookmarks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBookmark = new EntityDeletionOrUpdateAdapter<Bookmark>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bookmarks` SET `id` = ?,`bookId` = ?,`chapterIndex` = ?,`pageIndex` = ?,`chapterTitle` = ?,`content` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Bookmark entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getChapterIndex());
        statement.bindLong(4, entity.getPageIndex());
        statement.bindString(5, entity.getChapterTitle());
        statement.bindString(6, entity.getContent());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBookmarkById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteBookmarksByBookId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmarks WHERE bookId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBookmark(final Bookmark bookmark,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBookmark.insertAndReturnId(bookmark);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmark(final Bookmark bookmark,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookmark.handle(bookmark);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBookmark(final Bookmark bookmark,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBookmark.handle(bookmark);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmarkById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookmarkById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBookmarkById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookmarksByBookId(final long bookId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookmarksByBookId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bookId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBookmarksByBookId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Bookmark>> getBookmarksByBookId(final long bookId) {
    final String _sql = "SELECT * FROM bookmarks WHERE bookId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmarks"}, new Callable<List<Bookmark>>() {
      @Override
      @NonNull
      public List<Bookmark> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfChapterIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterIndex");
          final int _cursorIndexOfPageIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "pageIndex");
          final int _cursorIndexOfChapterTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterTitle");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Bookmark> _result = new ArrayList<Bookmark>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Bookmark _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpChapterIndex;
            _tmpChapterIndex = _cursor.getInt(_cursorIndexOfChapterIndex);
            final int _tmpPageIndex;
            _tmpPageIndex = _cursor.getInt(_cursorIndexOfPageIndex);
            final String _tmpChapterTitle;
            _tmpChapterTitle = _cursor.getString(_cursorIndexOfChapterTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Bookmark(_tmpId,_tmpBookId,_tmpChapterIndex,_tmpPageIndex,_tmpChapterTitle,_tmpContent,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBookmarkById(final long id, final Continuation<? super Bookmark> $completion) {
    final String _sql = "SELECT * FROM bookmarks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Bookmark>() {
      @Override
      @Nullable
      public Bookmark call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfChapterIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterIndex");
          final int _cursorIndexOfPageIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "pageIndex");
          final int _cursorIndexOfChapterTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "chapterTitle");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Bookmark _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpChapterIndex;
            _tmpChapterIndex = _cursor.getInt(_cursorIndexOfChapterIndex);
            final int _tmpPageIndex;
            _tmpPageIndex = _cursor.getInt(_cursorIndexOfPageIndex);
            final String _tmpChapterTitle;
            _tmpChapterTitle = _cursor.getString(_cursorIndexOfChapterTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Bookmark(_tmpId,_tmpBookId,_tmpChapterIndex,_tmpPageIndex,_tmpChapterTitle,_tmpContent,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
