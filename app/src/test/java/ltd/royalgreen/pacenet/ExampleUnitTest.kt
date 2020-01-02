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

        fun largestRectangle(heights: Array<Int>, n: Int): Long {
            // Create an empty stack. The stack holds indexes of heights[] array
            // The buildings stored in stack are always in increasing order of their
            // heights.
            val stack: Stack<Int> = Stack()

            var maxArea = 0L // Initialize max area

            var top: Int // To store top of stack

            var areaWithTop: Int // To store area with top bar as the smallest bar

            // Run through all buildings of given heights
            var i = 0
            while (i < n) { // If this building is higher than the building on top stack, push it to stack
                if (stack.isEmpty() || heights[stack.peek()] <= heights[i]) stack.push(i++) else {
                    top = stack.pop() // pop and store the top index
                    // Calculate the area with heights[top] stack as smallest building
                    areaWithTop = heights[top] * if (stack.isEmpty()) i else i - stack.peek() - 1
                    // update max area, if needed
                    if (maxArea < areaWithTop) maxArea = areaWithTop.toLong()
                }
            }

            // Now pop the remaining buildings from stack and calculate area with every
            // popped building as the smallest building
            while (stack.isNotEmpty()) {
                top = stack.pop()
                areaWithTop = heights[top] * if (stack.isEmpty()) i else i - stack.peek() - 1
                if (maxArea < areaWithTop) maxArea = areaWithTop.toLong()
            }

            return maxArea
        }

        println(largestRectangle(arrayOf(6, 2, 5, 4, 5, 1, 6), 7))
        assertEquals(4, 2 + 2)
    }
}


