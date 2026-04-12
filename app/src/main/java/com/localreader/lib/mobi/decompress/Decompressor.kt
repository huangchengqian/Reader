package com.localreader.lib.mobi.decompress

interface Decompressor {

    fun decompress(data: ByteArray): ByteArray

}
