package populator

import portfolio.Portfolio
import java.util.*
import kotlin.math.ceil

abstract class AbstractPopulator : Populator {

    protected fun checkSizes(population: List<Portfolio>, targetSize: Int) {
        if (population.isEmpty() || targetSize < 0) {
            throw IllegalArgumentException(
                    "require non-empty population and new population size > 0")
        }
    }

    protected fun isLarger(population: List<Portfolio>, targetSize: Int): Boolean {
        return population.size >= targetSize
    }

    protected fun getUniqueParents(portfolios: List<Portfolio>): Pair<Portfolio, Portfolio> {
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

    protected fun populate(population: List<Portfolio>, targetSize: Int,
                           checks: List<(List<Portfolio>) -> Unit>?): List<Portfolio> {
        checkSizes(population, targetSize)
        if (isLarger(population, targetSize)) {
            return population
        }

        checks?.forEach { it(population) }

        val breedCount = ceil((population.size - targetSize) / 2.0).toInt()
        val children: LinkedList<Portfolio> = unzipPairs((0..breedCount).map {
            val parents = getUniqueParents(population)
            createChild(parents.first, parents.second)
        })

        if (population.size + children.size > targetSize) {
            children.pop()
        }

        assert((population.size + children.size) == targetSize)
        return population + children
    }
}