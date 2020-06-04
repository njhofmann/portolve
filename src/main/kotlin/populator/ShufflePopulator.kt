package populator

import MaxAllocation
import portfolio.Allocation
import portfolio.Portfolio
import kotlin.random.Random

class ShufflePopulator(assetUniverse: Int, maxAllocation: MaxAllocation?) :
    AbstractPopulator(assetUniverse, maxAllocation) {

    override fun createChild(a: Portfolio, b: Portfolio): Pair<Portfolio, Portfolio> {
        checkSameSize(a, b)
        val first: MutableList<Allocation> = ArrayList(a.size)
        val firstMask = BooleanArray(a.size)
        val second: MutableList<Allocation> = ArrayList(a.size)
        val secondMask = BooleanArray(a.size)
        for (i in 0 until a.size) {
            if (Random.nextBoolean()) {
                first[i] = a.allocations[i]
                firstMask[i] = true
                second[i] = a.allocations[i]
                secondMask[i] = true
            } else {
                first[i] = b.allocations[i]
                firstMask[i] = false
                second[i] = a.allocations[i]
                secondMask[i] = true
            }
        }
        return repairChildren(first, second, firstMask, secondMask, a, b)
    }

    override fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio> {
        return super.populate(population, targetSize, null)
    }
}