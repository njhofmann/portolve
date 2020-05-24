package populator

import normalize
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio

class MultiPointPopulator(private val crossoverPoints: Int) : AbstractPopulator() {

    init {
        if (crossoverPoints < 1) {
            throw IllegalArgumentException("# of crossover points must be > 0")
        }
    }

    private fun invalidPopulation(portfolios: List<Portfolio>): Unit {
        if (portfolios.any { it.size % crossoverPoints != 0 }) {
            throw IllegalArgumentException(("population contains a portfolio with that does not" +
                    "factor %d crossover points").format(crossoverPoints))
        }
    }


    override fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio> {
        checkSameSize(a, b)
        val childA: MutableList<Allocation> = ArrayList()
        val childB: MutableList<Allocation> = ArrayList()
        val sectionSize = a.size / crossoverPoints // should be int
        for (i in 0..crossoverPoints) {
            val idx = sectionSize * i
            val aSection = a.allocations.subList(idx, idx + sectionSize)
            val bSection = b.allocations.subList(idx, idx + sectionSize)
            val isEven = i % 2 == 0
            childA.addAll(if (isEven) aSection else bSection)
            childB.addAll(if (isEven) bSection else aSection)
        }

        return Pair(DefaultPortfolio(normAllocs(childA)), DefaultPortfolio(normAllocs(childB)))
    }

    override fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio> {
        // TODO fix me
        val checks: MutableList<(List<Portfolio>) -> Unit> = ArrayList()
        checks.add { ::invalidPopulation }
        return super.populate(population, targetSize, checks)
    }
}