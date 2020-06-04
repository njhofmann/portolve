package mutator.weight

import MaxAllocation
import PositiveInt
import java.util.*

class GaussianWeightMutator(boundary: Double, mutationRate: Double, finalMutationRate: Double? = null,
                            iterations: PositiveInt? = null, maxAllocation: MaxAllocation? = null) :
        AbstractWeightMutator(boundary, mutationRate, finalMutationRate, iterations, maxAllocation) {

    private val randGenerator = Random()

    private val standardDev = 2.0

    private fun pruneInRange(value: Double): Double {
        if (value > standardDev) return standardDev
        if (value < -standardDev) return -standardDev
        return value
    }

    override fun getMutationValue(): Double {
        return pruneInRange(randGenerator.nextGaussian()) / this.boundary
    }
}