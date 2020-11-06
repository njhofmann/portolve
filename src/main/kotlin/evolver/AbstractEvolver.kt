package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getRandomPopulation
import selector.Selector
import javax.sound.sampled.Port

/**
 * Main abstract implementation of the Evolver interface, provides all the main functionality that is likely needed for
 * the interface aside from a specific implementation for how each new generation should be produced
 */
abstract class AbstractEvolver(
    private val assetsCount: Int, protected val selector: Selector, protected val assetMutator: AssetMutator,
    protected val weightMutator: WeightMutator, protected val populator: Populator,
    protected val fitnessMetric: FitnessMetric, protected val popSize: Int, val portfolioSize: Int,
    private val iterations: PositiveInt?, private val terminateThreshold: Double?
) : Evolver {

    init {
        if (popSize < 1 || portfolioSize < 1 || (terminateThreshold != null && terminateThreshold < 0)) {
            throw IllegalArgumentException("population size, portfolio size, and termination threshold must be > 0")
        } else if (portfolioSize > assetsCount) {
            throw IllegalArgumentException("portfolio size must be less than number of available assets")
        } else if (iterations == null && terminateThreshold == null) {
            throw IllegalArgumentException("must provide # of iterations or a termination threshold, or both")
        }
    }

    override fun iterator(): Iterator<List<Pair<Portfolio, Double>>> {

        return object : Iterator<List<Pair<Portfolio, Double>>> {

            private var iterCount: Int = 0

            private var population: List<Portfolio> = getRandomPopulation(assetsCount, popSize, portfolioSize)

            private var latestFitnessScores: List<Double> = DoubleArray(population.size) { 0.0 }.toList()

            private fun assignScoresToPortfolios(portfolios: List<Portfolio>): List<Pair<Portfolio, Double>> {
                latestFitnessScores = fitnessMetric.evaluate(portfolios)
                return pairScores(portfolios, latestFitnessScores)
            }

            private fun pairScores(portfolios: List<Portfolio>, scores: List<Double>): List<Pair<Portfolio, Double>> {
                return (portfolios.indices).map { Pair(portfolios[it], scores[it]) }
            }

            override fun hasNext(): Boolean {
                return !((iterations != null && iterCount == iterations.num)
                        || (terminateThreshold != null && latestFitnessScores.any { it > terminateThreshold }))
            }

            override fun next(): List<Pair<Portfolio, Double>> {
                iterCount++
                if (iterCount == 0) return assignScoresToPortfolios(population)

                val results = newGeneration(population, latestFitnessScores)
                latestFitnessScores = results.second
                return pairScores(results.first, latestFitnessScores)
            }

        }
    }

    abstract fun newGeneration(population: List<Portfolio>, oldScores: List<Double>) : Pair<List<Portfolio>, List<Double>>
}


