package fitness

import PositiveInt
import org.nield.kotlinstatistics.standardDeviation
import portfolio.Portfolio
import kotlin.math.sqrt

/**
 * Implementation of the FitnessMetric interface representing scoring via the a risk-return ratio, where returns are
 * the expected returns of a portfolio minus a (optional) baseline - divided by the standard deviation of those same
 * returns. Meant to represent the Sharpe ratio, Information ratio, Sortino ratio, and variants in a single class.
 *
 * Takes in pairing of assets to their respective returns, i-th item represented by its index. Also takes in an optional
 * list of returns to use as a baseline, must be the same length as every asset's return list.
 *
 * Also takes in an boolean flag to use all returns when computing the standard deviation, or just negative ones -
 * depending on one's definition of risk.
 *
 * Also takes in an annualization rate to convert the metric from that a of shorter time period to a longer time period,
 * ie month rates to a yearly rate. Equal to the number of times that the short time period appears in the larger rate.
 * @param assetsToReturns: pairing of assets to their respective returns
 * @param rateFreeReturns: list of rate free returns
 * @param onlyNegativeReturns: use only negative returns when computing
 * @param annualizationRate: rate to convert shorter period to longer period
 */
class ReturnRiskRatio(
    assetsToReturns: List<Pair<String, List<Double>>>,
    private val rateFreeReturns: List<Double>? = null,
    private val annualizationRate: PositiveInt? = null,
    private val onlyNegativeReturns: Boolean = false
) : AbstractFitnessMetric(assetsToReturns) {

    init {
        // asset sizes are already checked
        if (rateFreeReturns != null && rateFreeReturns.size != assetsToReturns.first().second.size) {
            throw IllegalArgumentException("average rate free return size must equal number of returns for each asset")
        }
    }

    /**
     * Applies the Sharpe ratio to the given Portfolio
     * @param portfolio: Portfolio to score
     * @return: Portfolio's score
     */
    override fun score(portfolio: Portfolio): Double {
        val weightedReturns = portfolio.allocations.map { getIthAssetReturns(it.asset).map { d -> d * it.amount } }
        var portfolioReturns = (0 until (getIthAssetReturns(0).size)).map { weightedReturns.map { r -> r[it] }.sum() }
        val excessReturn = portfolioReturns.mapIndexed {
                idx, d -> d - (if (rateFreeReturns != null) rateFreeReturns[idx] / 252 else 0.0)
        }.average()

        if (onlyNegativeReturns) {
            portfolioReturns = portfolioReturns.filter { it < 0.01 }
        }
        val ratio = (excessReturn / portfolioReturns.standardDeviation())

        if (annualizationRate != null) {
            return ratio * sqrt(annualizationRate.num.toDouble())
        }
        return ratio
    }
}