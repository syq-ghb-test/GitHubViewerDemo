object TimeUtil {
    fun formatTime(iso: String): String {
        return iso.substring(0, 10)
    }
}

import org.junit.Assert.*
import org.junit.Test

class TimeUtilTest {
    @Test
    fun testFormatTime() {
        val iso = "2024-05-10T15:00:00Z"
        val result = TimeUtil.formatTime(iso)
        assertEquals("2024-05-10", result)
    }
} 