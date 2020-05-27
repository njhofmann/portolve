package selector

import getPercentAtTick
import portfolio.Portfolio
import kotlin.math.round
import kotlin.random.Random

class StochasticUniversalSelector(private val keepPercent: Double, private val iterations: Int) :
        AbstractSelector(keepPercent, iterations) {

    private fun getCurKeepPercent(): Double {
        // TODO is this increasing or decreasing?
        return getPercentAtTick(keepPercent, timesCalled, iterations)
    }

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        val numToSelect: Int = round(getCurKeepPercent() * fitnessScores.size).toInt()
        val pointerDist: Double = 1.0 / numToSelect
        var threshold: Double = Random.nextDouble(0.0, 1.0 / numToSelect)
        val percentiles: List<Double> = normalizedPercentiles(fitnessScores)

        var idx: Int = 0
        return (0..numToSelect).map {
            threshold += if (it == 0) 0.0 else pointerDist
            while (threshold > percentiles[idx]) {
                idx++
            }
            portfolios[idx]
        }
    }

}