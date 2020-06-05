package fitness

import org.nield.kotlinstatistics.standardDeviation
import portfolio.Portfolio

class SharpeMetric(assetsToReturns: List<Pair<String, List<Double>>>, private val avgRateFreeReturn: Double = 0.0) :
    AbstractFitnessMetric(assetsToReturns) {

    override fun score(portfolio: Portfolio): Double {
        // TODO store avg returns instead of whole list
        val avgReturns = portfolio.allocations.map { Pair(it.amount, getIthAssetReturns(it.asset).average()) }
        val weightedReturns =  avgReturns.map { it.first * (it.second - avgRateFreeReturn) }
        return weightedReturns.sum() / avgReturns.map { it.second }.standardDeviation()
    }
}