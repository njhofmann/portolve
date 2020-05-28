package fitness

import org.nield.kotlinstatistics.variance
import portfolio.Portfolio

class MeanVarianceMetric(assetsToReturns: List<Pair<String, List<Double>>>, private val lambda: Double) :
    AbstractFitnessMetric(assetsToReturns) {

    private fun minusAverage(nums: List<Double>): List<Double> {
        val avg = nums.average()
        return nums.map { it - avg }
    }

    private fun computeCovariance(x: List<Double>, y: List<Double>): Double {
        val averagedX = minusAverage(x)
        val averagedY = minusAverage(y)
        return averagedX.mapIndexed { idx, d -> d * averagedY[idx] }.sum() / (x.size - 1)
    }

    private fun createKey(x: Int, y: Int): Set<Pair<Int, Int>> {
        val key = HashSet<Pair<Int, Int>>()
        key.add(Pair(x, y))
        return key
    }

    private fun computeRisk(selectedWeights: List<Double>, selectedReturns: List<List<Double>>): Double {
        var history: MutableMap<Set<Pair<Int, Int>>, Double> = HashMap();
        return selectedReturns.mapIndexed { idx, iList ->
            selectedReturns.mapIndexed { jdx, jList ->
                val idxKey = createKey(idx, jdx)
                if (!history.containsKey(idxKey)) {
                    history[idxKey] = selectedWeights[idx] * computeCovariance(iList, jList) * selectedWeights[jdx]
                }
                history[idxKey]!!
            }.sum()
        }.sum()
    }

    override fun score(portfolio: Portfolio): Double {
        // edge cases for lambda of 1 and 0
        val selectedReturns = portfolio.allocations.map { Pair(it.amount, getIthAssetReturns(it.asset)) }
        val expectedReturn = selectedReturns.map { it.first * it.second.average() }.sum()
        val portfolioRisk = computeRisk(selectedReturns.map { it.first }, selectedReturns.map { it.second })
        return (lambda * portfolioRisk) - ((1 - lambda) * expectedReturn)
    }
}