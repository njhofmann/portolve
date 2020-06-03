import portfolio.Allocation
import kotlin.math.abs
import kotlin.math.round

fun isNotUnitValue(value: Double): Boolean {
    return !(0.0 < value && value < 1.0)
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

fun <T> randomItemNoReplacement(items: MutableSet<T>): T {
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