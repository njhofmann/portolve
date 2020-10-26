package populator

import MaxAllocation
import portfolio.Allocation
import portfolio.Portfolio
import java.util.*
import kotlin.collections.ArrayList

class MultiPointPopulator(private val crossoverPoints: Int, assetUniverse: Int, maxAllocation: MaxAllocation?) :
    AbstractPopulator(assetUniverse, maxAllocation) {

    init {
        if (crossoverPoints < 1) {
            throw IllegalArgumentException("# of crossover points must be > 0")
        }
    }

    private fun invalidPopulation(portfolios: List<Portfolio>) {
        if (portfolios.any { it.size % crossoverPoints != 0 }) {
            throw IllegalArgumentException(
                "population contains a portfolio with that does not factor ${crossoverPoints} crossover points"
            )
        }
    }

    override fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio> {
        checkSameSize(a, b)
        val first: MutableList<Allocation> = LinkedList()
        val firstMask = LinkedList<Boolean>()
        val second: MutableList<Allocation> = LinkedList()
        val secondMask = LinkedList<Boolean>()
        val sectionSize = a.size / crossoverPoints // should be int
        for (i in 0 until crossoverPoints) {
            val idx = sectionSize * i
            val endIdx = idx + sectionSize
            val aSection = a.allocations.subList(idx, endIdx)
            val bSection = b.allocations.subList(idx, endIdx)

            val idxRange = (idx until endIdx)
            if (i % 2 == 0) {
                first.addAll(aSection)
                firstMask.addAll(idxRange.map { true })
                second.addAll(bSection)
                secondMask.addAll(idxRange.map { false })
            } else {
                first.addAll(bSection)
                firstMask.addAll(idxRange.map { false })
                second.addAll(aSection)
                secondMask.addAll(idxRange.map { true })
            }
        }
        return repairChildren(first, second, firstMask.toBooleanArray(), secondMask.toBooleanArray(), a, b)
    }

    override fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio> {
        val checks: MutableList<(List<Portfolio>) -> Unit> = ArrayList()
        checks.add(::invalidPopulation)
        return super.populate(population, targetSize, checks)
    }
}