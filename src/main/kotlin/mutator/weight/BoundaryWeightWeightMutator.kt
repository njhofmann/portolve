package mutator.weight

class BoundaryWeightWeightMutator(mutationRate: Double, finalMutationRate: Double? = null, iterations: Int? = null,
                                  private val boundary: Double) :
        AbstractWeightMutator(mutationRate, finalMutationRate, iterations) {

    override fun getMutationValue(): Double {
        return boundary
    }
}