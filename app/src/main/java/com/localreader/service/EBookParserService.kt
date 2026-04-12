package com.localreader.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.Html
import com.localreader.data.model.BookFileType
import com.localreader.lib.mobi.KF6Book
import com.localreader.lib.mobi.KF8Book
import com.localreader.lib.mobi.MobiReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.ByteArrayOutputStream
import nl.siegmann.epublib.epub.EpubReader
import android.util.Log

class EBookParserService(private val context: Context) {
    private val TAG = "EBookParserService"

    data class ParsedBook(
        val title: String,
        val author: String,
        val coverBitmap: Bitmap?,
        val chapters: List<Chapter>,
    )

    data class Chapter(
        val title: String,
        val paragraphs: List<String>,
    )

    fun determineFileType(fileName: String): BookFileType? {
        return when {
            fileName.endsWith(".epub", ignoreCase = true) -> BookFileType.EPUB
            fileName.endsWith(".mobi", ignoreCase = true) -> BookFileType.MOBI
            fileName.endsWith(".azw3", ignoreCase = true) -> BookFileType.AZW3
            else -> null
        }
    }

    fun parseBook(inputStream: InputStream, fileType: BookFileType): ParsedBook {
        return when (fileType) {
            BookFileType.EPUB -> parseEpub(inputStream)
            BookFileType.MOBI -> parseMobi(inputStream)
            BookFileType.AZW3 -> parseAzw3(inputStream)
        }
    }

    private fun parseEpub(inputStream: InputStream): ParsedBook {
        val epubBook = EpubReader().readEpub(inputStream)
        val title = epubBook.metadata.titles.firstOrNull() ?: "Unknown"
        val author = epubBook.metadata.authors.firstOrNull()?.firstname + " " +
                epubBook.metadata.authors.firstOrNull()?.lastname ?: "Unknown"

        var coverBitmap: Bitmap? = null
        try {
            val coverImage = epubBook.coverImage
            if (coverImage != null) {
                coverBitmap = BitmapFactory.decodeByteArray(
                    coverImage.data, 0, coverImage.data.size
                )
            }
        } catch (_: Exception) {
        }

        val chapters = epubBook.tableOfContents.tocReferences.mapIndexed { index, tocRef ->
            val resource = tocRef.resource
            val htmlContent = if (resource != null) {
                String(resource.data, Charsets.UTF_8)
            } else {
                ""
            }
            Chapter(
                title = tocRef.title ?: "第 ${index + 1} 章",
                paragraphs = extractParagraphs(htmlContent),
            )
        }

        return ParsedBook(
            title = title,
            author = author,
            coverBitmap = coverBitmap,
            chapters = chapters,
        )
    }

    private fun parseMobi(inputStream: InputStream): ParsedBook {
        Log.d(TAG, "Using MobiParserService")
        
        try {
            val data = inputStream.readBytes()
            if (data.size < 32) {
                return ParsedBook("Error", "File too small", null, emptyList())
            }
            
            val tempFile = File(context.cacheDir, "temp_mobi_${System.currentTimeMillis()}.mobi")
            FileOutputStream(tempFile).use { it.write(data) }
            tempFile.deleteOnExit()
            
            val parser = MobiParserService(context)
            val result = parser.parseBook(Uri.fromFile(tempFile).toString())
            parser.close()
            
            val chapters = result.chapters.map { ch ->
                Chapter(ch.title, ch.paragraphs)
            }
            
            return ParsedBook(result.title, result.author, result.coverBitmap, chapters)
        } catch (e: Exception) {
            Log.e(TAG, "MobiParserService failed: ${e.message}", e)
            return ParsedBook("Parse Error", e.message ?: "Error", null, emptyList())
        }
    }
    
    private fun parseKF8Chapters(book: KF8Book): List<Chapter> {
        val chapters = mutableListOf<Chapter>()
        
        try {
            // Try to get all text records directly
            val allText = StringBuilder()
            
            // Method 1: use getRaw for each text record
            try {
                for (i in 0 until book.palmdoc.numTextRecords) {
                    try {
                        val record = book.getTextRecord(i)
                        if (record.isNotEmpty()) {
                            val text = String(record, book.charset)
                            if (text.isNotBlank()) {
                                allText.append(text).append("\n")
                            }
                        }
                    } catch (e: Exception) {
                        // Skip errors
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "getTextRecord failed: ${e.message}")
            }
            
            val fullText = allText.toString().trim()
            Log.d(TAG, "KF8 extracted text length: ${fullText.length}")
            
            if (fullText.isNotBlank()) {
                val paragraphs = cleanAndSplitText(fullText)
                Log.d(TAG, "KF8 paragraphs: ${paragraphs.size}")
                if (paragraphs.isNotEmpty()) {
                    chapters.add(Chapter("Content", paragraphs))
                }
            }
            
            // Fallback: try sections
            if (chapters.isEmpty()) {
                for (section in book.sections) {
                    try {
                        val text = book.getSectionText(section)
                        if (text.isNotBlank()) {
                            val paragraphs = cleanAndSplitText(text)
                            if (paragraphs.isNotEmpty()) {
                                chapters.add(Chapter(section.href.take(30), paragraphs))
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Section error: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "KF8 error: ${e.message}", e)
        }
        
        return chapters.ifEmpty { listOf(Chapter("Content", listOf("Unable to extract text"))) }
    }
    
    private fun parseKF6Chapters(book: KF6Book): List<Chapter> {
        val chapters = mutableListOf<Chapter>()
        
        try {
            // Try to get all text records
            val allText = StringBuilder()
            
            try {
                for (i in 0 until book.palmdoc.numTextRecords) {
                    try {
                        val record = book.getTextRecord(i)
                        if (record.isNotEmpty()) {
                            val text = String(record, book.charset)
                            if (text.isNotBlank()) {
                                allText.append(text).append("\n")
                            }
                        }
                    } catch (e: Exception) {
                        // Skip
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "getTextRecord failed: ${e.message}")
            }
            
            val fullText = allText.toString().trim()
            Log.d(TAG, "KF6 extracted text length: ${fullText.length}")
            
            if (fullText.isNotBlank()) {
                val paragraphs = cleanAndSplitText(fullText)
                Log.d(TAG, "KF6 paragraphs: ${paragraphs.size}")
                if (paragraphs.isNotEmpty()) {
                    chapters.add(Chapter("Content", paragraphs))
                }
            }
            
            // Fallback to sections
            if (chapters.isEmpty()) {
                for (section in book.sections) {
                    try {
                        val text = book.getSectionText(section)
                        if (text.isNotBlank()) {
                            val paragraphs = cleanAndSplitText(text)
                            if (paragraphs.isNotEmpty()) {
                                chapters.add(Chapter(section.href.take(30), paragraphs))
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Section error: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "KF6 error: ${e.message}", e)
        }
        
        return chapters.ifEmpty { listOf(Chapter("Content", listOf("Unable to extract text"))) }
    }
    
    private fun cleanAndSplitText(text: String): List<String> {
        var clean = try {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            text.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ")
        }
        
        clean = clean
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
        
        return clean.split(Regex("\\n{2,}"))
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 1 }
    }

    private fun parseAzw3(inputStream: InputStream): ParsedBook {
        return parseMobi(inputStream)
    }
    
    private fun extractParagraphs(html: String): List<String> {
        val text = stripHtml(html)
        return text.split(Regex("\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { cleanHtmlEntity(it) }
    }
    
    private fun stripHtml(html: String): String {
        return try {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            html.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ").replace(Regex("\\s+"), " ").trim()
        }
    }
    
    private fun cleanHtmlEntity(text: String): String {
        return text
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun saveCoverToCache(bookId: Long, bitmap: Bitmap): String {
        val cacheDir = File(context.cacheDir, "covers")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val coverFile = File(cacheDir, "book_${bookId}_cover.png")
        FileOutputStream(coverFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return coverFile.absolutePath
    }

    fun saveBookToInternalStorage(uri: Uri, fileName: String): String {
        val booksDir = File(context.filesDir, "books")
        if (!booksDir.exists()) booksDir.mkdirs()
        val destFile = File(booksDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile.absolutePath
    }
}