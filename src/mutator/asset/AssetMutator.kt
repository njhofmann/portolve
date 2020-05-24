package mutator.asset

import portfolio.Portfolio

/**
 * Mutation operation for mutating the asset of a given Portfolio
 */
interface AssetMutator {

    fun mutate(population: List<Portfolio>): List<Portfolio>

}