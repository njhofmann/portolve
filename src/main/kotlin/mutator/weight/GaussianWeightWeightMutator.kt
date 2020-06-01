package mutator.weight

import isNotUnitValue
import java.util.*

class GaussianWeightWeightMutator(mutationRate: Double, finalMutationRate: Double? = null, iterations: Int? = null,
                                  private val boundary: Double) :
        AbstractWeightMutator(mutationRate, finalMutationRate, iterations) {

    private val randGenerator = Random()

    private val standardDev = 2.0

    init {
        if (isNotUnitValue(boundary)) {
            throw IllegalArgumentException("boundary not in range of (0, 1)")
        }
    }

    private fun pruneInRange(value: Double): Double {
        if (value > standardDev) return standardDev
        if (value < -standardDev) return -standardDev
        return value
    }

    override fun getMutationValue(): Double {
        return pruneInRange(randGenerator.nextGaussian()) / this.boundary
    }
}