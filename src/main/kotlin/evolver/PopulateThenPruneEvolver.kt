package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import selector.Selector

class PopulateThenPruneEvolver(
    private val assets: List<String>, private val selector: Selector, private val assetMutator: AssetMutator,
    private val weightMutator: WeightMutator, private val populator: Populator,
    private val fitnessMetric: FitnessMetric, private val popSize: Int, val portfolioSize: Int,
    private val iterations: PositiveInt?, private val terminateThreshold: Double?
) : AbstractEvolver(assets, popSize, portfolioSize, iterations, terminateThreshold) {

    override fun iterator(): Iterator<List<Pair<Portfolio, Double>>> {
        TODO("Not yet implemented")
    }
}