package mutator.weight

import AbstractRateAnnealer
import isNotUnitValue
import mutator.AbstractMutator
import normAllocs
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.random.Random

abstract class AbstractWeightMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?) :
    WeightMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    abstract fun getMutationValue(): Double

    override fun mutateAllocation(allocation: Allocation): Allocation {
        return Allocation(allocation.asset, allocation.amount + getMutationValue())
    }

    override fun mutateWeights(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}