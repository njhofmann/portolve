package mutator.asset

import PositiveInt
import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import randomItemNoReplacement

/**
 * Implementation of the AssetMutator interface, providing random mutation for the assets in a Portfolio. Takes in the
 * rate at which to mutate assets, and two optional parameters to have a linearly changing mutation rate.
 * @param assetUniverse: gives the number of indicies making up all asset ids, from 0 to assetUniverse - 1
 * @param mutationRate: rate at which mutation is applied
 * @param finalMutationRate: optional param determining a final mutation rate
 * @param iterations: optional param giving the number of steps between the starting and final mutation rates
 */
class RandomAssetMutator(assetUniverse: Int, mutationRate: Double, finalMutationRate: Double? = null,
                         iterations: PositiveInt? = null) :
    AssetMutator, AbstractMutator(mutationRate, finalMutationRate, iterations) {

    /**
     * Universe of all assets, from 0 to assetUniverse - 1
     */
    private val allAssets: Set<Int> = (0 until assetUniverse).toHashSet()

    /**
     * Gives the assets in the universe of assets, but not in the given Portfolio
     * @param portfolio: Portfolio to utilize
     * @return: asset ids not in given Portfolio
     */
    private fun getAvailableAssets(portfolio: Portfolio): MutableSet<Int> {
        // TODO check portfolio assets are in universe
        return (allAssets - portfolio.allocations.map { it.asset }).toMutableSet()
    }

    /**
     * Mutates the assets in the given Portfolio via the given mutation threshold. If an asset if selected for mutation,
     * selects a random asset from the universe of all assets that are not already apart of the Portfolio
     * @param portfolio: Portfolio to possibly mutate
     * @param mutateThreshold: threshold determining if mutation will occur
     * @return: mutated Portfolio
     */
    override fun mutatePortfolio(portfolio: Portfolio, mutateThreshold: Double): Portfolio {
        // TODO case where more items are mutated than available
        val availableAssets = getAvailableAssets(portfolio)
        return DefaultPortfolio(portfolio.allocations.map {
            if (toMutate(mutateThreshold)) Allocation(randomItemNoReplacement(availableAssets), it.amount) else it
        })
    }

    override fun mutateAssets(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}