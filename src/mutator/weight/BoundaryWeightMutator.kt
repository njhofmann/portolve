package mutator.weight

import portfolio.Portfolio
import java.util.Random

class BoundaryWeightMutator(private val mutationRate: Double, private val boundary: Double) :
        AbstractMutator(mutationRate) {

    override fun getMutationValue(): Double {
        return boundary
    }
}