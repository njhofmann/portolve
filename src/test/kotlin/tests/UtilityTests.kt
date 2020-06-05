package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import randomItemNoReplacement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import unzipPairs

class RandomItemNoReplacement {

    @Test
    fun `empty set`(){
        assertThrows(IllegalArgumentException::class.java) {
            randomItemNoReplacement(HashSet<Int>())
        }
    }

    @Test
    fun `removed item not present`() {
        val items = (0..9).toMutableSet()
        assertEquals(10, items.size)
        val selectedItem = randomItemNoReplacement(items)
        assertEquals(9, items.size)
        assertFalse(items.contains(selectedItem))
    }
}

class UnzipTests {

    @Test
    fun `unzip one pair`() {
        val pairs = listOf(Pair(1, 2))
        val ints = unzipPairs(pairs)
        assertEquals(2, ints.size)
        assertEquals(1, ints[0])
        assertEquals(2, ints[1])
    }

    @Test
    fun `unzip multiple pairs`() {
        val pairs = listOf(Pair(1, 2), Pair(3, 4), Pair(5, 6))
        val ints = unzipPairs(pairs)
        assertEquals(6, ints.size)
        ints.forEach {
            assertEquals(it, ints[it - 1])
        }
    }
}