package selector

import portfolio.Portfolio
import roundToNearestInt
import kotlin.random.Random

class RouletteWheelSelector(keepPercent: Double, endKeepPercent: Double?, iterations: Int?) :
    AbstractSelector(keepPercent, endKeepPercent, iterations) {

    private fun getRandPortfolioIdx(percentiles: List<Double>): Int {
        // with replacement
        var cumPercentage = 0.0
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
        val numToKeep: Int = roundToNearestInt(getPercentAtTick() * portfolios.size)
        return (0..numToKeep).map { portfolios[getRandPortfolioIdx(cumNormScores)] }
    }
}