package fitness

import PositiveInt
import org.nield.kotlinstatistics.geometricMean
import org.nield.kotlinstatistics.standardDeviation
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Implementation of the FitnessMetric interface representing scoring via the Sharpe metric. Takes in pairing of assets
 * to their respective returns, i-th item represented by its index. Also takes in a list of returns for the average
 * rate free return, must be the same length as every asset's return list.
 *
 * Also takes in an annualization rate to convert the metric from that a of shorter time period to a longer time period,
 * ie month rates to a yearly rate. Equal to the number of times that the short time period appears in the larger
 * @param assetsToReturns: pairing of assets to their respective returns
 * @param avgRateFreeReturn: returns making up the rate free return
 * @param annualizationRate: rate to convert shorter period to longer period
 */
class SharpeMetric(assetsToReturns: List<Pair<String, List<Double>>>, private val avgRateFreeReturn: List<Double>,
private val annualizationRate: PositiveInt) :
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
        val weightedReturns = portfolio.allocations.map { getIthAssetReturns(it.asset).map { d -> d * it.amount }}
        val portfolioReturns = (0 until (getIthAssetReturns(0).size)).map { weightedReturns.map { r -> r[it] }.sum() }
        val sharpe = portfolioReturns.mapIndexed { idx, d ->
            d - (avgRateFreeReturn[idx] / 252)
        }.average() / portfolioReturns.standardDeviation()
        return sharpe * sqrt(annualizationRate.num.toDouble())
    }
}