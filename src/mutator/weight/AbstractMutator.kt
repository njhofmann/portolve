package mutator.weight

import normAllocs
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.random.Random

abstract class AbstractMutator(private val mutationRate: Double) : WeightMutator {

    init {
        if (isNotUnitValue(mutationRate)) {
            throw IllegalArgumentException("mutation rate must be in (0, 1)")
        }
    }

    abstract fun getMutationValue(): Double

    protected fun isNotUnitValue(value: Double): Boolean {
        return !(0 < value && value < 1.0)
    }

    private fun mutatePortfolio(portfolio: Portfolio): Portfolio {
        val mutatedAllocs = portfolio.allocations.map {
            val mutate = Random.nextDouble(0.0, 1.0) < mutationRate
            val delta = if (mutate) getMutationValue() else 0.0
            Allocation(it.asset, it.amount + delta)
        }
        return DefaultPortfolio(normAllocs(mutatedAllocs))
    }

    override fun mutate(population: List<Portfolio>): List<Portfolio> {
        return population.map { mutatePortfolio(it) }
    }
}