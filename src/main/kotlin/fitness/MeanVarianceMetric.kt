package fitness

import portfolio.Portfolio
import java.util.*
import kotlin.collections.HashMap

/**
 * Implementation of the FitnessMetric interface representing scoring via the mean-variance model of modern portfolio
 * theory. Takes in pairing of assets to their respective returns, i-th item represented by its index. Lambda
 * gives the weighting between risk and return, close to 1 the more weight given to risk.
 * @param assetsToReturns: pairing of assets to their respective returns
 * @param lambda: unit value determining the weighting between risk and return in the model
 */
class MeanVarianceMetric(assetsToReturns: List<Pair<String, List<Double>>>, private val lambda: Double) :
    AbstractFitnessMetric(assetsToReturns) {

    /**
     * Subtracts the average of the given list from each item in the list
     * @param nums: list of doubles to work on
     * @return: given list of doubles with average subtracted from each element
     */
    private fun minusAverage(nums: List<Double>): List<Double> {
        val avg = nums.average()
        return nums.map { it - avg }
    }

    /**
     * Computes the between covariance two assets
     * @param x: returns for the first asset
     * @param y: returns for the second asset
     * @return: covariance between x and y
     */
    private fun computeCovariance(x: List<Double>, y: List<Double>): Double {
        val averagedX = minusAverage(x)
        val averagedY = minusAverage(y)
        return averagedX.mapIndexed { idx, d -> d * averagedY[idx] }.sum() / (x.size - 1)
    }

    /**
     * Creates the key for two assets to map to their respective covariance. Key for (x, y) == key for (y, x).
     * @param x: first asset
     * @param y: second asset
     * @return: key for x and y
     */
    private fun createKey(x: Int, y: Int): SortedSet<Int> {
        return listOf(x, y).toSortedSet()
    }

    /**
     * Computes the risk for the given set of weights and returns, i-th weights corresponds to the i-th returns.
     * Essentially computes W * C * W, where C is the covariance matrix of all the returns and W is the weight list.
     * @param weights: weights to use
     * @param returns: returns to apply weights to
     * @return: risk score for given weights and returns
     */
    private fun computeRisk(weights: List<Double>, returns: List<List<Double>>): Double {
        val history: MutableMap<SortedSet<Int>, Double> = HashMap();
        return returns.mapIndexed { x, xList ->
            returns.mapIndexed { y, yList ->
                val idxKey = createKey(x, y)
                if (!history.containsKey(idxKey)) {
                    history[idxKey] = weights[x] * computeCovariance(xList, yList) * weights[y]
                }
                history[idxKey]!!
            }.sum()
        }.sum()
    }

    /**
     * Transforms the given Portfolio into list of pairing between weights and returns, where the i-th pairing
     * corresponds to the i-th Allocation in the Portfolio
     * @param portfolio: Portfolio to transform
     * @return: pairings of weights and returns for the given Portfolio
     */
    private fun getSelectedReturns(portfolio: Portfolio): List<Pair<Double, List<Double>>> {
        return portfolio.allocations.map { Pair(it.amount, getIthAssetReturns(it.asset)) }
    }

    private fun computeExpectedReturn(selectedReturns: List<Pair<Double, List<Double>>>): Double {
        return selectedReturns.map { it.first * it.second.average() }.sum()
    }

    private fun computeExpectedRisk(selectedReturns: List<Pair<Double, List<Double>>>): Double {
        return computeRisk(selectedReturns.map { it.first }, selectedReturns.map { it.second })
    }

    /**
     * Applies the mean-variance score to the given Portfolio
     * @param portfolio: Portfolio to score
     * @return: Portfolio's score
     */
    override fun score(portfolio: Portfolio): Double {
        val selectedReturns = getSelectedReturns(portfolio)
        return when (lambda) {
            0.0 -> computeExpectedReturn(selectedReturns)
            1.0 -> computeExpectedRisk(selectedReturns)
            else -> (lambda * (computeExpectedRisk(selectedReturns) + computeExpectedRisk(selectedReturns))) - 1
        }
    }
}