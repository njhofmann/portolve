package mutator.asset

import portfolio.Portfolio

/**
 * Mutation operation for mutating the asset of a given Portfolio
 */
interface AssetMutator {

    fun mutateAssets(population: List<Portfolio>): List<Portfolio>

}