package populator

import MaxAllocation
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import randomItemNoReplacement
import unzipPairs
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.ceil

abstract class AbstractPopulator(assetUniverse: Int, private val maxAllocation: MaxAllocation?) : Populator {

    private val assetUniverseSet: Set<Int>

    init {
        if (assetUniverse < 1) {
            throw IllegalArgumentException("asset universe must be > 0")
        }
        assetUniverseSet = (0 until assetUniverse).toSet()
    }

    private fun checkSizes(population: List<Portfolio>, targetSize: Int) {
        if (population.isEmpty() || targetSize < 0) {
            throw IllegalArgumentException("require non-empty population and new population size > 0")
        }
    }

    private fun isLarger(population: List<Portfolio>, targetSize: Int): Boolean {
        return population.size >= targetSize
    }

    private fun getUniqueParents(portfolios: List<Portfolio>): Pair<Portfolio, Portfolio> {
        val a = portfolios.random()
        var b = portfolios.random()
        while (a == b) {
            b = portfolios.random()
        }
        return Pair(a, b)
    }

    abstract fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio>

    protected fun checkSameSize(a: Portfolio, b: Portfolio) {
        if (a.size != b.size) {
            throw IllegalArgumentException("portfolios must be of same size")
        }
    }

    private fun removeDupAssets(assets: List<Allocation>, unselectedAssets: MutableSet<Int>): List<Allocation> {
        val childAssets: MutableSet<Int> = assets.map { it.asset }.toMutableSet()
        if (assets.size == childAssets.size) {
            return assets
        }
        val dupAssets = assets.map { it.asset }.filter { !childAssets.remove(it) }.toMutableSet()
        val unselectedAssetUniverse = (assetUniverseSet - childAssets).toMutableSet()
        return assets.map {
            if (dupAssets.contains(it.asset)) {
                dupAssets.remove(it.asset)
                val newAsset: Int
                when {
                    unselectedAssets.isNotEmpty() -> {
                        newAsset = randomItemNoReplacement(unselectedAssets)
                        unselectedAssetUniverse.remove(newAsset)
                    }
                    unselectedAssetUniverse.isNotEmpty() -> {
                        newAsset = randomItemNoReplacement(unselectedAssetUniverse)
                    }
                    else -> {
                        println("WARNING: asset universe not big enough to prevent duplicate assets in children")
                        newAsset = it.asset
                    }
                }
                Allocation(newAsset, it.amount)
            } else {
                it
            }
        }
    }

    private fun repairChild(assets: List<Allocation>, mask: BooleanArray, unselectedAssets: MutableSet<Int>): Portfolio {
        return DefaultPortfolio(proportionalReallocation(removeDupAssets(assets, unselectedAssets), mask))
    }

    protected fun repairChildren(first: List<Allocation>, second: List<Allocation>, firstMask: BooleanArray,
                                 secondMask: BooleanArray, parentA: Portfolio, parentB: Portfolio):
            Pair<Portfolio, Portfolio> {
        val parentAssets = (parentA.allocations.map { it.asset } + parentB.allocations.map { it.asset }).toSet()
        val unselectedFirstAssets = (parentAssets - first.map { it.asset }).toMutableSet()
        val unselectedSecondAssets = (parentAssets - second.map { it.asset }).toMutableSet()
        val repairedFirst = repairChild(first, firstMask, unselectedFirstAssets)
        val repairedSecond = repairChild(second, secondMask, unselectedSecondAssets)
        return Pair(repairedFirst, repairedSecond)
    }

    private fun mergeAdjustedAssets(a: LinkedList<Pair<Allocation, Int>>, b: LinkedList<Pair<Allocation, Int>>): List<Allocation> {
        return (0 until (a.size + b.size)).map {
            (if (a.isNotEmpty() && a.peekFirst().second == it) a else b).pop().first
        }
    }

    private fun readjustAssets(assets: LinkedList<Pair<Allocation, Int>>, totalSize: Int): LinkedList<Pair<Allocation, Int>> {

        // TODO fix reallocation
        val reallocFactor = (assets.size * 1.0 / totalSize) / assets.map { it.first.amount }.sum()
        var adjustedAssets = assets.map { Pair(Allocation(it.first.asset, it.first.amount * reallocFactor), it.second) }

        if (maxAllocation == null) {
            return LinkedList(adjustedAssets)
        }

        val adjustedMaxAlloc = maxAllocation.num * reallocFactor
        val fullIndices: MutableSet<Int> = HashSet()
        while (true) {
            var toAdjust = false
            val leftovers = LinkedList<Double>()
            adjustedAssets = adjustedAssets.mapIndexed { idx, pair ->
                val adjustedAmount = reallocFactor * pair.first.amount
                if (adjustedAmount > adjustedMaxAlloc) {
                    leftovers.add(adjustedAmount - adjustedMaxAlloc)
                    fullIndices.add(idx)
                    toAdjust = true
                    Pair(Allocation(pair.first.asset, maxAllocation.num), pair.second)
                } else {
                    pair
                }
            }

            if (!toAdjust) {
                return LinkedList(adjustedAssets)
            }

            val adjustCount = assets.size - fullIndices.size
            leftovers.forEach {
                val adjustAmount = it / adjustCount
                adjustedAssets = adjustedAssets.mapIndexed { idx, pair ->
                    if (fullIndices.contains(idx)) {
                        pair
                    } else {
                        Pair(Allocation(pair.first.asset, pair.first.amount + adjustAmount), pair.second)
                    }
                }
            }
        }
    }

    /**
     * Proportionately reallocation the weights of the given list of Allocations, based on the percentage of Allocations
     * come from - i.e. if a parent contributed half of the Allocations in the list, the weights of those Allocations
     * are adjusted to make up 50% of the make up of the List. The given Boolean mask signals which parent contributed
     * the ith Allocation - true from parent A, false for B.
     * @param assets: list of Allocations to adjust
     * @param parentMask: Boolean mask signalling Allocation parentage
     * @return: assets with proportionately readjusted weights
     */
    private fun proportionalReallocation(assets: List<Allocation>, parentMask: BooleanArray): List<Allocation> {
        val firstAllocs = LinkedList<Pair<Allocation, Int>>()
        val secondAllocs = LinkedList<Pair<Allocation, Int>>()
        assets.forEachIndexed { idx, a -> (if (parentMask[idx]) firstAllocs else secondAllocs).add(Pair(a, idx)) }
        val readjustedFirst = readjustAssets(firstAllocs, assets.size)
        val readjustedSecond = readjustAssets(secondAllocs, assets.size)
        return mergeAdjustedAssets(readjustedFirst, readjustedSecond)
    }

    protected fun populate(population: List<Portfolio>, targetSize: Int, checks: List<(List<Portfolio>) -> Unit>?):
            List<Portfolio> {
        checkSizes(population, targetSize)
        if (isLarger(population, targetSize)) {
            return population
        }

        checks?.forEach { it(population) }

        val breedCount = ceil(abs((population.size - targetSize)) / 2.0).toInt()
        val children: LinkedList<Portfolio> = unzipPairs((0 until breedCount).map {
            val parents = getUniqueParents(population)
            createChild(parents.first, parents.second)
        })

        if (population.size + children.size > targetSize) {
            children.pop()
        }

        assert((population.size + children.size) == targetSize)
        return (population + children).shuffled()
    }
}