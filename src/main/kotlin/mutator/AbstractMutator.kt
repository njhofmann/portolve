package mutator

import AbstractRateAnnealer
import PositiveInt
import portfolio.Portfolio
import kotlin.random.Random

abstract class AbstractMutator(mutationRate: Double, finalMutationRate: Double?, iterations: PositiveInt?):
    AbstractRateAnnealer(mutationRate, finalMutationRate, iterations) {


    protected fun toMutate(): Boolean {
        return Random.nextDouble(0.0, 1.0) < getPercentAtTick()
    }

    protected abstract fun mutatePortfolio(portfolio: Portfolio): Portfolio

    protected fun mutatePortfolios(population: List<Portfolio>): List<Portfolio> {
        return population.map { mutatePortfolio(it) }
    }
}