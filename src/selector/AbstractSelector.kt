package selector

import portfolio.Portfolio

abstract class AbstractSelector(private val iters: Int?) : Selector {

    protected var timesCalled: Int = 0

    init {
        if (iters != null && iters <= 0) {
            throw IllegalArgumentException("number of iterations must be > 0")
        }
    }

    protected fun sizeCheck(portfolios: List<Portfolio>, scores: List<Double>) {
        if (portfolios.size != scores.size) {
            throw IllegalArgumentException("number of Portfolios must equal number of fitness scores")
        }
    }

    private fun cumulativeSum(scores: List<Double>): List<Double> {
        return scores.mapIndexed { idx, dbl -> if (idx == 0) dbl else dbl + scores[idx - 1] }
    }

    private fun normalize(doubles: List<Double>): List<Double> {
        val sum: Double = doubles.sum()
        return doubles.map { it / sum }
    }

    protected fun normalizedPercentiles(scores: List<Double>): List<Double> {
        return normalize(cumulativeSum(scores))
    }
}