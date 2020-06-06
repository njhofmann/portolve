package selector

import AbstractRateAnnealer
import PositiveInt
import portfolio.Portfolio
import kotlin.math.abs
import kotlin.math.min

abstract class AbstractSelector(keepPercent: Double, endKeepPercent: Double?, iterations: PositiveInt?) : Selector,
    AbstractRateAnnealer(keepPercent, endKeepPercent, iterations) {

    protected fun sizeCheck(portfolios: List<Portfolio>, scores: List<Double>) {
        if (portfolios.size != scores.size) {
            throw IllegalArgumentException("number of Portfolios must equal number of fitness scores")
        }
    }

    private fun cumulativeSum(scores: List<Double>): List<Double> {
        var cumSum = 0.0
        return scores.map { cumSum += it; cumSum }
    }

    protected fun normalizedPercentiles(scores: List<Double>): List<Double> {
        val cumSum = cumulativeSum(scaleScoresToPositive(scores))
        return cumSum.map { it / cumSum[cumSum.size - 1]}
    }

    private fun scaleScoresToPositive(scores: List<Double>): List<Double> {
        if (scores.all { it > 0 }) {
            return scores
        }
        val minScore: Double = scores.min()!!
        return scores.map { it + abs(minScore) }
    }
}