package populator

import portfolio.Portfolio

/**
 * Crossover operator for creating a new Portfolios from a group of Portfolios, to repopulate that group
 */
interface Populator {

    /**
     * Repopulates the given population of Portfolios to the given size via crossover of Portfolios within the
     * population
     * @param population: population of Portfolios to repopulate
     * @param targetSize: target size of the population
     * @return: repopulated Portfolio population
     */
    fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio>
}