abstract class AbstractRateAnnealer(private val startPercent: Double, private val endPercent: Double?,
                                    private val iterations: Int?) {

    private var curIteration: Int = 0

    init {
        if (isNotUnitValue(startPercent) || (endPercent != null && isNotUnitValue(endPercent))) {
            throw IllegalArgumentException("start and end percents must be unit values in (0, 1)")
        }
        else if (iterations != null && iterations < 1) {
            throw IllegalArgumentException("number of iterations must be positive")
        }
        else if (!((iterations == null && endPercent == null) || (iterations != null && endPercent != null))) {
            throw IllegalArgumentException("iterations and end percent must both be given, or both absent")
        }
    }

    protected fun getPercentAtTick(): Double {
        if (iterations == null && endPercent == null) {
            return startPercent
        }
        else if (curIteration > iterations!!) {
            throw RuntimeException("more iterations have passed than allotted for")
        }
        val diff = (endPercent!! - startPercent) * (curIteration / iterations)
        curIteration++
        return startPercent + diff
    }
}