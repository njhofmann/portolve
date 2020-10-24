package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import portfolio.getRandomPopulation
import selector.Selector

class PopulateThenPruneEvolver(
    assets: List<String>, selector: Selector, assetMutator: AssetMutator, weightMutator: WeightMutator,
    populator: Populator, fitnessMetric: FitnessMetric,  popSize: Int, portfolioSize: Int, iterations: PositiveInt?,
    terminateThreshold: Double?
) : AbstractEvolver(assets, selector, assetMutator, weightMutator, populator, fitnessMetric, popSize, portfolioSize,
    iterations, terminateThreshold) {


    override fun newGeneration(population: List<Portfolio>, fitnessScores: List<Double>): List<Portfolio> {
        var newPop = populator.populate(population, popSize)
        newPop = assetMutator.mutateAssets(newPop)
        newPop = weightMutator.mutateWeights(newPop)
        return selector.prune(newPop, fitnessScores)
    }
}