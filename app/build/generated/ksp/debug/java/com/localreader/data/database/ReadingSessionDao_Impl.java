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
import com.localreader.data.model.ReadingSession;
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
public final class ReadingSessionDao_Impl implements ReadingSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReadingSession> __insertionAdapterOfReadingSession;

  private final EntityDeletionOrUpdateAdapter<ReadingSession> __updateAdapterOfReadingSession;

  private final SharedSQLiteStatement __preparedStmtOfEndSession;

  public ReadingSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReadingSession = new EntityInsertionAdapter<ReadingSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_sessions` (`id`,`bookId`,`startedAt`,`endedAt`,`durationMs`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindLong(5, entity.getDurationMs());
      }
    };
    this.__updateAdapterOfReadingSession = new EntityDeletionOrUpdateAdapter<ReadingSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reading_sessions` SET `id` = ?,`bookId` = ?,`startedAt` = ?,`endedAt` = ?,`durationMs` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBookId());
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindLong(5, entity.getDurationMs());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfEndSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reading_sessions SET endedAt = ?, durationMs = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSession(final ReadingSession session,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReadingSession.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSession(final ReadingSession session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReadingSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object endSession(final long sessionId, final long endedAt, final long durationMs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEndSession.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, endedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, durationMs);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, sessionId);
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
          __preparedStmtOfEndSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReadingSession>> getSessionsByBookId(final long bookId) {
    final String _sql = "SELECT * FROM reading_sessions WHERE bookId = ? ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_sessions"}, new Callable<List<ReadingSession>>() {
      @Override
      @NonNull
      public List<ReadingSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final List<ReadingSession> _result = new ArrayList<ReadingSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReadingSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            _item = new ReadingSession(_tmpId,_tmpBookId,_tmpStartedAt,_tmpEndedAt,_tmpDurationMs);
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
  public Object getActiveSession(final Continuation<? super ReadingSession> $completion) {
    final String _sql = "SELECT * FROM reading_sessions WHERE endedAt IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingSession>() {
      @Override
      @Nullable
      public ReadingSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final ReadingSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBookId;
            _tmpBookId = _cursor.getLong(_cursorIndexOfBookId);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            _result = new ReadingSession(_tmpId,_tmpBookId,_tmpStartedAt,_tmpEndedAt,_tmpDurationMs);
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
  public Flow<Long> getTotalTimeByBookId(final long bookId) {
    final String _sql = "SELECT SUM(durationMs) FROM reading_sessions WHERE bookId = ? AND endedAt IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_sessions"}, new Callable<Long>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
