package selector

import PositiveInt
import portfolio.Portfolio
import roundToNearestInt
import kotlin.random.Random

class StochasticUniversalSelector(keepPercent: Double, endKeepPercent: Double? = null, iterations: PositiveInt? = null) :
        AbstractSelector(keepPercent, endKeepPercent, iterations) {

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        val numToSelect: Int = roundToNearestInt(getPercentAtTick() * fitnessScores.size)
        val pointerDist: Double = 1.0 / numToSelect
        var threshold: Double = Random.nextDouble(0.0, pointerDist)
        val percentiles: List<Double> = normalizedPercentiles(fitnessScores)

        var idx = 0
        return (0 until numToSelect).map {
            threshold += if (it == 0) 0.0 else pointerDist
            while (threshold > percentiles[idx]) {
                idx++
            }
            portfolios[idx]
        }
    }

}