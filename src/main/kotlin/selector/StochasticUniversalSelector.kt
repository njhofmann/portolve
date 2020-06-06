package selector

import PositiveInt
import portfolio.Portfolio
import roundToNearestInt
import java.util.*
import kotlin.random.Random

class StochasticUniversalSelector(keepPercent: Double, endKeepPercent: Double? = null, iterations: PositiveInt? = null) :
        AbstractSelector(keepPercent, endKeepPercent, iterations) {

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        val numToSelect: Int = roundToNearestInt(getPercentAtTick() * fitnessScores.size)
        val pointerDist: Double = 1.0 / numToSelect // total fitness / # offspring to keep
        val threshold: Double = Random.nextDouble(0.0, pointerDist)
        val pointers = (0 until numToSelect).map { threshold + (it * pointerDist) }
        val percentiles: List<Double> = normalizedPercentiles(fitnessScores)

        var idx = 0
        val selectedPortfolios = LinkedList<Portfolio>()
        pointers.forEach {
            while (percentiles[idx] < it) {
                idx++
            }
            selectedPortfolios.add(portfolios[idx])
        }
        return selectedPortfolios
    }
}