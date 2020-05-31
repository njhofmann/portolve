package populator

import normAllocs
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.random.Random

class ShufflePopulator : AbstractPopulator() {

    override fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio> {
        checkSameSize(a, b)
        val childA: MutableList<Allocation> = ArrayList(a.size)
        val childB: MutableList<Allocation> = ArrayList(a.size)
        for (i in 0..a.size) {
            val isTrue = Random.nextBoolean()
            childA[i] = if (isTrue) a.allocations[i] else b.allocations[i]
            childB[i] = if (isTrue) b.allocations[i] else a.allocations[i]
        }
        return repairChildren(childA, childB, a, b)
    }

    override fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio> {
        return super.populate(population, targetSize, null)
    }
}