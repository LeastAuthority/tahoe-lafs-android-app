package org.tahoe.lafs.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QRCodeContentsTest {

    @Test
    fun testParseValid() {
        val result = QRCodeContents.parseContents("https://example.com someToken")
        assertTrue(result.isSuccess)
        assertEquals("https://example.com", result.getOrNull()?.url?.url.toString())
    }

    @Test
    fun testParseNonHttps() {
        val result = QRCodeContents.parseContents("http://example.com someToken")
        assertTrue(result.isFailure)
    }

    @Test
    fun testParseNoToken() {
        val result = QRCodeContents.parseContents("https://example.com")
        assertTrue(result.isFailure)
    }

    @Test
    fun testParseInvalid() {
        val result = QRCodeContents.parseContents("abcd")
        assertTrue(result.isFailure)
    }

}