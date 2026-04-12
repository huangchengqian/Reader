package com.localreader.ui.reader

import android.graphics.Color
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import com.localreader.ui.reader.ReaderSettingsSheet
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.localreader.LocalReaderApp
import com.localreader.data.model.Bookmark
import com.localreader.ui.reader.ReaderStyle
import com.localreader.ui.reader.FontOption
import com.localreader.ui.reader.TextAlignOption
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    bookId: Long,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: ReaderViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(context.applicationContext as LocalReaderApp) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val style by viewModel.style.collectAsState()
    val fontFamily = viewModel.getFontFamily()

    var showMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    var showChapterList by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPageIndex,
        pageCount = {
            val pages = uiState.pagesPerChapter.getOrNull(uiState.currentChapterIndex) ?: emptyList()
            pages.size.coerceAtLeast(1)
        }
    )

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
        viewModel.loadBookmarks(bookId)
    }

    DisposableEffect(Unit) {
        enterImmersiveMode(context)
        onDispose {
            exitImmersiveMode(context)
            viewModel.endReadingSession()
        }
    }

    LaunchedEffect(showMenu) {
        if (showMenu) {
            exitImmersiveMode(context)
        } else {
            enterImmersiveMode(context)
        }
    }

    val bgColor = ComposeColor(style.backgroundColor)
    val textColor = ComposeColor(style.textColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        if (style.pageAnim != 4) {
                            // Only handle taps in page-turn mode, not scroll mode
                            val screenWidth = size.width
                            val leftZone = screenWidth / 3
                            val rightZone = screenWidth * 2 / 3

                            when {
                                offset.x < leftZone -> viewModel.prevPage()
                                offset.x > rightZone -> viewModel.nextPage()
                                else -> showMenu = !showMenu
                            }
                        } else {
                            // In scroll mode, center tap shows menu
                            showMenu = !showMenu
                        }
                    }
                )
            }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            when (style.pageAnim) {
                4 -> {
                    ColumnContent(
                        paragraphs = uiState.paragraphs,
                        style = style,
                        textColor = textColor,
                        scrollState = scrollState,
                        fontFamily = fontFamily
                    )
                }
                else -> {
                    val currentPages = uiState.pagesPerChapter.getOrNull(uiState.currentChapterIndex) ?: emptyList()
                    val currentPage = uiState.currentPageIndex
                    
                    val pageCount = currentPages.size.coerceAtLeast(1)
                    val pagePagerState = rememberPagerState(
                        initialPage = currentPage,
                        pageCount = { pageCount }
                    )

                    HorizontalPager(
                        state = pagePagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val pageText = currentPages.getOrElse(page) { "" }
                        val textAlign = when (style.textAlignment) {
                            1 -> TextAlign.Center
                            2 -> TextAlign.Justify
                            else -> TextAlign.Start
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = "${page + 1} / $pageCount",
                                color = textColor.copy(alpha = 0.6f),
                                fontSize = (style.fontSize * 0.7f).sp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            )
                            Text(
                                text = pageText,
                                color = textColor,
                                fontSize = style.fontSize.sp,
                                fontFamily = fontFamily,
                                lineHeight = (style.fontSize * style.lineSpacing).sp,
                                textAlign = textAlign
                            )
                        }
                    }
                    
                    LaunchedEffect(pagePagerState.currentPage) {
                        if (pagePagerState.currentPage != uiState.currentPageIndex) {
                            viewModel.goToPage(pagePagerState.currentPage)
                        }
                    }
                    
                    LaunchedEffect(uiState.currentPageIndex, uiState.currentChapterIndex) {
                        val targetPage = uiState.currentPageIndex
                        if (pagePagerState.currentPage != targetPage && targetPage >= 0 && targetPage < pageCount) {
                            pagePagerState.animateScrollToPage(targetPage)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComposeColor.Black.copy(alpha = 0.3f))
                    .clickable { showMenu = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(MaterialTheme.colorScheme.surface)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                                }
                                Column(modifier = Modifier.widthIn(max = 150.dp)) {
                                    Text(
                                        text = uiState.title.take(30) + if (uiState.title.length > 30) "..." else "",
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (uiState.chapters.isNotEmpty()) {
                                        Text(
                                            text = uiState.chapters.getOrNull(uiState.currentChapterIndex)?.title ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            Row {
                                IconButton(onClick = { showChapterList = true }) {
                                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "目录")
                                }
                                IconButton(onClick = { showBookmarks = true }) {
                                    Icon(Icons.Default.Brightness5, contentDescription = "书签")
                                }
                                IconButton(onClick = { showSettings = true }) {
                                    Icon(Icons.Default.Settings, contentDescription = "设置")
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = style.pageAnim != 4 && !showMenu,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.prevPage() },
                    enabled = uiState.currentPageIndex > 0 || uiState.currentChapterIndex > 0
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "上一页")
                }
                Text(
                    text = "${uiState.currentPageIndex + 1} / ${uiState.pagesPerChapter.getOrNull(uiState.currentChapterIndex)?.size ?: 1}",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = { viewModel.nextPage() }
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "下一页")
                }
            }
        }
    }

    if (showSettings) {
        ReaderSettingsSheet(
            style = style,
            onStyleChange = { newStyle ->
                viewModel.updateStyle(newStyle)
            },
            onBackgroundChange = { color ->
                viewModel.setBackgroundColor(color)
            },
            onPickBackgroundImage = { },
            onDismiss = { showSettings = false }
        )
    }

    if (showChapterList) {
        ChapterListSheet(
            chapters = uiState.chapters,
            currentChapterIndex = uiState.currentChapterIndex,
            onChapterSelect = { index ->
                viewModel.goToChapter(index)
                showChapterList = false
            },
            onDismiss = { showChapterList = false }
        )
    }

    if (showBookmarks) {
        BookmarkSheet(
            bookmarks = viewModel.bookmarks.value,
            onBookmarkClick = { bookmark ->
                viewModel.goToChapter(bookmark.chapterIndex)
                showBookmarks = false
            },
            onBookmarkDeleted = { bookmark ->
                viewModel.deleteBookmark(bookmark)
            },
            onDismiss = { showBookmarks = false }
        )
    }
}

@Composable
private fun ColumnContent(
    paragraphs: List<String>,
    style: ReaderStyle,
    textColor: ComposeColor,
    scrollState: androidx.compose.foundation.ScrollState,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        paragraphs.forEachIndexed { index, paragraph ->
            val isFirstParagraph = index == 0
            val textAlign = when (style.textAlignment) {
                1 -> TextAlign.Center
                2 -> TextAlign.Justify
                else -> TextAlign.Start
            }
            Text(
                text = if (isFirstParagraph && style.textIndent) "　　$paragraph" else paragraph,
                style = TextStyle(
                    fontSize = style.fontSize.sp,
                    color = textColor,
                    fontFamily = fontFamily,
                    lineHeight = (style.fontSize * style.lineSpacing).sp,
                    textAlign = textAlign,
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    bottom = if (isFirstParagraph) style.paragraphSpacing.dp else 8.dp
                )
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChapterPagerContent(
    chapters: List<ReaderChapter>,
    currentIndex: Int,
    style: ReaderStyle,
    textColor: ComposeColor,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onPageChanged: (Int) -> Unit,
    fontFamily: FontFamily
) {
    val fontSize = style.fontSize
    val lineSpacing = style.lineSpacing
    val paragraphSpacing = style.paragraphSpacing
    val textIndent = style.textIndent

    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val chapter = chapters.getOrNull(page) ?: return@HorizontalPager
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (page == 0) {
                Text(
                    text = chapter.title,
                    style = TextStyle(
                        fontSize = (fontSize + 4).sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = textColor,
                        fontFamily = fontFamily
                    ),
                    modifier = Modifier.padding(bottom = paragraphSpacing.dp)
                )
            }

            chapter.paragraphs.forEachIndexed { index, paragraph ->
                val isFirstParagraph = index == 0
                Text(
                    text = if (isFirstParagraph && textIndent) "　　$paragraph" else paragraph,
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        color = textColor,
                        fontFamily = fontFamily,
                        lineHeight = (fontSize * lineSpacing).sp,
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(
                        bottom = if (isFirstParagraph) paragraphSpacing.dp else 8.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun TopMenuBar(
    title: String,
    chapterTitle: String,
    textColor: ComposeColor,
    bgColor: ComposeColor,
    isNightMode: Boolean,
    onBack: () -> Unit,
    onChapterList: () -> Unit,
    onBookmarks: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = textColor
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (chapterTitle.isNotEmpty()) {
                    Text(
                        text = chapterTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row {
            IconButton(onClick = onChapterList) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "目录",
                    tint = textColor
                )
            }
            IconButton(onClick = onBookmarks) {
                Icon(
                    Icons.Default.Brightness5,
                    contentDescription = "书签",
                    tint = textColor
                )
            }
            IconButton(onClick = onSettings) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = textColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterListSheet(
    chapters: List<ReaderChapter>,
    currentChapterIndex: Int,
    onChapterSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "目录",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                itemsIndexed(chapters) { index, chapter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChapterSelect(index) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (index == currentChapterIndex) "● " else "○ ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (index == currentChapterIndex) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (index == currentChapterIndex) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkSheet(
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit,
    onBookmarkDeleted: (Bookmark) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "书签",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            if (bookmarks.isEmpty()) {
                Text(
                    text = "暂无书签",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(400.dp)
                ) {
                    items(bookmarks) { bookmark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBookmarkClick(bookmark) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bookmark.content.take(50),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "章节 ${bookmark.chapterIndex + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { onBookmarkDeleted(bookmark) }) {
                                Text("删除")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    style: ReaderStyle,
    onStyleChange: (ReaderStyle) -> Unit,
    onBackgroundChange: (Int) -> Unit,
    onPickBackgroundImage: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onStyleChange(style.copy(backgroundImageUri = it.toString()))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(text = "阅读设置", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "背景颜色",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val backgrounds = listOf(
                    0xFFFFFFFF.toInt(), 0xFFF5E6D3.toInt(), 0xFFE8F5E9.toInt(),
                    0xFFE3F2FD.toInt(), 0xFF1C1B1F.toInt(), 0xFF2D2D2D.toInt()
                )
                backgrounds.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ComposeColor(color))
                            .clickable { onBackgroundChange(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (style.backgroundColor == color) {
                            Text("✓", color = if (isDarkColorInt(color)) ComposeColor.White else ComposeColor.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("背景图片", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (style.backgroundImageUri != null) "已设置" else "无",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Row {
                    if (style.backgroundImageUri != null) {
                        TextButton(onClick = { onStyleChange(style.copy(backgroundImageUri = null)) }) {
                            Text("清除")
                        }
                    }
                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text(if (style.backgroundImageUri != null) "更换" else "选择图片")
                    }
                }
            }

            if (style.backgroundImageUri != null) {
                Text(
                    text = "背景透明度: ${(style.backgroundAlpha * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = style.backgroundAlpha,
                    onValueChange = { onStyleChange(style.copy(backgroundAlpha = it)) },
                    valueRange = 0.1f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "翻页方式",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PageAnimType.entries.forEach { anim ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (style.pageAnim == anim.value) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onStyleChange(style.copy(pageAnim = anim.value)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = anim.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (style.pageAnim == anim.value) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "亮度: ${if (style.brightness < 0) "跟随系统" else "${(style.brightness * 100).toInt()}%"}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = if (style.brightness < 0) 0.5f else style.brightness,
                onValueChange = { 
                    val brightness = if (it == 0.5f) -1f else it
                    onStyleChange(style.copy(brightness = brightness))
                },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "文本对齐",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextAlignOption.entries.forEach { align ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (style.textAlignment == align.value) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onStyleChange(style.copy(textAlignment = align.value)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = align.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (style.textAlignment == align.value) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "字体",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FontOption.entries.toList()) { font ->
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (style.fontFamily == font.value) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onStyleChange(style.copy(fontFamily = font.value)) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = font.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (style.fontFamily == font.value) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "字体大小: ${style.fontSize.toInt()}sp",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = style.fontSize,
                onValueChange = { onStyleChange(style.copy(fontSize = it)) },
                valueRange = 12f..28f,
                steps = 7,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "行间距: ${String.format("%.1f", style.lineSpacing)}x",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = style.lineSpacing,
                onValueChange = { onStyleChange(style.copy(lineSpacing = it)) },
                valueRange = 1.2f..2.2f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("首行缩进", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Switch(
                    checked = style.textIndent,
                    onCheckedChange = { onStyleChange(style.copy(textIndent = it)) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun isDarkColorInt(color: Int): Boolean {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance < 0.5
}

private fun enterImmersiveMode(context: android.content.Context) {
    val window = (context as? android.app.Activity)?.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

private fun exitImmersiveMode(context: android.content.Context) {
    val window = (context as? android.app.Activity)?.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
}