import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun computeReturns(prices: List<String>): List<Double> {
    val typedPrices = prices.map { it.toDouble() }
    return typedPrices.mapIndexed { idx, price ->
        if (idx == typedPrices.size - 1) price else (price - typedPrices[idx+1]) / typedPrices[idx+1]
    }.subList(0, typedPrices.size - 1)
}

fun loadAssetReturns(fileName: String): List<Pair<String, List<Double>>> {
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
    if (assetsToPriceLists.containsKey("date")) {
        assetsToPriceLists.remove("date")
    }
    return assetsToPriceLists.map { Pair(it.key, computeReturns(it.value)) }
}
