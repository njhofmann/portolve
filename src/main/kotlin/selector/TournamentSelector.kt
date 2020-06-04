package selector

import PositiveInt
import portfolio.Portfolio
import roundToNearestInt
import kotlin.random.Random

class TournamentSelector(private val tournySize: Int, keepPercent: Double, endKeepPercent: Double? = null,
                         iterations: PositiveInt? = null) : AbstractSelector(keepPercent, endKeepPercent, iterations) {

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
        val newPopSize = roundToNearestInt(getPercentAtTick() * portfolios.size)
        return (0 until newPopSize).map {
            var bestIdx: Int = -1
            (0 until tournySize).forEach { _ ->
                val randIdx = Random.nextInt(0, portfolios.size)
                if (bestIdx < 0 || fitnessScores[randIdx] > fitnessScores[bestIdx]) {
                    bestIdx = randIdx
                }
            }
            portfolios[bestIdx]
        }
    }
}