package mutator.asset

import getPercentAtTick
import mutator.asset.AssetMutator
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.random.Random

class RandomAssetMutator(private val assetUniverse: Int, private val mutationRate: Double,
                         private val iterations: Int?) : AssetMutator {

    private val allAssets: Set<Int> = (0..assetUniverse).toHashSet()

    private var timesCalled: Int = 0

    private fun getCurrentPercentage(): Double {
        val toReturn = getPercentAtTick(mutationRate, timesCalled, iterations)
        timesCalled++
        return toReturn
    }

    private fun getAvailableAssets(portfolio: Portfolio): MutableSet<Int> {
        return (allAssets - portfolio.allocations.map { it.asset }).toMutableSet()
    }

    private fun getRandomAssetNoReplacement(assets: MutableSet<Int>): Pair<MutableSet<Int>, Int> {
        val selectedAsset: Int = assets.random()
        assets.remove(selectedAsset)
        return Pair(assets, selectedAsset)
    }

    private fun toMutate(threshold: Double): Boolean {
        return Random.nextDouble(0.0, 1.0) < threshold
    }

    private fun mutatePortfolio(portfolio: Portfolio, mutationThreshold: Double): Portfolio {
        // TODO case where more items are mutated than available
        var availableAssets = getAvailableAssets(portfolio)
        return DefaultPortfolio(portfolio.allocations.map { x: Allocation ->
            if (toMutate(mutationThreshold)) {
                val selected = getRandomAssetNoReplacement(availableAssets)
                availableAssets = selected.first
                Allocation(selected.second, x.amount)
            } else {
                x
            }
        })
    }

    override fun mutate(population: List<Portfolio>): List<Portfolio> {
        val mutationChange = getCurrentPercentage()
        return population.map { mutatePortfolio(it, mutationChange) }
    }
}