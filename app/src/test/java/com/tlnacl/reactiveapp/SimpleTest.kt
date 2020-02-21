package com.tlnacl.reactiveapp

import junit.framework.Assert.assertEquals
import org.junit.Test

class SimpleTest {
    @Test
    fun TestFilterRange() {
        val intRange = IntRange(5, 100).toList()

        assertEquals(getRelatedPhotos(intRange, 20), IntRange(15, 25).toList())
        assertEquals(getRelatedPhotos(intRange, 7), IntRange(5, 12).toList())
        assertEquals(getRelatedPhotos(intRange, 100), IntRange(95, 100).toList())
    }

    private fun getRelatedPhotos(photos: List<Int>, testInt: Int): List<Int> {
        if (photos.size < 10) return photos
        else {
            val photoIndex = photos.indexOf(testInt)
            val startIndex = if (photoIndex - 5 >= 0) photoIndex - 5 else 0
            val endIndex = if (photoIndex + 5 < photos.size - 1) photoIndex + 5 else photos.size - 1

            return photos.slice(IntRange(startIndex, endIndex))
        }
    }
}