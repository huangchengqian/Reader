package com.localreader.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.localreader.data.model.Book
import com.localreader.lib.mobi.KF6Book
import com.localreader.lib.mobi.KF8Book
import com.localreader.lib.mobi.MobiBook
import com.localreader.lib.mobi.MobiReader
import com.localreader.lib.mobi.entities.TOC
import java.io.InputStream

class MobiParserService(private val context: Context) {
    private val TAG = "MobiParserService"
    
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var mobiBook: MobiBook? = null
    private var pfd: ParcelFileDescriptor? = null
    
    data class MobiParsedBook(
        val title: String,
        val author: String,
        val coverBitmap: Bitmap?,
        val chapters: List<MobiChapter>
    )
    
    data class MobiChapter(
        val title: String,
        val paragraphs: List<String>
    )
    
    fun parseBook(uri: String): MobiParsedBook {
        Log.d(TAG, "Parsing MOBI: $uri")
        
        try {
            pfd = context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
                ?: return MobiParsedBook("Error", "Cannot open file", null, emptyList())
            
            fileDescriptor = pfd
            mobiBook = MobiReader().readMobi(pfd!!)
            
            val book = mobiBook!!
            val title = book.metadata.title.ifBlank { "Unknown" }
            val author = book.metadata.author.joinToString(", ").ifBlank { "Unknown" }
            
            var coverBitmap: Bitmap? = null
            try {
                val cover = book.getCover()
                if (cover != null && cover.isNotEmpty()) {
                    coverBitmap = BitmapFactory.decodeByteArray(cover, 0, cover.size)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Cover load failed: ${e.message}")
            }
            
            val chapters = buildChapters(book)
            Log.d(TAG, "Parsed: title='$title', chapters=${chapters.size}")
            
            return MobiParsedBook(title, author, coverBitmap, chapters)
            
        } catch (e: Exception) {
            Log.e(TAG, "Parse failed: ${e.message}", e)
            return MobiParsedBook("Parse Error", e.message ?: "Unknown", null, emptyList())
        }
    }
    
    private fun buildChapters(book: MobiBook): List<MobiChapter> {
        val chapters = mutableListOf<MobiChapter>()
        
        // Try TOC-based extraction first
        val tocChapters = extractFromTOC(book)
        if (tocChapters.isNotEmpty()) {
            Log.d(TAG, "TOC extraction yielded ${tocChapters.size} chapters")
            return tocChapters
        }
        
        // Fallback: try sections directly
        val sectionChapters = extractFromSections(book)
        if (sectionChapters.isNotEmpty()) {
            Log.d(TAG, "Section extraction yielded ${sectionChapters.size} chapters")
            return sectionChapters
        }
        
        // Final fallback: read text records directly from PDB
        val directChapters = extractFromTextRecords(book)
        if (directChapters.isNotEmpty()) {
            Log.d(TAG, "Direct record extraction yielded ${directChapters.size} chapters")
            return directChapters
        }
        
        return listOf(MobiChapter("Content", listOf("Unable to extract text")))
    }
    
    private fun extractFromTOC(book: MobiBook): List<MobiChapter> {
        val chapters = mutableListOf<MobiChapter>()
        
        when (book) {
            is KF8Book -> {
                val toc = book.toc ?: return emptyList()
                if (toc.isEmpty()) return emptyList()
                
                toc.forEach { ref ->
                    val href = ref.href ?: ""
                    if (href.isNotEmpty() && ref.subitems.isNullOrEmpty()) {
                        try {
                            val chapterTitle = ref.label ?: "Chapter"
                            val section = book.getSectionByHref(href)
                            if (section != null) {
                                val text = book.getSectionText(section)
                                if (text.isNotBlank()) {
                                    val paragraphs = splitToParagraphs(text)
                                    if (paragraphs.isNotEmpty()) {
                                        chapters.add(MobiChapter(chapterTitle, paragraphs))
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "TOC chapter error: ${e.message}")
                        }
                    }
                }
            }
            is KF6Book -> {
                val toc = book.toc ?: return emptyList()
                if (toc.isEmpty()) return emptyList()
                
                toc.forEach { ref ->
                    val href = ref.href ?: ""
                    if (href.isNotEmpty() && ref.subitems.isNullOrEmpty()) {
                        try {
                            val chapterTitle = ref.label ?: "Chapter"
                            val section = book.getSectionByHref(href)
                            if (section != null) {
                                val text = book.getSectionText(section)
                                if (text.isNotBlank()) {
                                    val paragraphs = splitToParagraphs(text)
                                    if (paragraphs.isNotEmpty()) {
                                        chapters.add(MobiChapter(chapterTitle, paragraphs))
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "TOC chapter error: ${e.message}")
                        }
                    }
                }
            }
        }
        
        return chapters
    }
    
    private fun extractFromSections(book: MobiBook): List<MobiChapter> {
        val chapters = mutableListOf<MobiChapter>()
        
        when (book) {
            is KF8Book -> {
                val sections = book.sections
                if (sections.isEmpty()) return emptyList()
                
                sections.forEachIndexed { index, section ->
                    try {
                        if (section.href.isNotEmpty()) {
                            val text = book.getSectionText(section)
                            if (text.isNotBlank()) {
                                val paragraphs = splitToParagraphs(text)
                                if (paragraphs.isNotEmpty()) {
                                    val title = "Section ${index + 1}"
                                    chapters.add(MobiChapter(title, paragraphs))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Section error: ${e.message}")
                    }
                }
            }
            is KF6Book -> {
                val sections = book.sections
                if (sections.isEmpty()) return emptyList()
                
                sections.forEachIndexed { index, section ->
                    try {
                        val text = book.getSectionText(section)
                        if (text.isNotBlank()) {
                            val paragraphs = splitToParagraphs(text)
                            if (paragraphs.isNotEmpty()) {
                                val title = "Section ${index + 1}"
                                chapters.add(MobiChapter(title, paragraphs))
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Section error: ${e.message}")
                    }
                }
            }
        }
        
        return chapters
    }
    
    private fun extractFromTextRecords(book: MobiBook): List<MobiChapter> {
        val chapters = mutableListOf<MobiChapter>()
        
        try {
            val inputStream = book.getTextRecordInputStream()
            val buffer = ByteArray(1024 * 1024) // 1MB buffer
            val bytesRead = inputStream.read(buffer)
            
            if (bytesRead > 0) {
                val text = String(buffer, 0, bytesRead, book.charset).trim()
                Log.d(TAG, "Direct read got $bytesRead bytes")
                
                if (text.isNotBlank() && text.length > 10) {
                    val paragraphs = splitToParagraphs(text)
                    if (paragraphs.isNotEmpty()) {
                        // Split into chunks of ~50 paragraphs
                        paragraphs.chunked(50).forEachIndexed { index, paras ->
                            val title = "Part ${index + 1}"
                            chapters.add(MobiChapter(title, paras))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Direct extraction failed: ${e.message}", e)
        }
        
        return chapters
    }
    
    private fun splitToParagraphs(text: String): List<String> {
        var clean = text
        try {
            clean = android.text.Html.fromHtml(text, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            clean = text.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ")
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
    
    fun close() {
        try {
            fileDescriptor?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Close failed: ${e.message}")
        }
    }
}