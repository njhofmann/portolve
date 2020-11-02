package mutator.asset

import PositiveInt
import mutator.AbstractMutator
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import randomItemNoReplace

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
        val portfolioAssets = portfolio.allocations.map { it.asset }
        if (!allAssets.containsAll(portfolioAssets)) {
            throw IllegalArgumentException("portfolio contains assets that are not in the universe of assets")
        }
        return (allAssets - portfolioAssets).toMutableSet()
    }

    /**
     * Mutates the assets in the given Portfolio via the given mutation threshold. If an asset if selected for mutation,
     * selects a random asset from the universe of all assets that are not already apart of the Portfolio
     * @param portfolio: Portfolio to possibly. If there are no assets left to select, prints a warning.
     * @param mutateThreshold: threshold determining if mutation will occur
     * @return: mutated Portfolio
     */
    override fun mutatePortfolio(portfolio: Portfolio, mutateThreshold: Double): Portfolio {
        val availableAssets = getAvailableAssets(portfolio)
        return DefaultPortfolio(portfolio.allocations.map {
            if (availableAssets.isNotEmpty() && toMutate(mutateThreshold)) {
                Allocation(randomItemNoReplace(availableAssets), it.amount)
            } else {
                if (availableAssets.isEmpty()) {
                    println("warning: asset universe empty, mutation ended prematurely")  // TODO replace w logging
                }
                it
            }
        })
    }

    override fun mutateAssets(population: List<Portfolio>): List<Portfolio> {
        return mutatePortfolios(population)
    }
}