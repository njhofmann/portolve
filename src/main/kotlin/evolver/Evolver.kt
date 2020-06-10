package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import selector.Selector

interface Evolver {

    fun findSolution(assets: List<String>, selector: Selector, assetMutator: AssetMutator, weightMutator: WeightMutator,
                     populator: Populator, fitnessMetric: FitnessMetric, popSize: Int,  portfolioSize: Int,
                     iterations: PositiveInt?, terminateThreshold: Double?)

}