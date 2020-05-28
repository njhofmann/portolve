package selector

import portfolio.Portfolio
import roundToNearestInt

class TruncateSelector(keepPercent: Double, endKeepPercent: Double?, iterations: Int?) :
        AbstractSelector(keepPercent, endKeepPercent, iterations) {

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        val truncAmount = roundToNearestInt(getPercentAtTick() * fitnessScores.size)
        val sortedIndices = (0..portfolios.size).map { Pair(it, fitnessScores[it]) }
                .sortedWith(compareBy({ it.second }, { it.first })).map { it.first }
        val truncIndices = sortedIndices.subList(
                sortedIndices.size - truncAmount, sortedIndices.size)
        return truncIndices.map { portfolios[it] }
    }
}