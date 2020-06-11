package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getBest
import portfolio.getRandomPopulation
import selector.Selector

/**
 * An evolutionary algorithm constructed from the given operation classes (Mutator, Selectors,
 * FitnessMetric, and Populator) and various parameters (asset universe, population size, etc.)
 */
class DefaultEvolver : Evolver {

    /**
     * "Names" the given Portfolio by reassociating the assets in the Portfolio with their respective String names,
     * in the given list of asset names. i-th asset corresponds to the i-th asset name. Returns a list of asset names
     * to their respective weights, from the original Portfolio.
     */
    private fun namePortfolio(portfolio: Portfolio, assets: List<String>): List<Pair<String, Double>> {
        return portfolio.allocations.map { Pair(assets[it.asset], it.amount) }
    }

    /**
     * Returns if the current session of evolutionary search has concluded, i.e. if the number of iterations has been
     * reached or if a Portfolio has been found that scores above the set threshold
     * @param fitnessScores: fitness scores used to determine if the termination threshold has been reached
     * @param curIteration: current iteration of the search
     * @param iterations: maximum number of iterations allowed for search
     * @param terminateThreshold: value to determine if a suitable Portfolio has been found
     * @return: if current search session has concluded
     */
    private fun isFinished(fitnessScores: List<Double>, curIteration: Int, iterations: PositiveInt?,
                           terminateThreshold: Double?): Boolean {
        if ((iterations != null && curIteration == iterations.num)
            || (terminateThreshold != null && fitnessScores.any { it > terminateThreshold })) {
            return true
        }
        return false
    }

    override fun findSolution(assets: List<String>, selector: Selector, assetMutator: AssetMutator,
                              weightMutator: WeightMutator, populator: Populator, fitnessMetric: FitnessMetric,
                              popSize: Int, portfolioSize: Int, iterations: PositiveInt?, terminateThreshold: Double?,
                              collect: Boolean?): List<Pair<String, Double>> {
        if (popSize < 1 || portfolioSize < 1 || (terminateThreshold != null && terminateThreshold < 0)) {
            throw IllegalArgumentException("population size, portfolio size, and termination threshold must be > 0")
        } else if (portfolioSize > assets.size) {
            throw IllegalArgumentException("portfolio size must be less than number of available assets")
        } else if (iterations == null && terminateThreshold == null) {
            throw IllegalArgumentException("must provide # of iterations or a termination threshold, or both")
        }

        var iterCount = 0
        var population: List<Portfolio> = getRandomPopulation(assets.size, popSize, portfolioSize)
        while (true) {
            val scores = fitnessMetric.evaluate(population)
            print("Generation %d, Score: %f, Avg Score: %f\n".format(iterCount, scores.max(), scores.average()))
            iterCount++
            if (isFinished(scores, iterCount, iterations, terminateThreshold)) {
                break
            }
            population = populator.populate(
                weightMutator.mutateWeights(
                    assetMutator.mutateAssets(
                        selector.prune(population, scores))),
                popSize)
        }
        return namePortfolio(getBest(population, fitnessMetric), assets)
    }
}