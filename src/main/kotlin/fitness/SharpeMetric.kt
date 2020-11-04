package fitness

import org.nield.kotlinstatistics.standardDeviation
import portfolio.Portfolio

/**
 * Implementation of the FitnessMetric interface representing scoring via the Sharpe metric. Takes in pairing of assets
 * to their respective returns, i-th item represented by its index. Also takes in a list of returns for the average
 * rate free return, must be the same length as every asset's return list.
 * @param assetsToReturns: pairing of assets to their respective returns
 * @param avgRateFreeReturn: returns making up the rate free return
 */
class SharpeMetric(assetsToReturns: List<Pair<String, List<Double>>>, private val avgRateFreeReturn: List<Double>) :
    AbstractFitnessMetric(assetsToReturns) {

    init {
        // asset sizes are already checked
        if (avgRateFreeReturn.size != assetsToReturns.first().second.size) {
            throw IllegalArgumentException("average rate free return size must equal number of returns for each asset")
        }
    }

    /**
     * Applies the Sharpe ratio to the given Portfolio
     * @param portfolio: Portfolio to score
     * @return: Portfolio's score
     */
    override fun score(portfolio: Portfolio): Double {
        val avgAdjustedReturns = portfolio.allocations.map {
            Pair(it.amount, getIthAssetReturns(it.asset).mapIndexed { idx, d -> d -
                    avgRateFreeReturn[idx]
            }.average())
        }
        val weightedReturns =  avgAdjustedReturns.map { it.first * it.second }
        return weightedReturns.sum() / avgAdjustedReturns.map { it.second }.standardDeviation()
    }
}