import Portfolio
import kotlin.DoubleArray
import kotlin.collections.List
import kotlin.ranges
import kotlin.String

class DefaultPortfolio(override var assets: List<String>) : Portfolio {

    private static zeroThreshold: Int = .00001

    private var weights: MutableList<Double> = MutableList<Double>(assets.size) { 0.0}

    override val assetWeights: List<Double> = this.weights

    override val numOfAssets: Int
        get() = assets.size

    override val numOfWeightedAssets: Int
        get() = weights.filter({ x -> x > 0.00001 }).size

    override fun assetsToWeights(): Map<String, Double> {
        val map = HashMap<String, Double>(this.weights.size)
        for (i in 0..this.numOfAssets) {
            if (this.weights[i] < .00001)
            map.put(this.assets[i], this.weights[i])
        }
    }
}
