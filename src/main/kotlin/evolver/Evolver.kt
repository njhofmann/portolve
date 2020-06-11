package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import selector.Selector

/**
 * Represents an evolutionary strategy comprised of various evolutionary operators to solve the optimal portfolio
 * problem for a given set of assets
 */
interface Evolver {

    /**
     *
     * @param assets:
     * @param selector:
     * @param assetMutator:
     * @param weightMutator:
     * @param populator:
     * @param fitnessMetric:
     * @param popSize:
     * @param portfolioSize
     * @param iterations
     * @param terminateThreshold:
     * @return
     */
    fun findSolution(
        assets: List<String>, selector: Selector, assetMutator: AssetMutator, weightMutator: WeightMutator,
        populator: Populator, fitnessMetric: FitnessMetric, popSize: Int, portfolioSize: Int,
        iterations: PositiveInt?, terminateThreshold: Double?, collect: Boolean?
    ): List<Pair<String, Double>>

}