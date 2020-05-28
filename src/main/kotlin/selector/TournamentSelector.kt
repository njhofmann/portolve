package selector

import portfolio.Portfolio
import kotlin.math.round
import kotlin.random.Random

class TournamentSelector(keepPercent: Double, endKeepPercent: Double?, iterations: Int?, private val tournySize: Int) :
    AbstractSelector(keepPercent, endKeepPercent, iterations) {

    init {
        if (tournySize < 2) {
            throw IllegalArgumentException("tournament size must be > 1")
        }
    }

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        sizeCheck(portfolios, fitnessScores)
        if (portfolios.size > tournySize) {
            throw IllegalArgumentException("portfolio size is greater than tournament size")
        }
        val newPopSize = round(getPercentAtTick() * portfolios.size).toInt()
        return (0..newPopSize).map {
            var bestIdx: Int? = null
            for (i in (0..tournySize)) {
                val randIdx = Random.nextInt(0, portfolios.size)
                if (bestIdx == null || fitnessScores[randIdx] > fitnessScores[bestIdx]) {
                    bestIdx = randIdx
                }
            }
            portfolios[bestIdx!!]
        }
    }
}