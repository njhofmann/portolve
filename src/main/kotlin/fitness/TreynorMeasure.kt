package fitness

import portfolio.Portfolio

class TreynorMeasure(assetsToReturns: List<Pair<String, List<Double>>>) : AbstractFitnessMetric(assetsToReturns) {
    override fun score(portfolio: Portfolio): Double {
        TODO("Not yet implemented")
    }
}