package portfolio

import equalDoubles
import java.util.*
import kotlin.math.abs

/**
 * Represents an immutable allocation of the ith asset in a portfolio.Portfolio
 */
class Allocation(val asset: Int, val amount: Double) {

    private var hashCode: Int? = null

    init {
        if (0 >= this.amount || this.amount >= 100) {
            throw IllegalArgumentException("amount of a Allocation must be in [0, 100]")
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Allocation) false else this.asset == other.asset && equalDoubles(this.amount, other.amount)
    }

    override fun hashCode(): Int {
        if (hashCode == null) {
            hashCode = Objects.hash(this.asset, this.amount)
        }
        return hashCode!!
    }
}