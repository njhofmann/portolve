package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getRandomPopulation
import selector.Selector

/**
 * Implementation of the Evolver interface that prunes the population, then repopulates it
 */
class PruneThenPopulateEvolver(
    assets: Int, selector: Selector,  assetMutator: AssetMutator, weightMutator: WeightMutator,
    populator: Populator, fitnessMetric: FitnessMetric,  popSize: Int, portfolioSize: Int, iterations: PositiveInt?,
    terminateThreshold: Double?
) : AbstractEvolver(assets, selector, assetMutator, weightMutator, populator, fitnessMetric, popSize, portfolioSize,
    iterations, terminateThreshold) {

    override fun newGeneration(population: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        var newPop = selector.prune(population, fitnessScores)
        newPop = assetMutator.mutateAssets(newPop)
        newPop = weightMutator.mutateWeights(newPop)
        return populator.populate(newPop, popSize)
    }
}