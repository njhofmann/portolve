package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getRandomPopulation
import selector.Selector

/**
 * An evolutionary algorithm constructed from the given operation classes (Mutator, Selectors, FitnessMetric, and
 * Populator) and various parameters (asset universe, population size, etc.). First prunes the population then
 * repopulates
 */
class PruneThenPopulateEvolver(
    private val assets: List<String>, private val selector: Selector, private val assetMutator: AssetMutator,
    private val weightMutator: WeightMutator, private val populator: Populator,
    private val fitnessMetric: FitnessMetric, private val popSize: Int, val portfolioSize: Int,
    private val iterations: PositiveInt?, private val terminateThreshold: Double?
) : AbstractEvolver(assets, popSize, portfolioSize, iterations, terminateThreshold) {

    override fun iterator(): Iterator<List<Pair<Portfolio, Double>>> {

        return object : Iterator<List<Pair<Portfolio, Double>>> {

            private var iterCount: Int = 0

            private var population: List<Portfolio> = getRandomPopulation(assets.size, popSize, portfolioSize)

            private var latestFitnessScores: List<Double> = DoubleArray(population.size) { 0.0 }.toList()

            private fun assignScoresToPortfolios(portfolios: List<Portfolio>):
                    List<Pair<Portfolio, Double>> {
                latestFitnessScores = fitnessMetric.evaluate(portfolios)
                return (portfolios.indices).map { Pair(portfolios[it], latestFitnessScores[it]) }
            }

            override fun hasNext(): Boolean {
                return !((iterations != null && iterCount == iterations.num)
                        || (terminateThreshold != null && latestFitnessScores.any { it > terminateThreshold }))
            }

            override fun next(): List<Pair<Portfolio, Double>> {
                iterCount++
                if (iterCount == 0) return assignScoresToPortfolios(population)

                population = populator.populate(
                    targetSize = popSize,
                    population = weightMutator.mutateWeights(
                        assetMutator.mutateAssets(
                            selector.prune(population, latestFitnessScores))))
                return assignScoresToPortfolios(population)
            }

        }
    }
}