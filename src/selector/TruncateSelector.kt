package selector

import getPercentAtTick
import portfolio.Portfolio
import kotlin.math.round

class TruncateSelector(private val topPercent: Double, private val iterations: Int?) :
        AbstractSelector(iterations) {

    private fun getCurrentPercent(): Double {
        return 1 - getPercentAtTick(topPercent, timesCalled, iterations)
    }

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        val curPercent = this.getCurrentPercent()
        val truncAmnt = round(curPercent * fitnessScores.size).toInt()
        val sortedIndices = (0..portfolios.size).map { Pair(it, fitnessScores[it]) }
                .sortedWith(compareBy({ it.second }, { it.first })).map { it.first }
        val truncIndices = sortedIndices.subList(
                sortedIndices.size - truncAmnt, sortedIndices.size)
        return truncIndices.map { portfolios[it] }
    }
}