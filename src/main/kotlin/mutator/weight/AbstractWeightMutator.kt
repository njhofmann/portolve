package mutator.weight

import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio

abstract class AbstractWeightMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?,
    maxAllocation: Double?) : WeightMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    abstract fun getMutationValue(): Double

    private fun mutateAllocation(allocation: Allocation, amount: Double): Allocation {
        return Allocation(allocation.asset, allocation.amount + amount)
    }

    private fun adjustOverflowingAllocations(portfolio: Portfolio, deltas: DoubleArray): DoubleArray {
        deltas.mapIndexed { idx, d ->

        }
    }

    override fun mutatePortfolio(portfolio: Portfolio): Portfolio {
        val deltas = DoubleArray(portfolio.size)
        portfolio.allocations.map { if (toMutate()) getMutationValue() else 0.0 }.forEachIndexed { idx, mutation ->
            val deltaAdjust = mutation / (portfolio.size - 1)
            deltas.forEachIndexed { jdx, delta -> delta + (if (idx == jdx) mutation else deltaAdjust) }
        }
        return DefaultPortfolio(portfolio.allocations.mapIndexed { idx, a -> mutateAllocation(a, deltas[idx]) } )
    }

    override fun mutateWeights(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}