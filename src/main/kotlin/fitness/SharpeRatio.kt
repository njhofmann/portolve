package fitness

import PositiveInt
import org.nield.kotlinstatistics.geometricMean
import org.nield.kotlinstatistics.standardDeviation
import portfolio.Allocation
import portfolio.DefaultPortfolio
import portfolio.Portfolio
import kotlin.math.pow
import kotlin.math.sqrt


class SharpeRatio(assetsToReturns: List<Pair<String, List<Double>>>,
                  rateFreeReturns: List<Double>,
                  annualizationRate: PositiveInt) :
    AbstractSharpeRatio(assetsToReturns, rateFreeReturns, annualizationRate) {


    override fun score(portfolio: Portfolio): Double {
        return score(portfolio, null)
    }
}