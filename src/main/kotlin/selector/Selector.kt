package selector

import portfolio.Portfolio

/**
 * Selection operator for determining which Portfolios in a population "survive" via given fitness scores
 */
interface Selector {

    /**
     * "Prunes" the given population of Portfolios using the given list of fitness scores, i-th Portfolio corresponds to
     * the i-th score
     * @param portfolios: population of Portfolios
     * @param fitnessScores: list of fitness scores
     * @return: prunes population
     */
    fun prune(portfolios: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio>
}