package tests

import equalDoubles
import loadAssetReturns
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ReaderTests {

    @Test
    fun `read in test file correctly`() {
        val loadedAssets = loadAssetReturns("test.csv")
        assertEquals(5, loadedAssets.size)
        loadedAssets.forEachIndexed { idx, it ->
            assertEquals(4, it.second.size)
            assertEquals(idx + 1, it.first.toInt())
        }
    }
}