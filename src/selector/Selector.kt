package selector

import fitness.FitnessMetric
import portfolio.Portfolio

/**
 * Selection operation for determining which Portfolios in a population "survive" via a given
 * fitness scores
 */
interface Selector {
    fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio>
}