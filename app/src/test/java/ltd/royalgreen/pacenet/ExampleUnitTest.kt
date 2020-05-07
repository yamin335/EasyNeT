package ltd.royalgreen.pacenet

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        fun printDays(values: Array<Int>) {
            val days = mapOf(0.0f to "Mon", 20.0f to "Tu", 40.0f to "Wed", 60.0f to "Th", 80.0f to "Fr", 100.0f to "Sa", 120.0f to "Su")

            for (i in values) {
                print(if (days.containsKey(i)) days[i] else "" )
            }
        }

        printDays(arrayOf(0, 20, 40, 60, 80, 100, 120))
        assertEquals(4, 2 + 2)
    }
}


