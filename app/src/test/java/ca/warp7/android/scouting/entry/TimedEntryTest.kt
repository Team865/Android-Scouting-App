package ca.warp7.android.scouting.entry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TimedEntryTest {
    @Test
    fun testEncode() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }
        assertEquals("M:T:S:R1:0::", entry.getEncoded())
    }

    @Test
    fun testNextIndex() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }
        entry.dataPoints.add(DataPoint(0, 0, 6.0))
        entry.dataPoints.add(DataPoint(0, 0, 7.0))
        entry.dataPoints.add(DataPoint(0, 0, 8.0))
        entry.dataPoints.add(DataPoint(0, 0, 10.0))

        assertEquals(2, entry.getNextIndex(7.5))
        assertEquals(3, entry.getNextIndex(8.0))
        assertEquals(3, entry.getNextIndex(9.0))
        assertEquals(1, entry.getNextIndex(0.0))
        assertEquals(4, entry.getNextIndex(10.0))
    }

    @Test
    fun testNextIndexSameTime() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }
        entry.dataPoints.add(DataPoint(0, 0, 7.0))
        entry.dataPoints.add(DataPoint(0, 0, 7.0))
        entry.dataPoints.add(DataPoint(0, 0, 8.0))

        assertEquals(1, entry.getNextIndex(7.0))
        assertEquals(2, entry.getNextIndex(7.1))
    }

    @Test
    fun testScoutName() {
        val entry = TimedEntry("M", "T", "Scout N", Board.R1, 0) { 0.0 }
        assertEquals("M:T:Scout_N:R1:0::", entry.getEncoded())
    }

    @Test
    fun testFocus() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }

        entry.dataPoints.add(DataPoint(0, 0, 6.0))
        entry.dataPoints.add(DataPoint(0, 0, 7.0))
        entry.dataPoints.add(DataPoint(0, 0, 8.0))
        entry.dataPoints.add(DataPoint(0, 0, 10.0))

        assertTrue(entry.isFocused(0, 6.5))
    }

    @Test
    fun testEmptyNextIndex() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }
        assertEquals(0, entry.getNextIndex(0.0))
    }
}