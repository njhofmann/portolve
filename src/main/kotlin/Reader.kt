import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun computeReturns(prices: List<Double>): List<Double> {
    return prices.mapIndexed { idx, price ->
        if (idx == prices.size - 1) price else (price - prices[idx+1]) / prices[idx+1]
    }.subList(0, prices.size - 1)
}

fun loadAssetReturns(fileName: String, compute: Boolean = true): List<Pair<String, List<Double>>> {
    val file = File(fileName)
    val assetsToPrices: List<Map<String, String>> = csvReader().readAllWithHeader(file)
    val assetsToPriceLists = HashMap<String, MutableList<String>>()
    assetsToPrices.forEach {
        it.forEach { (key, value) ->
            if (!assetsToPriceLists.contains(key)) {
                assetsToPriceLists[key] = LinkedList()
            }
            assetsToPriceLists[key]?.add(value)
        }
    }
    if (assetsToPriceLists.containsKey("dates")) {
        assetsToPriceLists.remove("dates")
    }
    val assetsToReturns = assetsToPriceLists.map { item -> Pair(item.key, item.value.map { it.toDouble() }) }
    if (!compute) {
        return assetsToReturns
    }
    return assetsToReturns.map { Pair(it.first, computeReturns(it.second)) }
}
