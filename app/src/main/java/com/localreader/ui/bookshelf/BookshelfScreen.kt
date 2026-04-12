package com.localreader.ui.bookshelf

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.localreader.LocalReaderApp
import com.localreader.data.model.Book
import com.localreader.data.model.BookFileType
import com.localreader.service.EBookParserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class BookshelfViewModel(application: Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val context = application
    private val database = (context as LocalReaderApp).database
    private val bookDao = database.bookDao()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    init {
        viewModelScope.launch {
            bookDao.getAllBooks().collect { bookList ->
                _books.value = bookList
            }
        }
    }

    suspend fun importBook(uri: Uri): Boolean {
        val parser = EBookParserService(context)

        val fileName = getFileNameFromUri(uri) ?: uri.lastPathSegment ?: return false
        val fileType = parser.determineFileType(fileName) ?: return false

        val inputStream = context.contentResolver.openInputStream(uri) ?: return false
        val parsedBook = inputStream.use { stream ->
            parser.parseBook(stream, fileType)
        }

        val existingBook = bookDao.getBookByTitleAndAuthor(parsedBook.title, parsedBook.author)
        if (existingBook != null) {
            return false
        }

        val localPath = parser.saveBookToInternalStorage(uri, fileName)

        var coverPath: String? = null
        parsedBook.coverBitmap?.let { bitmap ->
            coverPath = parser.saveCoverToCache(System.currentTimeMillis(), bitmap)
        }

        val book = Book(
            title = parsedBook.title,
            author = parsedBook.author,
            filePath = localPath,
            coverPath = coverPath,
            fileType = fileType,
            fileSize = 0,
            totalChapters = parsedBook.chapters.size,
        )
        bookDao.insertBook(book)
        return true
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        result = it.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }
}

@Composable
fun BookshelfScreen(
    onBookClick: (Long) -> Unit,
    navController: androidx.navigation.NavController
) {
    val context = LocalContext.current
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<BookshelfViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BookshelfViewModel(context.applicationContext as com.localreader.LocalReaderApp) as T
            }
        }
    )

    val books by viewModel.books.collectAsState()
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            scope.launch {
                val success = viewModel.importBook(it)
                if (!success) {
                    Toast.makeText(context, "书籍已存在", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "书架",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = { launcher.launch(arrayOf("application/epub+zip", "application/x-mobipocket-ebook", "application/x-kindle-atz")) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("导入")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (books.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "书架暂无书籍",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "点击右上角导入书籍",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCoverItem(
                        book = book,
                        onClick = { onBookClick(book.id) },
                        onDelete = { scope.launch { viewModel.deleteBook(book) } }
                    )
                }
            }
        }
    }
}

@Composable
fun BookCoverItem(
    book: Book,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.7f)
            .clickable(onClick = onClick)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (book.coverPath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(book.coverPath)
                            .crossfade(true)
                            .build(),
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(6.dp)
                ) {
                    Column {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = book.author,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
