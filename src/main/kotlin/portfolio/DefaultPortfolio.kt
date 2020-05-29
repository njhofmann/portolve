package portfolio

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.random.Random

/**
 * Default implementation of the Portfolio interface, enforcing the following constraints:
 * - sum of the weights of this Portfolio's assets = 1
 * - size of this Portfolio is always constant
 * - weight of any asset never exceeds a maximum threshold  TODO
 */
class DefaultPortfolio : Portfolio {

    /**
     * Number of assets in this Portfolio
     */
    override val size: Int

    /**
     * Allocations making up this Porfolio
     */
    override val allocations: List<Allocation>

    /**
     * Creates a new random, starting portfolio.DefaultPortfolio by select "portfolioSize" assets from the
     * "numOfAssets" that make of the universe of selectable assets. The ith number in [0,
     * numOfAssets) corresponds to the ith asset. Each selected asset is then assigned a random
     * weight such at the number
     * @param numOfAssets: number of assets to select from
     * @param portfolioSize: number of assets in this Portfolio
     */
    constructor(numOfAssets: Int, portfolioSize: Int) {
        if (numOfAssets < 1 || portfolioSize < 1 || numOfAssets < portfolioSize) {
            throw IllegalArgumentException("0 < portfolio size <= size of asset universe")
        }
        this.size = portfolioSize
        this.allocations = this.startingAllocations(numOfAssets)
    }

    /**
     * Creates a new Portfolio from an existing list of Allocations
     */
    constructor(allocations: List<Allocation>) {
        // TODO constraint checks
        if (allocations.isEmpty()) {
            throw IllegalArgumentException("must have at least one asset in this Portfolio")
        }
        else if (!sumToOne(allocations)) {
            throw IllegalArgumentException("Allocation weights in a Portfolio must sum to 1")
        }
        else if (!uniqueAllocations(allocations)) {
            throw IllegalArgumentException("given allocations must contain unique assets")
        }
        this.size = allocations.size
        this.allocations = allocations
    }

    private fun sumToOne(allocations: List<Allocation>): Boolean {
        val allocationsSum: Double = allocations.map { it.amount }.sum()
        return abs(allocationsSum - 1.0) < .0001
    }

    private fun uniqueAllocations(allocations: List<Allocation>): Boolean {
        return allocations.map { it.asset }.toSet().size == allocations.size
    }

    /**
     * Creates a starting list of Allocations consisting of 'numOfAssets' random assets and weights
     */
    private fun startingAllocations(numOfAssets: Int): List<Allocation> {
        // get unique assets
        val selectedIndices = TreeSet<Int>()
        var index: Int
        for (i in 0..size) {
            while (true) {
                index = Random.Default.nextInt(0, numOfAssets)
                if (index !in selectedIndices) {
                    selectedIndices.add(index)
                    break
                }
            }
        }

        // get random weights
        var weights = DoubleArray(size) { _ -> Random.Default.nextDouble(0.0, 1.0)}.toList()
        val weightSum = weights.sum()
        weights = weights.map { it / weightSum }

        // pair assets and weights into a Allocations
        val randomAllocation = ArrayList<Allocation>(size)
        selectedIndices.forEachIndexed { idx, selected ->
            randomAllocation.set(idx, Allocation(selected, weights.get(idx)))
        }
        return randomAllocation
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Portfolio) {
            return false
        }
        else {
            return this.allocations.toSet() == other.allocations.toSet()
        }
    }

    override fun hashCode(): Int {
        return this.allocations.hashCode()
    }
}