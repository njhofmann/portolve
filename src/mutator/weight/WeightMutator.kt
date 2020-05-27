package mutator.weight

import portfolio.Portfolio

/**
 * Mutation operation for mutating the asset weights of a given Portfolio
 */
interface WeightMutator {
    fun mutateWeights(population: List<Portfolio>): List<Portfolio>
}