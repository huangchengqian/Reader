package com.localreader.ui.reader

import android.app.Application
import android.graphics.Color
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localreader.LocalReaderApp
import com.localreader.data.model.Book
import com.localreader.data.model.Bookmark
import com.localreader.data.model.ReadingProgress
import com.localreader.service.EBookParserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.font.FontFamily
import java.io.File
import java.util.Locale

data class ReaderChapter(
    val title: String,
    val paragraphs: List<String>,
)

data class ReaderUiState(
    val title: String = "",
    val chapters: List<ReaderChapter> = emptyList(),
    // current chapter index
    val currentChapterIndex: Int = 0,
    val pagesPerChapter: List<List<String>> = emptyList(),
    val currentPageIndex: Int = 0,
    val paragraphs: List<String> = emptyList(),
    val scrollPosition: Int = 0,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false,
)

data class ReaderStyle(
    val backgroundColor: Int = Color.WHITE,
    val textColor: Int = 0xFF333333.toInt(),
    val fontSize: Float = 18f,
    val lineSpacing: Float = 1.6f,
    val paragraphSpacing: Float = 24f,
    val textIndent: Boolean = true,
    val textAlignment: Int = 0,
    val fontFamily: String = "default",
    val brightness: Float = -1f,
    val pageAnim: Int = 0,
    val backgroundImageUri: String? = null,
    val backgroundAlpha: Float = 1f,
    val isNightMode: Boolean = false,
)

enum class FontOption(val value: String, val displayName: String) {
    DEFAULT("default", "默认字体"),
    SERIF("serif", "衬线体"),
    SANS_SERIF("sans-serif", "无衬线"),
    MONOSPACE("monospace", "等宽字体"),
    CJK("cjk", "思源宋体"),
    CJK_SANS("cjk-sans", "思源黑体"),
}

enum class TextAlignOption(val value: Int, val displayName: String) {
    LEFT(0, "左对齐"),
    CENTER(1, "居中"),
    JUSTIFY(2, "两端对齐"),
}

enum class PageAnimType(val value: Int, val displayName: String) {
    NONE(0, "无"),
    COVER(1, "覆盖"),
    SLIDE(2, "滑动"),
    SIMULATION(3, "仿真"),
    SCROLL(4, "滚动"),
}

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as LocalReaderApp).database
    private val bookDao = database.bookDao()
    private val bookmarkDao = database.bookmarkDao()
    private val progressDao = database.readingProgressDao()
    private val sessionDao = database.readingSessionDao()

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private val _style = MutableStateFlow(ReaderStyle())
    val style: StateFlow<ReaderStyle> = _style.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private var currentBook: Book? = null
    private var sessionId: Long = 0
    private var sessionStartTime: Long = 0

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        applyDefaultStyle()
        initTts(application)
    }

    private fun initTts(application: Application) {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.CHINESE
                isTtsReady = true
            }
        }
    }

    fun toggleReadAloud() {
        val state = _uiState.value
        if (state.isSpeaking) {
            stopReadAloud()
        } else {
            startReadAloud()
        }
    }

    private fun startReadAloud() {
        if (!isTtsReady) return
        val state = _uiState.value
        val currentPages = state.pagesPerChapter.getOrNull(state.currentChapterIndex) ?: emptyList()
        val currentPageText = currentPages.getOrNull(state.currentPageIndex) ?: state.paragraphs.joinToString("\n")
        
        if (currentPageText.isNotBlank()) {
            tts?.speak(currentPageText, TextToSpeech.QUEUE_FLUSH, null, "read_aloud")
            _uiState.value = state.copy(isSpeaking = true)
        }
    }

    fun stopReadAloud() {
        tts?.stop()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    fun addBookmark() {
        val book = currentBook ?: return
        val state = _uiState.value
        val currentPages = state.pagesPerChapter.getOrNull(state.currentChapterIndex) ?: emptyList()
        val currentPageText = currentPages.getOrNull(state.currentPageIndex) ?: ""
        val chapterTitle = state.chapters.getOrNull(state.currentChapterIndex)?.title ?: ""

        viewModelScope.launch {
            val bookmark = Bookmark(
                bookId = book.id,
                chapterIndex = state.currentChapterIndex,
                pageIndex = state.currentPageIndex,
                chapterTitle = chapterTitle,
                content = currentPageText.take(200)
            )
            bookmarkDao.insertBookmark(bookmark)
            loadBookmarks(book.id)
        }
    }

    fun loadBookmarks(bookId: Long) {
        viewModelScope.launch {
            bookmarkDao.getBookmarksByBookId(bookId).collect { list ->
                _bookmarks.value = list
            }
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkDao.deleteBookmark(bookmark)
            currentBook?.let { loadBookmarks(it.id) }
        }
    }

    fun goToBookmark(bookmark: Bookmark) {
        goToChapter(bookmark.chapterIndex)
    }

    private fun applyDefaultStyle() {
        _style.value = ReaderStyle(
            backgroundColor = Color.WHITE,
            textColor = Color.parseColor("#333333"),
            fontSize = 18f,
            lineSpacing = 1.6f,
            paragraphSpacing = 24f,
            textIndent = true,
        )
    }

    fun loadBook(bookId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val book = bookDao.getBookById(bookId) ?: return@launch
            currentBook = book

            val parser = EBookParserService(getApplication())
            val file = File(book.filePath)

            if (!file.exists()) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            withContext(Dispatchers.IO) {
                try {
                    val fileInputStream = file.inputStream()
                    val parsed = parser.parseBook(fileInputStream, book.fileType)
                    fileInputStream.close()

                    val chapters = parsed.chapters.map { chapter ->
                        ReaderChapter(
                            title = chapter.title,
                            paragraphs = chapter.paragraphs,
                        )
                    }

                    val savedProgress = progressDao.getProgressByBookIdSync(bookId)
                    val chapterIndex = savedProgress?.currentChapter ?: 0
                    val paragraphs = chapters.getOrNull(chapterIndex)?.paragraphs ?: emptyList()

                    val newPagesPerChapter = chapters.map { chap ->
                        val chapterText = chap.paragraphs.joinToString("\n\n")
                        paginateChapter(chap, chapterText)
                    }

                    _uiState.value = _uiState.value.copy(
                        title = book.title,
                        chapters = chapters,
                        // start at the first page of the current chapter
                        currentChapterIndex = chapterIndex.coerceIn(0, chapters.size - 1),
                        pagesPerChapter = newPagesPerChapter,
                        currentPageIndex = 0,
                        scrollPosition = savedProgress?.currentPosition ?: 0,
                        isLoading = false,
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }

            startReadingSession(bookId)
        }
    }

    private suspend fun startReadingSession(bookId: Long) {
        sessionId = sessionDao.insertSession(
            com.localreader.data.model.ReadingSession(
                bookId = bookId,
                startedAt = System.currentTimeMillis(),
                endedAt = null
            )
        )
        sessionStartTime = System.currentTimeMillis()
    }

    fun endReadingSession() {
        viewModelScope.launch {
            val endedAt = System.currentTimeMillis()
            val durationMs = endedAt - sessionStartTime
            sessionDao.endSession(sessionId, endedAt, durationMs)
            saveProgress()
        }
    }

    private suspend fun saveProgress() {
        val book = currentBook ?: return
        val state = _uiState.value
        val elapsedMs = System.currentTimeMillis() - sessionStartTime

        val existing = progressDao.getProgressByBookIdSync(book.id)
        val totalReadTime = (existing?.totalReadTimeMs ?: 0) + elapsedMs

        val progress = ReadingProgress(
            id = existing?.id ?: 0,
            bookId = book.id,
            // Persist chapter and page position for paginated content
            currentChapter = state.currentChapterIndex,
            currentPosition = state.currentPageIndex,
            totalPositions = state.pagesPerChapter.size, // chapters with pages considered as positions
            progressPercent = if (state.chapters.isNotEmpty()) {
                // rough progress by chapters; refine if needed using pages per chapter
                (state.currentChapterIndex + 1).toFloat() / state.chapters.size
            } else 0f,
            lastReadAt = System.currentTimeMillis(),
            totalReadTimeMs = totalReadTime,
        )

        if (existing != null) {
            progressDao.updateProgress(progress)
        } else {
            progressDao.insertProgress(progress)
        }
        sessionStartTime = System.currentTimeMillis()
    }

    fun goToChapter(index: Int) {
        val state = _uiState.value
        if (index < 0 || index >= state.chapters.size) return

        viewModelScope.launch {
            saveProgress()
            _uiState.value = state.copy(
                currentChapterIndex = index,
                // reset to first page of the new chapter
                currentPageIndex = 0,
                paragraphs = state.chapters[index].paragraphs,
            )
        }
    }

    fun nextChapter() {
        val state = _uiState.value
        if (state.currentChapterIndex < state.chapters.size - 1) {
            goToChapter(state.currentChapterIndex + 1)
        }
    }

    fun prevChapter() {
        val state = _uiState.value
        if (state.currentChapterIndex > 0) {
            // Move to the last page of the previous chapter
            val prevIndex = state.currentChapterIndex - 1
            val prevPages = state.pagesPerChapter.getOrNull(prevIndex) ?: emptyList()
            val lastPage = (prevPages.size - 1).coerceAtLeast(0)
            _uiState.value = state.copy(
                currentChapterIndex = prevIndex,
                currentPageIndex = lastPage,
                paragraphs = state.chapters[prevIndex].paragraphs,
            )
        }
    }

    fun nextPage() {
        val state = _uiState.value
        val pages = state.pagesPerChapter.getOrNull(state.currentChapterIndex) ?: emptyList()
        if (state.currentPageIndex < pages.size - 1) {
            updateScrollPosition(state.currentPageIndex + 1)
        } else {
            nextChapter()
        }
    }

    fun prevPage() {
        val state = _uiState.value
        if (state.currentPageIndex > 0) {
            updateScrollPosition(state.currentPageIndex - 1)
        } else {
            // beginning of chapter -> go to previous chapter if available
            prevChapter()
        }
    }

    fun updateScrollPosition(position: Int) {
        val state = _uiState.value
        val chapterPages = state.pagesPerChapter.getOrNull(state.currentChapterIndex) ?: emptyList()
        val clamped = position.coerceIn(0, (chapterPages.size - 1).coerceAtLeast(0))
        _uiState.value = state.copy(currentPageIndex = clamped)
    }
    
    fun goToPage(index: Int) {
        updateScrollPosition(index)
    }
    
    fun updateStyle(newStyle: ReaderStyle) {
        _style.value = newStyle
    }

    fun setBackgroundColor(color: Int) {
        val current = _style.value
        val textColor = if (isDarkColor(color)) {
            Color.WHITE
        } else {
            Color.parseColor("#333333")
        }
        _style.value = current.copy(
            backgroundColor = color,
            textColor = textColor,
        )
    }

    fun setFontSize(size: Float) {
        _style.value = _style.value.copy(fontSize = size)
    }

    fun setLineSpacing(spacing: Float) {
        _style.value = _style.value.copy(lineSpacing = spacing)
    }

    fun setBrightness(brightness: Float) {
        _style.value = _style.value.copy(brightness = brightness)
    }

    fun setPageAnim(anim: Int) {
        _style.value = _style.value.copy(pageAnim = anim)
    }

    fun setBackgroundImage(uri: String?) {
        _style.value = _style.value.copy(backgroundImageUri = uri)
    }

    fun setBackgroundAlpha(alpha: Float) {
        _style.value = _style.value.copy(backgroundAlpha = alpha)
    }

    fun setTextAlignment(alignment: Int) {
        _style.value = _style.value.copy(textAlignment = alignment)
    }

    fun setFontFamily(family: String) {
        _style.value = _style.value.copy(fontFamily = family)
    }

    fun getFontFamily(): FontFamily {
        return when (_style.value.fontFamily) {
            "serif" -> FontFamily.Serif
            "sans-serif" -> FontFamily.SansSerif
            "monospace" -> FontFamily.Monospace
            "cjk" -> FontFamily.Serif
            "cjk-sans" -> FontFamily.SansSerif
            else -> FontFamily.Default
        }
    }

    fun toggleNightMode() {
        val current = _style.value
        val newNightMode = !current.isNightMode
        if (newNightMode) {
            _style.value = current.copy(
                isNightMode = true,
                backgroundColor = 0xFF1C1B1F.toInt(),
                textColor = Color.WHITE
            )
        } else {
            _style.value = current.copy(
                isNightMode = false,
                backgroundColor = Color.WHITE,
                textColor = 0xFF333333.toInt()
            )
        }
    }

    private fun isDarkColor(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        return luminance < 0.5
    }

    // Pagination helper: split a chapter's text into pages based on screen metrics
    // Improved to better match Legado's text measuring approach
    private fun paginateChapter(chapter: ReaderChapter, chapterText: String): List<String> {
        val metrics = getApplication<Application>().resources.displayMetrics
        val fontSizePx = _style.value.fontSize * metrics.scaledDensity
        val lineSpacing = _style.value.lineSpacing
        val horizontalPaddingPx = (20 * metrics.density).toInt() * 2 // 20dp left/right padding
        val verticalPaddingPx = (16 * metrics.density).toInt() * 2 // 16dp top/bottom padding

        // Calculate effective view area
        val viewWidth = (metrics.widthPixels - horizontalPaddingPx).coerceAtLeast(1)
        val viewHeight = (metrics.heightPixels - verticalPaddingPx).coerceAtLeast(1)

        // More accurate character estimation based on font size and line spacing
        // Chinese characters are roughly 0.6-0.7 of font size width, Latin is 0.5-0.6
        val avgCharWidthFactor = 0.55f // Better estimate for mixed content
        val charsPerLine = (viewWidth / (fontSizePx * avgCharWidthFactor)).toInt().coerceAtLeast(1)
        val lineHeight = fontSizePx * lineSpacing
        val linesPerPage = (viewHeight / lineHeight).toInt().coerceAtLeast(1)

        // Apply 0.85 efficiency factor to account for margins and uneven line breaks
        val maxCharsPerPage = (charsPerLine * linesPerPage * 0.85f).toInt().coerceAtLeast(1)

        val pages = mutableListOf<String>()
        val whole = chapterText
        if (whole.isEmpty()) {
            pages.add("")
            return pages
        }

        // Split by paragraphs to preserve paragraph structure
        val paragraphs = whole.split(Regex("\n+")).filter { it.isNotBlank() }
        
        var currentPage = StringBuilder()
        for (paragraph in paragraphs) {
            // Check if adding this paragraph would exceed page capacity
            if (currentPage.length + paragraph.length + 1 > maxCharsPerPage) {
                // If current page has content, save it
                if (currentPage.isNotEmpty()) {
                    pages.add(currentPage.toString().trim())
                    currentPage = StringBuilder()
                }
                
                // If single paragraph exceeds page, split it further
                if (paragraph.length > maxCharsPerPage) {
                    var start = 0
                    val paraLen = paragraph.length
                    while (start < paraLen) {
                        var end = (start + maxCharsPerPage).coerceAtMost(paraLen)
                        if (end < paraLen) {
                            // Break at word boundary preferrably
                            val segment = paragraph.substring(start, end)
                            val lastSpace = segment.lastIndexOf(' ')
                            val lastPunctuation = maxOf(
                                segment.lastIndexOf('。'),
                                segment.lastIndexOf('！'),
                                segment.lastIndexOf('？'),
                                segment.lastIndexOf('.'),
                                segment.lastIndexOf('!'),
                                segment.lastIndexOf('?')
                            )
                            val breakPoint = maxOf(lastSpace, lastPunctuation)
                            if (breakPoint > start) {
                                end = breakPoint + 1
                            }
                        }
                        pages.add(paragraph.substring(start, end).trim())
                        start = end
                    }
                } else {
                    currentPage.append(paragraph)
                }
            } else {
                if (currentPage.isNotEmpty()) {
                    currentPage.append("\n")
                }
                currentPage.append(paragraph)
            }
        }
        
        // Add remaining content
        if (currentPage.isNotEmpty()) {
            pages.add(currentPage.toString().trim())
        }
        
        return pages.ifEmpty { listOf("") }
    }

    val totalChapters: Int get() = _uiState.value.chapters.size
    val currentChapterIndex: Int get() = _uiState.value.currentChapterIndex
    val currentChapterTitle: String get() = _uiState.value.chapters.getOrNull(_uiState.value.currentChapterIndex)?.title ?: ""

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
