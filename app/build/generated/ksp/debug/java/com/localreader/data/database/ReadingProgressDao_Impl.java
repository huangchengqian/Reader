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
import com.localreader.data.model.ReadingProgress;
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
public final class ReadingProgressDao_Impl implements ReadingProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReadingProgress> __insertionAdapterOfReadingProgress;

  private final EntityDeletionOrUpdateAdapter<ReadingProgress> __updateAdapterOfReadingProgress;

  private final SharedSQLiteStatement __preparedStmtOfDeleteProgressByBookId;

  public ReadingProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReadingProgress = new EntityInsertionAdapter<ReadingProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_progress` (`id`,`bookId`,`currentChapter`,`currentPosition`,`totalPositions`,`progressPercent`,`lastReadAt`,`totalReadTimeMs`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingProgress entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getCurrentChapter());
        statement.bindLong(4, entity.getCurrentPosition());
        statement.bindLong(5, entity.getTotalPositions());
        statement.bindDouble(6, entity.getProgressPercent());
        statement.bindLong(7, entity.getLastReadAt());
        statement.bindLong(8, entity.getTotalReadTimeMs());
      }
    };
    this.__updateAdapterOfReadingProgress = new EntityDeletionOrUpdateAdapter<ReadingProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reading_progress` SET `id` = ?,`bookId` = ?,`currentChapter` = ?,`currentPosition` = ?,`totalPositions` = ?,`progressPercent` = ?,`lastReadAt` = ?,`totalReadTimeMs` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingProgress entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getCurrentChapter());
        statement.bindLong(4, entity.getCurrentPosition());
        statement.bindLong(5, entity.getTotalPositions());
        statement.bindDouble(6, entity.getProgressPercent());
        statement.bindLong(7, entity.getLastReadAt());
        statement.bindLong(8, entity.getTotalReadTimeMs());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteProgressByBookId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_progress WHERE bookId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertProgress(final ReadingProgress progress,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReadingProgress.insertAndReturnId(progress);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProgress(final ReadingProgress progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReadingProgress.handle(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProgressByBookId(final long bookId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteProgressByBookId.acquire();
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
          __preparedStmtOfDeleteProgressByBookId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ReadingProgress> getProgressByBookId(final long bookId) {
    final String _sql = "SELECT * FROM reading_progress WHERE bookId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<ReadingProgress>() {
      @Override
      @Nullable
      public ReadingProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfCurrentChapter = CursorUtil.getColumnIndexOrThrow(_cursor, "currentChapter");
          final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPosition");
          final int _cursorIndexOfTotalPositions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPositions");
          final int _cursorIndexOfProgressPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "progressPercent");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final int _cursorIndexOfTotalReadTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReadTimeMs");
          final ReadingProgress _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpCurrentChapter;
            _tmpCurrentChapter = _cursor.getInt(_cursorIndexOfCurrentChapter);
            final int _tmpCurrentPosition;
            _tmpCurrentPosition = _cursor.getInt(_cursorIndexOfCurrentPosition);
            final int _tmpTotalPositions;
            _tmpTotalPositions = _cursor.getInt(_cursorIndexOfTotalPositions);
            final float _tmpProgressPercent;
            _tmpProgressPercent = _cursor.getFloat(_cursorIndexOfProgressPercent);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            final long _tmpTotalReadTimeMs;
            _tmpTotalReadTimeMs = _cursor.getLong(_cursorIndexOfTotalReadTimeMs);
            _result = new ReadingProgress(_tmpId,_tmpBookId,_tmpCurrentChapter,_tmpCurrentPosition,_tmpTotalPositions,_tmpProgressPercent,_tmpLastReadAt,_tmpTotalReadTimeMs);
          } else {
            _result = null;
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
  public Object getProgressByBookIdSync(final long bookId,
      final Continuation<? super ReadingProgress> $completion) {
    final String _sql = "SELECT * FROM reading_progress WHERE bookId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingProgress>() {
      @Override
      @Nullable
      public ReadingProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfCurrentChapter = CursorUtil.getColumnIndexOrThrow(_cursor, "currentChapter");
          final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPosition");
          final int _cursorIndexOfTotalPositions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPositions");
          final int _cursorIndexOfProgressPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "progressPercent");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final int _cursorIndexOfTotalReadTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReadTimeMs");
          final ReadingProgress _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpCurrentChapter;
            _tmpCurrentChapter = _cursor.getInt(_cursorIndexOfCurrentChapter);
            final int _tmpCurrentPosition;
            _tmpCurrentPosition = _cursor.getInt(_cursorIndexOfCurrentPosition);
            final int _tmpTotalPositions;
            _tmpTotalPositions = _cursor.getInt(_cursorIndexOfTotalPositions);
            final float _tmpProgressPercent;
            _tmpProgressPercent = _cursor.getFloat(_cursorIndexOfProgressPercent);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            final long _tmpTotalReadTimeMs;
            _tmpTotalReadTimeMs = _cursor.getLong(_cursorIndexOfTotalReadTimeMs);
            _result = new ReadingProgress(_tmpId,_tmpBookId,_tmpCurrentChapter,_tmpCurrentPosition,_tmpTotalPositions,_tmpProgressPercent,_tmpLastReadAt,_tmpTotalReadTimeMs);
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

  @Override
  public Flow<List<ReadingProgress>> getAllProgress() {
    final String _sql = "SELECT * FROM reading_progress ORDER BY lastReadAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<List<ReadingProgress>>() {
      @Override
      @NonNull
      public List<ReadingProgress> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfCurrentChapter = CursorUtil.getColumnIndexOrThrow(_cursor, "currentChapter");
          final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPosition");
          final int _cursorIndexOfTotalPositions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPositions");
          final int _cursorIndexOfProgressPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "progressPercent");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final int _cursorIndexOfTotalReadTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReadTimeMs");
          final List<ReadingProgress> _result = new ArrayList<ReadingProgress>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingProgress _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpCurrentChapter;
            _tmpCurrentChapter = _cursor.getInt(_cursorIndexOfCurrentChapter);
            final int _tmpCurrentPosition;
            _tmpCurrentPosition = _cursor.getInt(_cursorIndexOfCurrentPosition);
            final int _tmpTotalPositions;
            _tmpTotalPositions = _cursor.getInt(_cursorIndexOfTotalPositions);
            final float _tmpProgressPercent;
            _tmpProgressPercent = _cursor.getFloat(_cursorIndexOfProgressPercent);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            final long _tmpTotalReadTimeMs;
            _tmpTotalReadTimeMs = _cursor.getLong(_cursorIndexOfTotalReadTimeMs);
            _item = new ReadingProgress(_tmpId,_tmpBookId,_tmpCurrentChapter,_tmpCurrentPosition,_tmpTotalPositions,_tmpProgressPercent,_tmpLastReadAt,_tmpTotalReadTimeMs);
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
  public Flow<Long> getTotalReadTimeMs() {
    final String _sql = "SELECT SUM(totalReadTimeMs) FROM reading_progress";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<List<ReadingProgress>> getProgressByReadTime() {
    final String _sql = "SELECT * FROM reading_progress ORDER BY totalReadTimeMs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<List<ReadingProgress>>() {
      @Override
      @NonNull
      public List<ReadingProgress> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfCurrentChapter = CursorUtil.getColumnIndexOrThrow(_cursor, "currentChapter");
          final int _cursorIndexOfCurrentPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "currentPosition");
          final int _cursorIndexOfTotalPositions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPositions");
          final int _cursorIndexOfProgressPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "progressPercent");
          final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
          final int _cursorIndexOfTotalReadTimeMs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReadTimeMs");
          final List<ReadingProgress> _result = new ArrayList<ReadingProgress>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingProgress _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final int _tmpCurrentChapter;
            _tmpCurrentChapter = _cursor.getInt(_cursorIndexOfCurrentChapter);
            final int _tmpCurrentPosition;
            _tmpCurrentPosition = _cursor.getInt(_cursorIndexOfCurrentPosition);
            final int _tmpTotalPositions;
            _tmpTotalPositions = _cursor.getInt(_cursorIndexOfTotalPositions);
            final float _tmpProgressPercent;
            _tmpProgressPercent = _cursor.getFloat(_cursorIndexOfProgressPercent);
            final long _tmpLastReadAt;
            _tmpLastReadAt = _cursor.getLong(_cursorIndexOfLastReadAt);
            final long _tmpTotalReadTimeMs;
            _tmpTotalReadTimeMs = _cursor.getLong(_cursorIndexOfTotalReadTimeMs);
            _item = new ReadingProgress(_tmpId,_tmpBookId,_tmpCurrentChapter,_tmpCurrentPosition,_tmpTotalPositions,_tmpProgressPercent,_tmpLastReadAt,_tmpTotalReadTimeMs);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
