package mutator.weight

class BoundaryWeightWeightMutator(private val boundary: Double, mutationRate: Double, finalMutationRate: Double? = null,
                                  iterations: Int? = null) :
        AbstractWeightMutator(mutationRate, finalMutationRate, iterations) {

    override fun getMutationValue(): Double {
        return boundary
    }
}