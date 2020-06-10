package mutator

import AbstractRateAnnealer
import PositiveInt
import portfolio.Portfolio
import kotlin.random.Random

/**
 * Abstract class providing common utilities to any class implementation a mutation operation. Takes in a rate at which
 * to apply mutations, and two optional parameters to determine a linearly changing mutation rate - a final mutation
 * rate and the number of steps to between the starting and final mutation rates.
 * @param mutationRate: rate at which mutation is applied
 * @param finalMutationRate: optional param determining a final mutation rate
 * @param iterations: optional param giving the number of steps between the starting and final mutation rates
 */
abstract class AbstractMutator(mutationRate: Double, finalMutationRate: Double?, iterations: PositiveInt?):
    AbstractRateAnnealer(mutationRate, finalMutationRate, iterations) {

    /**
     * Returns if a mutation operator should be applied as per the given mutation threshold
     * @param mutateThreshold: threshold determining mutation
     * @return: if mutation should be applied
     */
    protected fun toMutate(mutateThreshold: Double): Boolean {
        return Random.nextDouble(0.0, 1.0) < mutateThreshold
    }

    /**
     * Mutation operator to possibly apply to the given Portfolio, as per the given mutation threshold
     * @param portfolio: Portfolio to mutate
     * @param mutateThreshold: threshold determining if mutation will be applied
     * @return: possibly mutate portfolio
     */
    protected abstract fun mutatePortfolio(portfolio: Portfolio, mutateThreshold: Double): Portfolio

    /**
     * Applies a mutation operator to each Portfolio in the given population
     * @param population: list of Portfolios to possibly mutate
     * @return: possibly mutated population
     */
    protected fun mutatePortfolios(population: List<Portfolio>): List<Portfolio> {
        val mutateThreshold = getPercentAtTick()
        return population.map { mutatePortfolio(it, mutateThreshold) }
    }
}