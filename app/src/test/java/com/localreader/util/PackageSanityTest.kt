package com.localreader.util

import com.localreader.lib.mobi.utils.and
import com.localreader.lib.mobi.utils.readByteArray
import com.localreader.lib.mobi.utils.readString
import com.localreader.lib.mobi.utils.readUInt16
import com.localreader.lib.mobi.utils.readUInt8
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Sanity tests for the JUnit 4 infrastructure and the pure-Kotlin helpers in
 * `com.localreader.lib.mobi.utils`. Runs on the JVM without an emulator.
 */
class PackageSanityTest {

    @Test
    fun arithmeticSmokeTest() {
        assertEquals("2 + 2 should equal 4", 4, 2 + 2)
        assertTrue(
            "kotlin.collections.emptyList should be empty",
            kotlin.collections.emptyList<String>().isEmpty()
        )
    }

    @Test
    fun byteAndShortAndExtensionsAreUnsigned() {
        val signedByte: Byte = -1
        val signedShort: Short = -1

        val byteAsUnsigned = signedByte and 0xFF
        val shortAsUnsigned = signedShort and 0xFFFF

        assertEquals("Byte -1 masked with 0xFF should be 255", 255, byteAsUnsigned)
        assertEquals("Short -1 masked with 0xFFFF should be 65535", 65535, shortAsUnsigned)
    }

    @Test
    fun intAndLongExtensionPreservesBits() {
        val value: Int = 0x12345678
        val masked: Long = value and 0xFFFFFFFFL

        assertEquals(0x12345678L, masked)
    }

    @Test
    fun byteBufferReadHelpersReturnExpectedSlice() {
        val payload = "LocalReader".toByteArray(StandardCharsets.UTF_8)
        val buffer = ByteBuffer.allocate(payload.size + 2).apply {
            put(0x00.toByte())
            put(payload)
            put(0xFF.toByte())
            flip()
        }

        assertEquals(0x00, buffer.readUInt8(0).toInt())
        assertEquals(0xFF, buffer.readUInt8(payload.size + 1).toInt())

        val slice = buffer.readByteArray(1, payload.size)
        assertTrue(
            "Slice should equal the original payload",
            slice.contentEquals(payload)
        )

        assertEquals("LocalReader", buffer.readString(1, payload.size))
    }

    @Test
    fun byteBufferReadUInt16IsUnsigned() {
        val buffer = ByteBuffer.allocate(2).apply {
            put(0xFF.toByte())
            put(0xFF.toByte())
            flip()
        }

        assertEquals(65535, buffer.readUInt16(0).toInt())
    }

    @Test
    fun byteBufferReadStringWithCharset() {
        val payload = "hello".toByteArray(StandardCharsets.US_ASCII)
        val buffer = ByteBuffer.wrap(payload)

        assertEquals(
            "hello",
            buffer.readString(0, payload.size, StandardCharsets.US_ASCII)
        )
    }
}
