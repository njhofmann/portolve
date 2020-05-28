package fitness

import portfolio.Portfolio

/**
 * Scoring metric for evaluating the quality of a Portfolio
 */
interface FitnessMetric {

    fun evaluate(population: List<Portfolio>): List<Double>

}