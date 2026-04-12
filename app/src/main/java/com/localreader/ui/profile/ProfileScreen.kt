package com.localreader.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.localreader.LocalReaderApp
import com.localreader.data.model.Book
import com.localreader.data.model.UserProfile
import com.localreader.ui.theme.ThemeMode
import com.localreader.ui.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import org.json.JSONArray
import org.json.JSONObject

class ProfileViewModel(application: Application) : ViewModel() {
    private val database = (application as LocalReaderApp).database
    private val profileDao = database.userProfileDao()

    private val _nickname = MutableStateFlow("读者")
    val nickname: StateFlow<String> = _nickname
    
    private val _avatarPath = MutableStateFlow<String?>(null)
    val avatarPath: StateFlow<String?> = _avatarPath

    init {
        viewModelScope.launch {
            profileDao.getUserProfile().collect { profile ->
                profile?.let { 
                    _nickname.value = it.nickname
                    _avatarPath.value = it.avatarPath
                }
            }
        }
    }

    fun updateNickname(newNickname: String) {
        viewModelScope.launch {
            profileDao.updateProfile(UserProfile(nickname = newNickname))
        }
    }
    
    fun updateAvatarPath(newPath: String?) {
        viewModelScope.launch {
            profileDao.updateProfile(UserProfile(avatarPath = newPath))
        }
    }

    private fun bookToJson(book: Book): JSONObject = JSONObject().apply {
        put("id", book.id)
        put("title", book.title)
        put("author", book.author)
        put("filePath", book.filePath ?: "")
        put("coverPath", book.coverPath ?: "")
        put("fileType", book.fileType.name)
        put("fileSize", book.fileSize)
        put("totalChapters", book.totalChapters)
        put("addedAt", book.addedAt)
    }

    private fun jsonToBook(json: JSONObject): Book = Book(
        id = json.optLong("id", 0),
        title = json.optString("title", ""),
        author = json.optString("author", ""),
        filePath = json.optString("filePath", null),
        coverPath = json.optString("coverPath", null),
        fileType = try { com.localreader.data.model.BookFileType.valueOf(json.optString("fileType", "EPUB")) } catch (e: Exception) { com.localreader.data.model.BookFileType.EPUB },
        fileSize = json.optLong("fileSize", 0),
        totalChapters = json.optInt("totalChapters", 0),
        addedAt = json.optLong("addedAt", System.currentTimeMillis())
    )

    suspend fun backupDatabase(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.cacheDir, "backup")
            if (backupDir.exists()) backupDir.deleteRecursively()
            backupDir.mkdirs()
            val books = database.bookDao().getAllBooks().first()
            val jsonArray = JSONArray()
            books.forEach { jsonArray.put(bookToJson(it)) }
            File(backupDir, "books.json").writeText(jsonArray.toString())
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    backupDir.listFiles()?.forEach { file ->
                        zipOut.putNextEntry(ZipEntry(file.name))
                        file.inputStream().use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                }
            }
            backupDir.deleteRecursively()
            true
        } catch (e: Exception) { false }
    }

    suspend fun restoreDatabase(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.cacheDir, "restore_temp")
            if (backupDir.exists()) backupDir.deleteRecursively()
            backupDir.mkdirs()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry
                    while (entry != null) {
                        val file = File(backupDir, entry.name)
                        file.parentFile?.mkdirs()
                        file.outputStream().use { out ->
                            val buffer = ByteArray(4096)
                            var len = zipIn.read(buffer)
                            while (len > 0) { out.write(buffer, 0, len); len = zipIn.read(buffer) }
                        }
                        entry = zipIn.nextEntry
                    }
                }
            }
            val booksFile = File(backupDir, "books.json")
            if (booksFile.exists()) {
                val json = JSONArray(booksFile.readText())
                for (i in 0 until json.length()) {
                    database.bookDao().insertBook(jsonToBook(json.getJSONObject(i)))
                }
            }
            backupDir.deleteRecursively()
            true
        } catch (e: Exception) { false }
    }
}

@Composable
fun ProfileScreen(themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val viewModel = viewModel<ProfileViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(context.applicationContext as LocalReaderApp) as T
        }
    )

    val nickname by viewModel.nickname.collectAsState()
    val avatarPath by viewModel.avatarPath.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showSubPage by remember { mutableStateOf<String?>(null) }
    var selectedBackupUri by remember { mutableStateOf<Uri?>(null) }
    var showProgress by remember { mutableStateOf(false) }

    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { scope.launch { viewModel.updateAvatarPath(it.toString()) } } }
    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? -> uri?.let { selectedBackupUri = it } }
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? -> uri?.let {
        scope.launch {
            showProgress = true
            val success = viewModel.restoreDatabase(context, it)
            showProgress = false
            Toast.makeText(context, if (success) "恢复成功" else "恢复失败", Toast.LENGTH_SHORT).show()
        }
    } }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("我的", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(24.dp)) }
        
        // 个人资料 - 居中头像+用户名
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { avatarLauncher.launch("image/*") }
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarPath != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(Uri.parse(avatarPath)).crossfade(true).build(),
                                contentDescription = "头像",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { showNicknameDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("编辑资料")
                    }
                }
            }
        }
        
        item { Spacer(Modifier.height(24.dp)) }
        
        // 三个菜单项
        items(listOf(
            Triple(Icons.Default.Palette, "主题设置", "选择主题模式"),
            Triple(Icons.Default.CloudUpload, "备份与恢复", "导出/导入数据"),
            Triple(Icons.Default.FontDownload, "字体导入", "TTF/OTF字体")
        )) { (icon, title, subtitle) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showSubPage = title },
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    // 二级页面 - 主题设置
    if (showSubPage == "主题设置") {
        val uiState by themeViewModel.uiState.collectAsState()
        AlertDialog(
            onDismissRequest = { showSubPage = null },
            title = { Text("主题设置") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { themeViewModel.setThemeMode(mode) }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(when (mode) { ThemeMode.LIGHT -> "浅色"; ThemeMode.DARK -> "深色"; ThemeMode.SYSTEM -> "跟随系统" })
                            RadioButton(selected = uiState.themeMode == mode, onClick = { themeViewModel.setThemeMode(mode) })
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSubPage = null }) { Text("确定") } }
        )
    }

    // 二级页面 - 备份与恢复
    if (showSubPage == "备份与恢复") {
        AlertDialog(
            onDismissRequest = { showSubPage = null },
            title = { Text("备份与恢复") },
            text = {
                Column {
                    Text("备份：将数据导出为zip文件")
                    Spacer(Modifier.height(16.dp))
                    Text("恢复：从备份文件导入覆盖当前数据")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showSubPage = null
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    backupLauncher.launch("localreader_backup_${dateFormat.format(Date())}.zip")
                }) { Text("备份") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSubPage = null
                    restoreLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed"))
                }) { Text("恢复") }
            }
        )
    }

    // 二级页面 - 字体导入
    if (showSubPage == "字体导入") {
        AlertDialog(
            onDismissRequest = { showSubPage = null },
            title = { Text("字体导入") },
            text = { Text("功能开发中...") },
            confirmButton = { TextButton(onClick = { showSubPage = null }) { Text("确定") } }
        )
    }

    // 编辑昵称对话框
    if (showNicknameDialog) {
        var inputText by remember { mutableStateOf(nickname) }
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            title = { Text("编辑昵称") },
            text = { OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("输入昵称") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { viewModel.updateNickname(inputText); showNicknameDialog = false }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { showNicknameDialog = false }) { Text("取消") } }
        )
    }

    LaunchedEffect(selectedBackupUri) {
        selectedBackupUri?.let { uri ->
            showProgress = true
            val success = viewModel.backupDatabase(context, uri)
            showProgress = false
            Toast.makeText(context, if (success) "备份成功" else "备份失败", Toast.LENGTH_SHORT).show()
            selectedBackupUri = null
        }
    }

    if (showProgress) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
}
