# Legado Reference App Implementation Document

This document provides a comprehensive implementation blueprint for an ebook reader project modeled after the Legado reference app analysis.

---

## 1) Executive Summary

- Legado is an open-source Android ebook reader that supports:
  - EPUB (EPUB 2/3 with TOC)
  - MOBI (KF6/KF8 variants)
  - UMD (Universal Manga Dialect)
  - PDF (text pages rendered via PdfRenderer)
  - TXT (plain text with TOC rules)

- Core design goal: Provide a flexible, extensible reading experience by delegating content-format handling to per-format parsers, while exposing a unified content/TOC API to the UI layer.

- Architecture: Layered with clear separation between data parsing, data provisioning, and UI/presentation.

---

## 2) Architecture Overview

### Data Flow

```
LocalBook (data model) → Format Parsers
  ├── EpubFile → EPubBook (epublib)
  ├── MobiFile → MobiBook (KF6/KF8)
  ├── PdfFile → PdfRenderer
  ├── UmdFile → UmdBook
  └── TextFile → TOC/Text content

Each Parser provides:
  - getChapterList(book) → List<BookChapter>
  - getContent(book, chapter) → String?
  - getImage(book, href) → InputStream?
  - upBookInfo(book)
```

### UI Layer

- ReadView: Central reader container
- PageView: Single page render surface
- PageDelegate: Manages paging/animations
  - HorizontalPageDelegate: Basic swipe
  - SlidePageDelegate: Slide animation
  - SimulationPageDelegate: Curl/flip effect
  - ScrollPageDelegate: Vertical scroll
  - CoverPageDelegate: Cover flip

---

## 3) Component-by-Component Analysis

### 3.1 Book Parsing Layer

**Core abstractions:**

1. **LocalBook** - Entry point, delegates to format-specific parsers
2. **BaseLocalBookParse** - Interface defining:
   - `getChapterList(book: Book): List<BookChapter>`
   - `getContent(book: Book, chapter: BookChapter): String?`
   - `getImage(book: Book, href: String): InputStream?`
   - `upBookInfo(book: Book)`

3. **Per-format parsers:**
   - **EpubFile**: Uses epublib (EpubReader) for EPUB parsing
   - **MobiFile**: Uses MobiReader with KF6Book/KF8Book support
   - **PdfFile**: Uses Android PdfRenderer for PDF rendering
   - **UmdFile**: Uses UmdReader for UMD parsing
   - **TextFile**: Handles TXT with TOC rules and encoding detection

**Key patterns:**
- Companion objects implementing shared interface
- Per-book parser caching to avoid re-parsing
- Centralized IO via BookHelp

### 3.2 Reader UI Layer

**Components:**
- ReadView.kt - Central reader container
- PageView.kt - Single page surface
- PageDelegate.kt - Animation controller
- ReadBookActivity.kt - Main reading activity

**Paging flow:**
1. User touch → ReadView.onTouchEvent → PageDelegate handling
2. Gesture detection (drag/swipe) → determines page direction
3. PageDelegate renders prev/cur/next bitmaps
4. Animation plays (slide, cover, curl, scroll)

### 3.3 Text Layout Layer

**Text measuring (Conceptual):**
- **ChapterProvider**: Layout metrics (viewWidth, viewHeight, padding)
- **TextPageFactory**: Page navigation, creates page chunks
- **TextChapterLayout**: Converts text to pages using StaticLayout

---

## 4) Implementation Patterns

### Pattern 1: Per-format Interface

```kotlin
interface BaseLocalBookParse {
    fun upBookInfo(book: Book)
    fun getChapterList(book: Book): ArrayList<BookChapter>
    fun getContent(book: Book, chapter: BookChapter): String?
    fun getImage(book: Book, href: String): InputStream?
}
```

### Pattern 2: Parser Caching

```kotlin
companion object : BaseLocalBookParse {
    private var eFile: EpubFile? = null
    
    @Synchronized
    private fun getEFile(book: Book): EpubFile {
        if (eFile == null || eFile?.book?.bookUrl != book.bookUrl) {
            eFile = EpubFile(book)
        }
        return eFile!!
    }
}
```

### Pattern 3: PageDelegate Animation

```kotlin
abstract class PageDelegate {
    abstract fun onTouch(event: MotionEvent): Boolean
    abstract fun onDraw(canvas: Canvas)
    fun setBitmap(prev: Bitmap, cur: Bitmap, next: Bitmap)
}
```

---

## 5) File Format Handling Details

### EPUB
- Parsing: EpubReader().readEpubLazy(zipFile, "utf-8")
- TOC from tableOfContents
- Images via resource.data

### MOBI
- Parsing: MobiReader().readMobi() with KF6Book/KF8Book
- EXTH metadata handling
- Image extraction via getImage()

### PDF
- Android PdfRenderer for page rendering
- PAGE_SIZE constant for chapter grouping

### TXT
- Encoding detection (EncodingDetect)
- TOC rules (regex patterns)
- Chunked content loading

---

## 6) How to Implement

### Step 1: Define Interface

Create BaseLocalBookParse with per-format contract.

### Step 2: Implement Parsers

Implement each format parser with common interface.

### Step 3: Parser Caching

Maintain per-book cache to avoid re-parsing.

### Step 4: UI Integration

Create ReadView/PageView with PageDelegate pattern.

### Step 5: TOC Rules (TXT)

Implement dynamic TOC detection for TXT files.

---

## 7) Evidence Links

- Legado Repository: https://github.com/gedoor/legado
- BaseLocalBookParse: /refer/legado-master/app/src/main/java/io/legado/app/model/localBook/BaseLocalBookParse.kt
- EpubFile: /refer/legado-master/app/src/main/java/io/legado/app/model/localBook/EpubFile.kt
- MobiFile: /refer/legado-master/app/src/main/java/io/legado/app/model/localBook/MobiFile.kt
- PdfFile: /refer/legado-master/app/src/main/java/io/legado/app/model/localBook/PdfFile.kt
- LocalBook: /refer/legado-master/app/src/main/java/io/legado/app/model/localBook/LocalBook.kt
- ReadView (Page Delegate pattern): /refer/legado-master/app/src/main/java/io/legado/app/ui/book/read/page/delegate/

---

## 8) Current App vs Legado Comparison

| Feature | Current App | Legado |
|---------|-------------|--------|
| EPUB parsing | Basic epublib | Full epublib with lazy loading |
| MOBI parsing | Basic GBK support | KF6/KF8, EXTH metadata |
| PDF support | None | PdfRenderer |
| UMD support | None | UmdReader |
| TXT support | None | Full TOC rules |
| Encoding | Limited | Auto-detection |
| Page animations | Basic HorizontalPager | PageDelegate pattern |
| Text layout | Simple heuristics | StaticLayout-based |
| Caching | Limited | Per-book parser cache |

---

## 9) Next Steps

1. Add BaseLocalBookParse interface
2. Implement missing format parsers (PDF, UMD, TXT enhancements)
3. Improve page delegate pattern for better animations
4. Implement StaticLayout-based text measuring
5. Add parser caching

---

*Document generated based on Legado reference app analysis. Date: 2026*