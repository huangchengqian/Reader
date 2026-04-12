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
import com.localreader.data.model.Book;
import com.localreader.data.model.BookFileType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
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
public final class BookDao_Impl implements BookDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Book> __insertionAdapterOfBook;

  private final EntityDeletionOrUpdateAdapter<Book> __deletionAdapterOfBook;

  private final EntityDeletionOrUpdateAdapter<Book> __updateAdapterOfBook;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookById;

  public BookDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBook = new EntityInsertionAdapter<Book>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `books` (`id`,`title`,`author`,`filePath`,`coverPath`,`fileType`,`fileSize`,`addedAt`,`isCompleted`,`totalChapters`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Book entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getFilePath());
        if (entity.getCoverPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCoverPath());
        }
        statement.bindString(6, __BookFileType_enumToString(entity.getFileType()));
        statement.bindLong(7, entity.getFileSize());
        statement.bindLong(8, entity.getAddedAt());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getTotalChapters());
      }
    };
    this.__deletionAdapterOfBook = new EntityDeletionOrUpdateAdapter<Book>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `books` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Book entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBook = new EntityDeletionOrUpdateAdapter<Book>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `books` SET `id` = ?,`title` = ?,`author` = ?,`filePath` = ?,`coverPath` = ?,`fileType` = ?,`fileSize` = ?,`addedAt` = ?,`isCompleted` = ?,`totalChapters` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Book entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getFilePath());
        if (entity.getCoverPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCoverPath());
        }
        statement.bindString(6, __BookFileType_enumToString(entity.getFileType()));
        statement.bindLong(7, entity.getFileSize());
        statement.bindLong(8, entity.getAddedAt());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getTotalChapters());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBookById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM books WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBook(final Book book, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBook.insertAndReturnId(book);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBooks(final List<Book> books, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBook.insert(books);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBook(final Book book, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBook.handle(book);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBook(final Book book, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBook.handle(book);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookById(final long bookId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookById.acquire();
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
          __preparedStmtOfDeleteBookById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Book>> getAllBooks() {
    final String _sql = "SELECT * FROM books ORDER BY addedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"books"}, new Callable<List<Book>>() {
      @Override
      @NonNull
      public List<Book> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCoverPath = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPath");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalChapters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChapters");
          final List<Book> _result = new ArrayList<Book>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Book _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpCoverPath;
            if (_cursor.isNull(_cursorIndexOfCoverPath)) {
              _tmpCoverPath = null;
            } else {
              _tmpCoverPath = _cursor.getString(_cursorIndexOfCoverPath);
            }
            final BookFileType _tmpFileType;
            _tmpFileType = __BookFileType_stringToEnum(_cursor.getString(_cursorIndexOfFileType));
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpTotalChapters;
            _tmpTotalChapters = _cursor.getInt(_cursorIndexOfTotalChapters);
            _item = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpFilePath,_tmpCoverPath,_tmpFileType,_tmpFileSize,_tmpAddedAt,_tmpIsCompleted,_tmpTotalChapters);
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
  public Object getBookById(final long bookId, final Continuation<? super Book> $completion) {
    final String _sql = "SELECT * FROM books WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bookId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Book>() {
      @Override
      @Nullable
      public Book call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCoverPath = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPath");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalChapters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChapters");
          final Book _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpCoverPath;
            if (_cursor.isNull(_cursorIndexOfCoverPath)) {
              _tmpCoverPath = null;
            } else {
              _tmpCoverPath = _cursor.getString(_cursorIndexOfCoverPath);
            }
            final BookFileType _tmpFileType;
            _tmpFileType = __BookFileType_stringToEnum(_cursor.getString(_cursorIndexOfFileType));
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpTotalChapters;
            _tmpTotalChapters = _cursor.getInt(_cursorIndexOfTotalChapters);
            _result = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpFilePath,_tmpCoverPath,_tmpFileType,_tmpFileSize,_tmpAddedAt,_tmpIsCompleted,_tmpTotalChapters);
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
  public Flow<List<Book>> getBooksByType(final BookFileType fileType) {
    final String _sql = "SELECT * FROM books WHERE fileType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __BookFileType_enumToString(fileType));
    return CoroutinesRoom.createFlow(__db, false, new String[] {"books"}, new Callable<List<Book>>() {
      @Override
      @NonNull
      public List<Book> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCoverPath = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPath");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalChapters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChapters");
          final List<Book> _result = new ArrayList<Book>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Book _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpCoverPath;
            if (_cursor.isNull(_cursorIndexOfCoverPath)) {
              _tmpCoverPath = null;
            } else {
              _tmpCoverPath = _cursor.getString(_cursorIndexOfCoverPath);
            }
            final BookFileType _tmpFileType;
            _tmpFileType = __BookFileType_stringToEnum(_cursor.getString(_cursorIndexOfFileType));
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpTotalChapters;
            _tmpTotalChapters = _cursor.getInt(_cursorIndexOfTotalChapters);
            _item = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpFilePath,_tmpCoverPath,_tmpFileType,_tmpFileSize,_tmpAddedAt,_tmpIsCompleted,_tmpTotalChapters);
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
  public Flow<Integer> getBookCount() {
    final String _sql = "SELECT COUNT(*) FROM books";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"books"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getBookByPath(final String filePath, final Continuation<? super Book> $completion) {
    final String _sql = "SELECT * FROM books WHERE filePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, filePath);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Book>() {
      @Override
      @Nullable
      public Book call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCoverPath = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPath");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalChapters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChapters");
          final Book _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpCoverPath;
            if (_cursor.isNull(_cursorIndexOfCoverPath)) {
              _tmpCoverPath = null;
            } else {
              _tmpCoverPath = _cursor.getString(_cursorIndexOfCoverPath);
            }
            final BookFileType _tmpFileType;
            _tmpFileType = __BookFileType_stringToEnum(_cursor.getString(_cursorIndexOfFileType));
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpTotalChapters;
            _tmpTotalChapters = _cursor.getInt(_cursorIndexOfTotalChapters);
            _result = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpFilePath,_tmpCoverPath,_tmpFileType,_tmpFileSize,_tmpAddedAt,_tmpIsCompleted,_tmpTotalChapters);
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
  public Object getBookByTitleAndAuthor(final String title, final String author,
      final Continuation<? super Book> $completion) {
    final String _sql = "SELECT * FROM books WHERE title = ? AND author = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, title);
    _argIndex = 2;
    _statement.bindString(_argIndex, author);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Book>() {
      @Override
      @Nullable
      public Book call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCoverPath = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPath");
          final int _cursorIndexOfFileType = CursorUtil.getColumnIndexOrThrow(_cursor, "fileType");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalChapters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalChapters");
          final Book _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpFilePath;
            _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            final String _tmpCoverPath;
            if (_cursor.isNull(_cursorIndexOfCoverPath)) {
              _tmpCoverPath = null;
            } else {
              _tmpCoverPath = _cursor.getString(_cursorIndexOfCoverPath);
            }
            final BookFileType _tmpFileType;
            _tmpFileType = __BookFileType_stringToEnum(_cursor.getString(_cursorIndexOfFileType));
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpTotalChapters;
            _tmpTotalChapters = _cursor.getInt(_cursorIndexOfTotalChapters);
            _result = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpFilePath,_tmpCoverPath,_tmpFileType,_tmpFileSize,_tmpAddedAt,_tmpIsCompleted,_tmpTotalChapters);
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

  private String __BookFileType_enumToString(@NonNull final BookFileType _value) {
    switch (_value) {
      case EPUB: return "EPUB";
      case MOBI: return "MOBI";
      case AZW3: return "AZW3";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private BookFileType __BookFileType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "EPUB": return BookFileType.EPUB;
      case "MOBI": return BookFileType.MOBI;
      case "AZW3": return BookFileType.AZW3;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
