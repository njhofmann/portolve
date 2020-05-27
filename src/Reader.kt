import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun mean(nums: List<Double>): Double {
    return nums.sum() / nums.size
}

fun computeReturns(prices: List<String>): List<Double> {
    val typedPrices = prices.map { it.toDouble() }
    return typedPrices.mapIndexed { idx, price ->
        if (idx == typedPrices.size - 1) {
            price
        }
        (price - typedPrices[idx+1]) / typedPrices[idx+1]
    }
}

fun loadAssetReturns(fileName: String): List<Pair<String, Double>> {
    val file = File(fileName)
    val assetsToPrices: List<Map<String, String>> = csvReader().readAllWithHeader(file)
    val assetsToPriceLists = HashMap<String, MutableList<String>>().withDefault { LinkedList<String>() }
    assetsToPrices.forEach { it.forEach { (key, value) -> assetsToPriceLists[key]?.add(value) } }

    val assetsToAvgReturns = ArrayList<Pair<String, Double>>()
    assetsToPriceLists.forEach { asset, prices ->
        val avgReturn = mean(computeReturns(prices))
        assetsToAvgReturns.add(Pair(asset, avgReturn))
    }
    return assetsToAvgReturns
}
