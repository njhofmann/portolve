package selector

import fitness.FitnessMetric
import portfolio.Portfolio

class TournamentSelector(private val iterations: Int) : AbstractSelector(iterations) {

    override fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        TODO("Not yet implemented")
    }
}