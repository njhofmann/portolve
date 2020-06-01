package mutator.asset

import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.Portfolio
import randomItemNoReplacement

class RandomAssetMutator(assetUniverse: Int, mutationRate: Double, finalMutationRate: Double? = null,
                         iterations: Int? = null) :
    AssetMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    private val allAssets: Set<Int> = (0 until assetUniverse).toHashSet()

    private var availableAssets: MutableSet<Int>? = null

    private fun getAvailableAssets(portfolio: Portfolio): MutableSet<Int> {
        // TODO check portfolio assets are in universe
        return (allAssets - portfolio.allocations.map { it.asset }).toMutableSet()
    }

    override fun mutateAllocation(allocation: Allocation): Allocation {
        return Allocation(randomItemNoReplacement(availableAssets!!), allocation.amount)
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