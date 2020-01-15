package ca.warp7.android.scouting.entry

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimedEntryTest {
    @Test
    fun testEncode() {
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { 0.0 }
        assertEquals("M:T:S:R1:0::", entry.getEncoded())
    }

    @Test
    fun testNextIndex() {
        var time = 7.5
        val entry = TimedEntry("M", "T", "S", Board.R1, 0) { time }
        entry.dataPoints.add(DataPoint(0, 0, 6.0))
        entry.dataPoints.add(DataPoint(0, 0, 7.0))
        entry.dataPoints.add(DataPoint(0, 0, 8.0))
        entry.dataPoints.add(DataPoint(0, 0, 10.0))
        assertEquals(1, entry.getNextIndex())

        time = 8.0
        assertEquals(2, entry.getNextIndex())

        time = 9.0
        assertEquals(2, entry.getNextIndex())

        time = 0.0
        assertEquals(0, entry.getNextIndex())

        time = 10.0
        assertEquals(3, entry.getNextIndex())
    }
}