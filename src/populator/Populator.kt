package populator

import portfolio.Portfolio

/**
 * Recombination / crossover operation for creating a new Portfolio from two given Portfolio
 */
interface Populator {

    fun populate(population: List<Portfolio>, targetSize: Int): List<Portfolio>

}