package mutator.weight

import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import java.util.*
import kotlin.collections.HashSet

abstract class AbstractWeightMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?,
    private val maxAllocation: Double?) : WeightMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    abstract fun getMutationValue(): Double

    private fun mutateAllocation(allocation: Allocation, amount: Double): Allocation {
        return Allocation(allocation.asset, allocation.amount + amount)
    }

    private fun adjustOverflowingAllocations(portfolio: Portfolio, deltas: DoubleArray): DoubleArray {
        if (maxAllocation == null) {
            return deltas
        }

        var adjustedDeltas = deltas.toList()
        val skipIndices: MutableSet<Int> = HashSet()

        while (true) {
            val skipAdjusts = LinkedList<Double>()
            var toAdjust = false
            adjustedDeltas = adjustedDeltas.mapIndexed { idx, d ->
                val newAmount = deltas[idx] + portfolio.allocations[idx].amount
                val leftover = newAmount - maxAllocation!!
                if (leftover > 0.0) {
                    toAdjust = true
                    skipIndices.add(idx)
                    skipAdjusts.add(leftover)
                    maxAllocation - portfolio.allocations[idx].amount
                } else {
                    d
                }
            }

            if (!toAdjust) {
                return adjustedDeltas.toDoubleArray()
            }

            val adjustCount = adjustedDeltas.size - skipIndices.size
            skipAdjusts.forEach {
                val adjustAmount = it / adjustCount
                adjustedDeltas = adjustedDeltas.mapIndexed { idx, d ->
                    d + (if (skipIndices.contains(idx)) 0.0 else adjustAmount)
                }
            }
        }
    }

    override fun mutatePortfolio(portfolio: Portfolio): Portfolio {
        var deltas = DoubleArray(portfolio.size)
        (0 until portfolio.size).map { if (toMutate()) getMutationValue() else 0.0 }.forEachIndexed { idx, mutation ->
            val deltaAdjust = mutation / (portfolio.size - 1)
            deltas.forEachIndexed { jdx, delta -> delta + (if (idx == jdx) mutation else deltaAdjust) }
        }

        deltas = adjustOverflowingAllocations(portfolio, deltas)
        return DefaultPortfolio(portfolio.allocations.mapIndexed { idx, a -> mutateAllocation(a, deltas[idx]) } )
    }

    override fun mutateWeights(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}