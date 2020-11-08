package fitness

import PositiveInt
import portfolio.Portfolio

class SortinoRatio(assetsToReturns: List<Pair<String, List<Double>>>,
                   rateFreeReturns: List<Double>,
                   annualizationRate: PositiveInt
) :
    AbstractSharpeRatio(assetsToReturns, rateFreeReturns, annualizationRate) {

    override fun score(portfolio: Portfolio): Double {
        return score(portfolio) { d: Double -> d > 0.0 }
    }
}