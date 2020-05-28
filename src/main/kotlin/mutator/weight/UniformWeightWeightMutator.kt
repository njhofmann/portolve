package mutator.weight

import isNotUnitValue
import kotlin.random.Random

class UniformWeightWeightMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?,
                                 private val boundary: Double) :
    AbstractWeightMutator(mutationRate, finalMutationRate, iterations) {

    init {
        if (isNotUnitValue(boundary)) {
            throw IllegalArgumentException("mutation boundary must be in (0, 1)")
        }
    }

    override fun getMutationValue(): Double {
        return Random.nextDouble(-boundary, boundary)
    }
}