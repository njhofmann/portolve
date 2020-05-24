import portfolio.Allocation
import kotlin.IllegalArgumentException

public fun normalize(doubles: List<Double>): List<Double> {
    val sum = doubles.sum()
    return doubles.map { it / sum }
}

fun normAllocs(allocations: List<Allocation>): List<Allocation> {
    val normWeights = normalize(allocations.map { it.amount })
    return allocations.mapIndexed { idx, alloc -> Allocation(alloc.asset, normWeights[idx]) }
}


fun getPercentAtTick(startingPercent: Double, curTick: Int, maxTick: Int?): Double {
    if (0 > startingPercent || startingPercent > 1) {
        throw IllegalArgumentException("starting percentage must be in (0, 1), exclusive")
    }
    if (curTick < 0 || (maxTick != null && maxTick < 0)) {
        throw IllegalArgumentException("ticks all must be > 0")
    } else if (maxTick == null) {
        return startingPercent
    } else if (curTick > maxTick) {
        throw IllegalArgumentException("current tick %d is greater than max tick %s")
    }
    return (1 - (curTick / maxTick)) * startingPercent
}