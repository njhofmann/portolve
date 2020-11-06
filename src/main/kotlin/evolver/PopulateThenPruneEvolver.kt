package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import selector.Selector

/**
 * Implementation of the Evolver interface that expands the population, then prunes it
 */
class PopulateThenPruneEvolver(
    assets: Int, selector: Selector, assetMutator: AssetMutator, weightMutator: WeightMutator,
    populator: Populator, fitnessMetric: FitnessMetric,  popSize: Int, portfolioSize: Int, iterations: PositiveInt?,
    terminateThreshold: Double?
) : AbstractEvolver(assets, selector, assetMutator, weightMutator, populator, fitnessMetric, popSize, portfolioSize,
    iterations, terminateThreshold) {


    override fun newGeneration(population: List<Portfolio>, oldScores: List<Double>): Pair<List<Portfolio>, List<Double>> {
        var newPop = populator.populate(population, popSize)
        newPop = assetMutator.mutateAssets(newPop)
        newPop = weightMutator.mutateWeights(newPop)
        newPop = selector.prune(newPop, fitnessMetric.evaluate(newPop))
        return Pair(newPop, fitnessMetric.evaluate(newPop))
    }
}