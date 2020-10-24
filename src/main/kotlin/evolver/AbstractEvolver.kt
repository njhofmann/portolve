package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getRandomPopulation
import selector.Selector

abstract class AbstractEvolver(
    private val assets: List<String>, protected val selector: Selector, protected val assetMutator: AssetMutator,
    protected val weightMutator: WeightMutator, protected val populator: Populator,
    private val fitnessMetric: FitnessMetric, protected val popSize: Int, val portfolioSize: Int,
    private val iterations: PositiveInt?, private val terminateThreshold: Double?
) : Evolver {

    init {
        if (popSize < 1 || portfolioSize < 1 || (terminateThreshold != null && terminateThreshold < 0)) {
            throw IllegalArgumentException("population size, portfolio size, and termination threshold must be > 0")
        } else if (portfolioSize > assets.size) {
            throw IllegalArgumentException("portfolio size must be less than number of available assets")
        } else if (iterations == null && terminateThreshold == null) {
            throw IllegalArgumentException("must provide # of iterations or a termination threshold, or both")
        }
    }

    /**
     * "Names" the given Portfolio by reassociating the assets in the Portfolio with their respective String names,
     * in the given list of asset names. i-th asset corresponds to the i-th asset name. Returns a list of asset names
     * to their respective weights, from the original Portfolio.
     */
    override fun namePortfolio(portfolio: Portfolio, assets: List<String>): List<Pair<String, Double>> {
        return portfolio.allocations.map { Pair(assets[it.asset], it.amount) }
    }

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

                population = newGeneration(population, latestFitnessScores)
                return assignScoresToPortfolios(population)
            }

        }
    }

    abstract fun newGeneration(population: List<Portfolio>, fitnessScores: List<Double>) : List<Portfolio>
}


