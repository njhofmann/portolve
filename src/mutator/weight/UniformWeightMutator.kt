package mutator.weight

import portfolio.Portfolio
import kotlin.random.Random

class UniformWeightMutator(private val mutationRate: Double, private val boundary: Double) :
        AbstractMutator(mutationRate) {

    init {
        if (isNotUnitValue(boundary)) {
            throw IllegalArgumentException("mutation bounds must be in (0, 1)")
        }
    }

    override fun getMutationValue(): Double {
        return Random.nextDouble(-boundary, boundary)
    }
}