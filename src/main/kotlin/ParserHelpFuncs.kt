fun checkArgsSize(args: List<String>, expectedSize: Int) {
    if (args.size != expectedSize) {
        throw IllegalArgumentException("expected a $expectedSize arguments")
    }
}

fun checkNonZeroArgs(args: List<String>) {
    if (args.isEmpty()) {
        throw IllegalArgumentException("expected at least one arg")
    }
}

fun splitListHead(args: List<String>): Pair<String, List<String>> {
    val params = if (args.size == 1) ArrayList() else args.subList(1, args.size)
    return Pair(args[0], params)
}

fun getSingleInt(args: List<String>?): Int? {
    if (args == null || args.isEmpty()) {
        return null
    }
    checkArgsSize(args, 1)
    return toInteger(args[0])
}

fun getSingleDouble(args: List<String>?): Double? {
    if (args == null || args.isEmpty()) {
        return null
    }
    checkArgsSize(args, 1)
    return toDouble(args[0])
}

fun toInteger(str: String): Int {
    try {
        return str.toInt()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("expected single argument to be a double")
    }
}

fun toDouble(str: String): Double {
    try {
        return str.toDouble()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("expected single argument to be a double")
    }
}

fun getSingleBoolean(args: List<String>?): Boolean? {
    if (args == null) {
        return null
    }
    checkArgsSize(args, 1)
    return args[0].toBoolean()
}

fun parsePositiveInt(args: List<String>?): PositiveInt? {
    val value: Int? = getSingleInt(args)
    return if (value == null) null else PositiveInt(value)
}