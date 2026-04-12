package com.localreader.ui.statistics

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localreader.LocalReaderApp
import com.localreader.data.model.Book
import com.localreader.data.model.ReadingProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

data class BookStat(
    val id: Long,
    val title: String,
    val author: String,
    val readTimeMs: Long,
    val progress: Float,
    val isCompleted: Boolean
)

class StatisticsViewModel(application: Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val database = (application as LocalReaderApp).database
    private val bookDao = database.bookDao()
    private val progressDao = database.readingProgressDao()
    private val sessionDao = database.readingSessionDao()

    private val _bookStats = MutableStateFlow<List<BookStat>>(emptyList())
    val bookStats: StateFlow<List<BookStat>> = _bookStats.asStateFlow()

    private val _totalReadTimeMs = MutableStateFlow(0L)
    val totalReadTimeMs: StateFlow<Long> = _totalReadTimeMs.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                bookDao.getAllBooks(),
                progressDao.getAllProgress()
            ) { books, progresses ->
                val stats = books.map { book ->
                    val progress = progresses.find { it.bookId == book.id }
                    val sessionTime = sessionDao.getTotalTimeByBookId(book.id).first() ?: 0L
                    BookStat(
                        id = book.id,
                        title = book.title,
                        author = book.author,
                        readTimeMs = progress?.totalReadTimeMs ?: sessionTime,
                        progress = progress?.progressPercent ?: 0f,
                        isCompleted = book.isCompleted || (progress?.progressPercent ?: 0f) >= 1.0f
                    )
                }.sortedByDescending { it.readTimeMs }
                _bookStats.value = stats
                _totalReadTimeMs.value = stats.sumOf { it.readTimeMs }
            }.collect { }
        }
    }
}

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<StatisticsViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return StatisticsViewModel(context.applicationContext as com.localreader.LocalReaderApp) as T
            }
        }
    )

    val totalReadTimeMs by viewModel.totalReadTimeMs.collectAsState()
    val bookStats by viewModel.bookStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "统计",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "总阅读时长",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formatDuration(totalReadTimeMs),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "各书籍阅读时间",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (bookStats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无阅读记录",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookStats) { stat ->
                    BookStatCard(stat = stat)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "已读完",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        val completedBooks = bookStats.filter { it.isCompleted }
        if (completedBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "尚未读完任何书籍",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(completedBooks) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stat.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookStatCard(stat: BookStat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDuration(stat.readTimeMs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { stat.progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${(stat.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalMinutes = ms / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) {
        "${hours}小时${minutes}分钟"
    } else {
        "${minutes}分钟"
    }
}
