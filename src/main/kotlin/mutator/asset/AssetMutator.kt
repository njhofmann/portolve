package mutator.asset

import portfolio.Portfolio

/**
 * Mutation operator that mutates the assets making up the Portfolios of a population
 */
interface AssetMutator {

    /**
     * Mutates the assets making up the given population of Portfolios
     * @param population: list of Portfolio
     * @return: given population mutated
     */
    fun mutateAssets(population: List<Portfolio>): List<Portfolio>

}