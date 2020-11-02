package fitness

import portfolio.Portfolio

/**
 * Abstract implementation of FitnessMetric, provides common methods for all full implementations of FitnessMetric to
 * utilize. Takes in a list of pairings to returns, checks each return list is same size.
 * @param assetsToReturns: pairing of assets to their respective returns
 */
abstract class AbstractFitnessMetric(private val assetsToReturns: List<Pair<String, List<Double>>>) : FitnessMetric {

    init {
        when {
            assetsToReturns.isEmpty() -> {
                throw IllegalArgumentException("given asset universe must be non-empty")
            }
            assetsToReturns.any { it.second.size != assetsToReturns[0].second.size } -> {
                throw IllegalArgumentException("each set of asset returns must be the same size")
            }
            assetsToReturns[0].second.isEmpty() -> {
                throw IllegalArgumentException("asset returns must be non-empty")
            }
        }
    }

    /**
     * Retrieves the returns associated with the i-th asset
     * @param i: asset id
     * @return: i-th asset's returns
     */
    protected fun getIthAssetReturns(i: Int): List<Double> {
        return assetsToReturns[i].second
    }

    /**
     * Scoring metric to apply to each Portfolio in a population
     */
    protected abstract fun score(portfolio: Portfolio): Double

    override fun evaluate(population: List<Portfolio>): List<Double> {
        return population.map { score(it) }
    }
}