package mutator.weight

import java.util.*

class GaussianWeightMutator(private val mutationRate: Double, private val boundary: Double) :
        AbstractMutator(mutationRate) {

    private val generator = Random()

    private val standardDev = 2.0

    init {
        if (isNotUnitValue(boundary)) {
            throw IllegalArgumentException("boundary not in range of (0, 1)")
        }
    }

    private fun pruneInRange(value: Double): Double {
        if (value > standardDev) {
            return standardDev
        }
        else if (value < -standardDev) {
            return -standardDev
        }
        return value
    }

    override fun getMutationValue(): Double {
        var value = generator.nextGaussian()
        value = pruneInRange(value)
        return value / this.boundary
    }
}