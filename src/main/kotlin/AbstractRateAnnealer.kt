/**
 * Provides linear annealing between two numbers over some number of iterations, annealing is optional however
 * @param startPercent: number to start annealing from
 * @param endPercent: number to end annealing at
 * @param iterations: number of steps to go from start to end
 */
abstract class AbstractRateAnnealer(private val startPercent: Double, private val endPercent: Double?,
                                    private val iterations: PositiveInt?) {

    /**
     * The current iteration this annealer is on
     */
    private var curIteration: Int = 0

    init {
        checkIsUnitValue(startPercent)
        if (!((iterations == null && endPercent == null) || (iterations != null && endPercent != null))) {
            throw IllegalArgumentException("iterations and end percent must both be given, or both absent")
        } else if (endPercent != null) {
            checkIsUnitValue(endPercent)
        }
    }

    /**
     * Retrieves the number this RateAnnealer is currently on, if it is called more times than the number of given
     * iterations - an error is thrown. If annealing is not set, starting number is given
     * @return: current annealing rate
     */
    protected fun getPercentAtTick(): Double {
        if (iterations == null && endPercent == null) {
            return startPercent
        }
        else if (curIteration >= iterations!!.num) {
            throw IllegalStateException("more iterations have passed than allotted for")
        }
        val diff = (endPercent!! - startPercent) * (curIteration * 1.0 / (iterations.num - 1))
        curIteration += 1
        return startPercent + diff
    }
}