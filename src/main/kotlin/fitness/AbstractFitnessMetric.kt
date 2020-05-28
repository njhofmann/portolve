package fitness

import portfolio.Portfolio

abstract class AbstractFitnessMetric(private val assetsToReturns: List<Pair<String, List<Double>>>) : FitnessMetric {

    init {
        if (assetsToReturns.isEmpty()) {
            throw IllegalArgumentException("given asset universe must be non-empty")
        }
    }

    protected fun getIthAssetReturns(i: Int): List<Double> {
        return assetsToReturns[i].second
    }

    protected abstract fun score(portfolio: Portfolio): Double

    override fun evaluate(population: List<Portfolio>): List<Double> {
        return population.map { score(it) }
    }
}