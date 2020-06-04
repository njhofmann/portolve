package mutator.weight

import MaxAllocation
import PositiveInt
import kotlin.random.Random

class UniformWeightWeightMutator(boundary: Double, mutationRate: Double, finalMutationRate: Double? = null,
                                 iterations: PositiveInt? = null, maxAllocation: MaxAllocation? = null) :
    AbstractWeightMutator(boundary, mutationRate, finalMutationRate, iterations, maxAllocation) {

    override fun getMutationValue(): Double {
        return Random.nextDouble(-boundary, boundary)
    }
}