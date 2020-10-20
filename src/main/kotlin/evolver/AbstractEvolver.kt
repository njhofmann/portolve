package evolver

import PositiveInt
import portfolio.Portfolio

abstract class AbstractEvolver(
    assets: List<String>, popSize: Int, portfolioSize: Int, iterations: PositiveInt?, terminateThreshold: Double?
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
}

