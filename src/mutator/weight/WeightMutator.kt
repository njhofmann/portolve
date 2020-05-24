package mutator.weight

import portfolio.Portfolio

/**
 * Mutation operation for mutating the asset weights of a given Portfolio
 */
interface WeightMutator {
    fun mutate(population: List<Portfolio>): List<Portfolio>
}