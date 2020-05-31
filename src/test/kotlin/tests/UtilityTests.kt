package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertThrows
import randomItemNoReplacement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse

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