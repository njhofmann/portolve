package mutator.asset

import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.Portfolio

class RandomAssetMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?, assetUniverse: Int) :
    AssetMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    private val allAssets: Set<Int> = (0..assetUniverse).toHashSet()

    private var availableAssets: MutableSet<Int>? = null

    private fun getAvailableAssets(portfolio: Portfolio): MutableSet<Int> {
        // TODO check portfolio assets are in universe
        return (allAssets - portfolio.allocations.map { it.asset }).toMutableSet()
    }

    private fun getRandomAssetNoReplacement(assets: MutableSet<Int>): Pair<MutableSet<Int>, Int> {
        val selectedAsset: Int = assets.random()
        assets.remove(selectedAsset)
        return Pair(assets, selectedAsset)
    }

    override fun mutateAllocation(allocation: Allocation): Allocation {
        val selected = getRandomAssetNoReplacement(availableAssets!!)
        availableAssets = selected.first
        return Allocation(selected.second, allocation.amount)
    }

    override fun mutatePortfolio(portfolio: Portfolio): Portfolio {
        // TODO case where more items are mutated than available
        // update available assets with each new portfolio
        availableAssets = getAvailableAssets(portfolio)
        return super.mutatePortfolio(portfolio)
    }

    override fun mutateAssets(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}