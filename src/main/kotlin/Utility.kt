import portfolio.Allocation
import java.util.*
import kotlin.math.abs
import kotlin.math.round

fun checkIsUnitValue(value: Double) {
    if (value !in 0.0..1.0) {
        throw IllegalArgumentException("param must be in range of (0, 1)")
    }
}

fun equalDoubles(a: Double, b: Double): Boolean {
    return abs(a - b) < .00001
}

fun normalize(doubles: List<Double>): List<Double> {
    val sum = doubles.sum()
    return doubles.map { it / sum }
}

fun roundToNearestInt(num: Double): Int {
    return round(num).toInt()
}

fun <T> randomItemNoReplace(items: MutableSet<T>): T {
    if (items.isEmpty()) {
        throw IllegalArgumentException("can't select from empty set")
    }
    val item = items.random()
    items.remove(item)
    return item
}

fun normAllocs(allocations: List<Allocation>): List<Allocation> {
    val normWeights = normalize(allocations.map { it.amount })
    return allocations.mapIndexed { idx, alloc -> Allocation(alloc.asset, normWeights[idx]) }
}

fun <T> unzipPairs(pairs: List<Pair<T, T>>): LinkedList<T> {
    val lists: LinkedList<T> = LinkedList()
    pairs.forEach { lists.add(it.first); lists.add(it.second) }
    return lists
}