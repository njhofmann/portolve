package mutator

import AbstractRateAnnealer
import PositiveInt
import portfolio.Portfolio
import kotlin.random.Random

abstract class AbstractMutator(mutationRate: Double, finalMutationRate: Double?, iterations: PositiveInt?):
    AbstractRateAnnealer(mutationRate, finalMutationRate, iterations) {


    protected fun toMutate(mutateThreshold: Double): Boolean {
        return Random.nextDouble(0.0, 1.0) < mutateThreshold
    }

    protected abstract fun mutatePortfolio(portfolio: Portfolio, mutateThreshold: Double): Portfolio

    protected fun mutatePortfolios(population: List<Portfolio>): List<Portfolio> {
        val mutateThreshold = getPercentAtTick()
        return population.map { mutatePortfolio(it, mutateThreshold) }
    }
}