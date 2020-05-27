package mutator

import AbstractRateAnnealer
import normAllocs
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.random.Random

abstract class AbstractMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?):
    AbstractRateAnnealer(mutationRate, finalMutationRate, iterations) {


    private fun toMutate(): Boolean {
        return Random.nextDouble(0.0, 1.0) < getPercentAtTick()
    }

    protected abstract fun mutateAllocation(allocation: Allocation): Allocation

    protected fun mutatePortfolio(portfolio: Portfolio): Portfolio {
        val mutatedAllocs = portfolio.allocations.map {
            if (toMutate()) mutateAllocation(it) else it
        }
        return DefaultPortfolio(normAllocs(mutatedAllocs))
    }

    protected fun mutatePortfolios(population: List<Portfolio>): List<Portfolio> {
        return population.map { mutatePortfolio(it) }
    }
}