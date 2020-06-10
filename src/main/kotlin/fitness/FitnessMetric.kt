package fitness

import portfolio.Portfolio

/**
 * Scoring metric for evaluating the "fitness" of a population of Portfolios
 */
interface FitnessMetric {

    /**
     * Evaluates the given population of Portfolios via this FitnessMetric, i-th Double in the returned list is the
     * score of the i-th portfolio
     * @return: fitness scores for the given population
     */
    fun evaluate(population: List<Portfolio>): List<Double>

}