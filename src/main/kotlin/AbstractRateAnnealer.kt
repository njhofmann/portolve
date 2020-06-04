abstract class AbstractRateAnnealer(private val startPercent: Double, private val endPercent: Double?,
                                    private val iterations: PositiveInt?) {

    private var curIteration: Int = 0

    init {
        isNotUnitValue(startPercent)
        if (endPercent != null) {
            isNotUnitValue(endPercent)
        }

        if (!((iterations == null && endPercent == null) || (iterations != null && endPercent != null))) {
            throw IllegalArgumentException("iterations and end percent must both be given, or both absent")
        }
    }

    protected fun getPercentAtTick(): Double {
        if (iterations == null && endPercent == null) {
            return startPercent
        }
        else if (curIteration > iterations!!.num) {
            throw IllegalStateException("more iterations have passed than allotted for")
        }
        val diff = (endPercent!! - startPercent) * (curIteration / iterations.num)
        curIteration++
        return startPercent + diff
    }
}