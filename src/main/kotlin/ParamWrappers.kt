class UnitValue(val num: Double) {
    init {
        checkIsUnitValue(num)
    }
}


class PositiveInt(val num: Int) {
    init {
        if (num < 1) {
            throw IllegalArgumentException("must be > 0")
        }
    }
}


class MaxAllocation(value: Double, portfolioSize: Int) {

    val num: Double = UnitValue(value).num

    init {
        if ((value * portfolioSize) < 1) {
            throw IllegalArgumentException("max asset allocation must be able to fill a complete portfolio")
        }
    }
}