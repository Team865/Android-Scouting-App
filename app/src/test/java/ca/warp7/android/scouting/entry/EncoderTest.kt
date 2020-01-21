package ca.warp7.android.scouting.entry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EncoderTest {
    @Test
    fun toBase64Test() {
        assertEquals('A', toBase64(0))
        assertEquals('a', toBase64(26))
        assertEquals('0', toBase64(52))
        assertEquals('+', toBase64(62))
        assertEquals('/', toBase64(63))

        assertThrows<IllegalArgumentException> { toBase64(-1) }
        assertThrows<IllegalArgumentException> { toBase64(64) }
    }

    @Test
    fun fromBase64Test() {
        assertEquals(0, fromBase64('A'.toInt()))
        assertEquals(26, fromBase64('a'.toInt()))
        assertEquals(52, fromBase64('0'.toInt()))
        assertEquals(62, fromBase64('+'.toInt()))
        assertEquals(63, fromBase64('/'.toInt()))
        assertThrows<IllegalArgumentException> { fromBase64(-1) }
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun encoderTest() {
        val dp1 = DataPoint(0, 0, 0.0)
        assertEquals("AAAA", encodeDataPoint(dp1))
        val dp2 = DataPoint(27, 0, 0.0)
        assertEquals("bAAA", encodeDataPoint(dp2))

        val dp3 = DataPoint(0, 4, 0.0)
        assertEquals("AQAA", encodeDataPoint(dp3))

        val dp4 = DataPoint(0, 4, 2.0)
        assertEquals("AQDI", encodeDataPoint(dp4))
    }
}