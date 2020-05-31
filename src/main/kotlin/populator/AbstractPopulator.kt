package populator

import normAllocs
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import randomItemNoReplacement
import java.util.*
import kotlin.math.ceil

abstract class AbstractPopulator(assetUniverse: Int) : Populator {

    private val assetUniverseSet: Set<Int>

    init {
        if (assetUniverse < 1) {
            throw IllegalArgumentException("asset universe must be > 0")
        }
        assetUniverseSet = (0 until assetUniverse).toSet()
    }

    private fun checkSizes(population: List<Portfolio>, targetSize: Int) {
        if (population.isEmpty() || targetSize < 0) {
            throw IllegalArgumentException(
                "require non-empty population and new population size > 0"
            )
        }
    }

    private fun isLarger(population: List<Portfolio>, targetSize: Int): Boolean {
        return population.size >= targetSize
    }

    private fun getUniqueParents(portfolios: List<Portfolio>): Pair<Portfolio, Portfolio> {
        val a = portfolios.random()
        var b = portfolios.random()
        while (a != b) {
            b = portfolios.random()
        }
        return Pair(a, b)
    }

    abstract fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio>

    private fun <T> unzipPairs(pairs: List<Pair<T, T>>): LinkedList<T> {
        val lists: LinkedList<T> = LinkedList()
        pairs.forEach { lists.add(it.first); lists.add(it.second) }
        return lists
    }

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
        val dupAssets = (assets.map { it.asset } - childAssets).toMutableSet()
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
                        print("asset universe not big enough to prevent duplicate assets in children")
                        newAsset = it.asset
                    }
                }
                Allocation(newAsset, it.amount)
            } else {
                it
            }
        }
    }

    private fun repairChild(child: List<Allocation>, unselectedParentAssets: MutableSet<Int>): Portfolio {
        return DefaultPortfolio(normAllocs(removeDupAssets(child, unselectedParentAssets)))
    }

    protected fun repairChildren(firstChild: List<Allocation>, secondChild: List<Allocation>, firstParent: Portfolio,
        secondParent: Portfolio): Pair<Portfolio, Portfolio> {
        val parentAssets = (firstParent.allocations.map { it.asset } + secondParent.allocations.map { it.asset }).toSet()
        val unselectedFirstChildAssets = (parentAssets - firstChild.map { it.asset }).toMutableSet()
        val unselectedSecondChildAssets = (parentAssets - secondChild.map { it.asset }).toMutableSet()
        return Pair(repairChild(firstChild, unselectedFirstChildAssets),
            repairChild(secondChild, unselectedSecondChildAssets))
    }

    protected fun populate(population: List<Portfolio>, targetSize: Int, checks: List<(List<Portfolio>) -> Unit>?):
            List<Portfolio> {
        checkSizes(population, targetSize)
        if (isLarger(population, targetSize)) {
            return population
        }

        checks?.forEach { it(population) }

        val breedCount = ceil((population.size - targetSize) / 2.0).toInt()
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