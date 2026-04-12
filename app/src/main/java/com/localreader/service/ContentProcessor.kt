package com.localreader.service

import android.util.Log

object ContentProcessor {
    private const val TAG = "ContentProcessor"
    
    data class ReplaceRule(
        val pattern: String,
        val replacement: String,
        val isEnabled: Boolean = true
    )
    
    private val defaultTitleRules = listOf(
        ReplaceRule("\\s*第[一二三四五六七八九十\\d]+章.*", ""),
        ReplaceRule("^\\s+", "")
    )
    
    private val defaultContentRules = listOf(
        ReplaceRule("<script[^>]*>[\\s\\S]*?</script>", ""),
        ReplaceRule("<style[^>]*>[\\s\\S]*?</style>", ""),
        ReplaceRule("<nav[^>]*>[\\s\\S]*?</nav>", ""),
        ReplaceRule("<header[^>]*>[\\s\\S]*?</header>", ""),
        ReplaceRule("<footer[^>]*>[\\s\\S]*?</footer>", ""),
        ReplaceRule("<iframe[^>]*>[\\s\\S]*?</iframe>", ""),
        ReplaceRule("<form[^>]*>[\\s\\S]*?</form>", ""),
        ReplaceRule("<!--[\\s\\S]*?-->", ""),
        ReplaceRule("<br\\s*/?>", "\n"),
        ReplaceRule("</p>", "\n\n"),
        ReplaceRule("<p[^>]*>", "\n"),
        ReplaceRule("&nbsp;", " "),
        ReplaceRule("&amp;", "&"),
        ReplaceRule("&lt;", "<"),
        ReplaceRule("&gt;", ">"),
        ReplaceRule("&quot;", "\""),
        ReplaceRule("&#39;", "'"),
        ReplaceRule("&[a-z]+;", ""),
        ReplaceRule("<a[^>]*>([^<]*)</a>", "$1"),
        ReplaceRule("<img[^>]*alt=\"([^\"]*)\"[^>]*>", "[图片:$1]"),
        ReplaceRule("<img[^>]*>", "[图片]"),
        ReplaceRule("\\s+", " ")
    )
    
    fun processTitle(title: String): String {
        var result = title.trim()
        for (rule in defaultTitleRules) {
            if (rule.isEnabled) {
                try {
                    result = result.replace(Regex(rule.pattern, RegexOption.IGNORE_CASE), rule.replacement)
                } catch (e: Exception) {
                    Log.w(TAG, "Rule failed: ${e.message}")
                }
            }
        }
        return result.trim()
    }
    
    fun processContent(html: String): String {
        var result = html
        for (rule in defaultContentRules) {
            if (rule.isEnabled) {
                try {
                    result = result.replace(Regex(rule.pattern, RegexOption.IGNORE_CASE), rule.replacement)
                } catch (e: Exception) {
                    Log.w(TAG, "Rule failed: ${e.message}")
                }
            }
        }
        return result.trim()
    }
    
    fun removeEmptyLines(text: String): String {
        return text
            .replace(Regex("\n{3,}"), "\n\n")
            .replace(Regex("^[\\s]*$", RegexOption.MULTILINE), "")
            .trim()
    }
    
    fun splitToParagraphs(text: String, maxLength: Int = 5000): List<String> {
        val paragraphs = mutableListOf<String>()
        val lines = text.split("\n".toRegex())
        
        val current = StringBuilder()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                if (current.isNotEmpty()) {
                    paragraphs.add(current.toString().trim())
                    current.clear()
                }
            } else if (current.length + trimmed.length > maxLength) {
                if (current.isNotEmpty()) {
                    paragraphs.add(current.toString().trim())
                    current.clear()
                }
                if (trimmed.length > maxLength) {
                    var i = 0
                    while (i < trimmed.length) {
                        val end = minOf(i + maxLength, trimmed.length)
                        paragraphs.add(trimmed.substring(i, end))
                        i = end
                    }
                } else {
                    current.append(trimmed)
                }
            } else {
                if (current.isNotEmpty()) {
                    current.append("\n")
                }
                current.append(trimmed)
            }
        }
        
        if (current.isNotEmpty()) {
            paragraphs.add(current.toString().trim())
        }
        
        return paragraphs.ifEmpty { listOf("") }
    }
}