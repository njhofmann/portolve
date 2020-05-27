package selector

import getPercentAtTick
import portfolio.Portfolio
import java.lang.IllegalArgumentException
import kotlin.math.round
import kotlin.random.Random

class RouletteWheelSelector(private val keepPercent: Double, private val iterations: Int) :
        AbstractSelector(keepPercent, iterations) {

    private fun getCurrentPercent(): Double {
        // TODO is this the right way to decay selection percent?
        return 1 - getPercentAtTick(keepPercent, timesCalled, iterations)
    }

    private fun getRandPortfolioIdx(percentiles: List<Double>): Int {
        // with replacement
        var cumPercentage: Double = 0.0
        val threshold: Double = Random.nextDouble(0.0, 1.0)
        for ((idx, percentile) in percentiles.withIndex()) {
            cumPercentage += percentile
            if (threshold < cumPercentage) {
                return idx
            }
        }
        throw IllegalArgumentException("threshold out of percentile bounds")
    }

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        val cumNormScores = normalizedPercentiles(fitnessScores)
        val numToKeep: Int = round(getCurrentPercent() * portfolios.size).toInt()
        return (0..numToKeep).map { portfolios[getRandPortfolioIdx(cumNormScores)] }
    }
}