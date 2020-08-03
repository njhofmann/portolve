package evolver

import PositiveInt
import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import portfolio.Portfolio
import selector.Selector

/**
 * Represents an evolutionary strategy comprised of various evolutionary operators to solve the optimal portfolio
 * problem for a given set of assets. Returns a lazily evaluated sequence of Portfolios and their scores for each
 * generation this Evolver produces
 */
interface Evolver : Sequence<List<Pair<Portfolio, Double>>> {

    fun namePortfolio(portfolio: Portfolio, assets: List<String>): List<Pair<String, Double>>

    override fun iterator(): Iterator<List<Pair<Portfolio, Double>>>
}