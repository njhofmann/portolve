package tests

import AbstractRateAnnealer
import PositiveInt
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.lang.IllegalStateException

class TestRateAnnealer(startPercent: Double, endPercent: Double, iterations: Int) :
    AbstractRateAnnealer(startPercent, endPercent, PositiveInt(iterations)) {

    fun getNextPercent(): Double {
        return super.getPercentAtTick()
    }
}


class RateAnnealerTests {

    @Test
    fun `10 to 0 percent`() {
        val error = .0001
        val annealer = TestRateAnnealer(.1, 0.0, 5)
        assertEquals(0.1, annealer.getNextPercent(), error)
        assertEquals(0.075, annealer.getNextPercent(), error)
        assertEquals(0.05, annealer.getNextPercent(), error)
        assertEquals(0.025, annealer.getNextPercent(), error)
        assertEquals(0.0, annealer.getNextPercent(), error)
        assertThrows(IllegalStateException::class.java) { annealer.getNextPercent() }
    }
}