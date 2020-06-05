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
class Evolver(
    private val assets: List<String>, private val selector: Selector,
    private val assetMutator: AssetMutator, private val weightMutator: WeightMutator,
    private val populator: Populator, private val fitnessMetric: FitnessMetric,
    private val popSize: Int, private val portfolioSize: Int,
    private val iterations: PositiveInt?, private val terminateThreshold: Double?) {

    private var iterCount: Int = 0

    init {
        if (popSize < 1 || portfolioSize < 1 || (terminateThreshold != null && terminateThreshold < 0)) {
            throw IllegalArgumentException("population size, portfolio size, and termination threshold must be > 0")
        } else if (portfolioSize > assets.size) {
            throw IllegalArgumentException("portfolio size must be less than number of available assets")
        } else if (iterations == null && terminateThreshold == null) {
            throw IllegalArgumentException("must provide # of iterations or a termination threshold, or both")
        }
    }

    private fun namePortfolio(portfolio: Portfolio): List<Pair<String, Double>> {
        return portfolio.allocations.map { Pair(this.assets[it.asset], it.amount) }
    }

    private fun isFinished(fitnessScores: List<Double>): Boolean {
        if ((iterations != null && iterCount == iterations.num)
            || (terminateThreshold != null && fitnessScores.any { it > terminateThreshold })) {
            return true
        }
        iterCount++
        return false
    }

    fun findSolution(): List<Pair<String, Double>> {
        var population: List<Portfolio> = getRandomPopulation(assets.size, popSize, portfolioSize)
        while (true) {
            val scores = fitnessMetric.evaluate(population)
            print("Generation %d, Score: %f, Avg Score: %f\n".format(iterCount, scores.max(), scores.average()))
            if (isFinished(scores)) {
                break
            }
            population = selector.prune(population, scores)
            population = assetMutator.mutateAssets(population)
            population = weightMutator.mutateWeights(population)
            population = populator.populate(population, popSize)
        }
        return namePortfolio(getBest(population, fitnessMetric))
    }
}