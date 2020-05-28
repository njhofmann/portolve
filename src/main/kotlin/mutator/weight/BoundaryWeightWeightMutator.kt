package mutator.weight

class BoundaryWeightWeightMutator(mutationRate: Double, finalMutationRate: Double?, iterations: Int?,
                                  private val boundary: Double) :
        AbstractWeightMutator(mutationRate, finalMutationRate, iterations) {

    override fun getMutationValue(): Double {
        return boundary
    }
}