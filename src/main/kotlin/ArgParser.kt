/**
 * Simple argument parser that defines arguments by a name and a required flag
 */
class ArgParser {

    private data class Arg(val name: String, val abbrev: String, val required: Boolean)

    private val args: MutableList<Arg> = ArrayList()

    fun addArg(name: String, abbrev: String, required: Boolean) {
        if (name.isBlank() || abbrev.isBlank()) {
            throw IllegalArgumentException("can't have empty or blank arguments")
        } else if (this.args.any { name == it.name || abbrev == it.abbrev || name == it.abbrev || abbrev == it.name }) {
            throw IllegalArgumentException("duplicate argument with name %s and abbrev %s".format(name, abbrev))
        }
        args.add(Arg(name, abbrev, required))
    }

    private fun getRequiredArgs(): Set<String> {
        return args.filter { it.required }.map { it.name }.toSet()
    }

    private fun isValidArgName(arg: String, namedArg: Arg): Boolean {
        return arg == ("-" + namedArg.name) || arg == ("-" + namedArg.abbrev)
    }

    private fun getArgName(arg: String): String {
        args.forEach {
            if (isValidArgName(arg, it)) {
                return it.name
            }
        }
        throw RuntimeException("%s is an unregistered argument".format(arg))
    }

    private fun findNextParam(args: List<String>, startIdx: Int): Int {
        if (!args[startIdx].startsWith('-')) {
            throw IllegalArgumentException("argument list must begin with an argument")
        }
        var endIdx = startIdx + 1
        while (endIdx < args.size && !args[endIdx].startsWith('-')) {
            endIdx++
        }
        return endIdx
    }

    private fun findParams(args: List<String>): Map<String, List<String>> {
        val argsToParams: MutableMap<String, List<String>> = HashMap()
        var idx = 0
        while (idx <= (args.size - 1)) {
            val endIdx = findNextParam(args, idx)
            val argName = getArgName(args[idx])
            argsToParams[argName] = args.subList(idx + 1, endIdx)
            idx = endIdx
        }
        return argsToParams
    }

    private fun <T> isSubset(small: Set<T>, large: Set<T>): Boolean {
        if (small.size > large.size) {
            throw IllegalArgumentException("small set is larger than large set")
        }
        return small.all { large.contains(it)  }
    }

    fun parse(arguments: List<String>): Map<String, List<String>> {
        // TODO check all required arguments are required
        // TODO check no duplicate arguments
        val requiredArgs: Set<String> = this.getRequiredArgs()
        val argNamesToParams: Map<String, List<String>> = findParams(arguments)
        if (!isSubset(requiredArgs, argNamesToParams.keys)) {
            throw IllegalArgumentException("missing required args %s".format(requiredArgs - argNamesToParams.keys))
        }

        val uniqueArgs = argNamesToParams.keys.distinct()
        if (argNamesToParams.size > uniqueArgs.size) {
            throw IllegalArgumentException("have duplicate arguments %s".format(argNamesToParams - uniqueArgs))
        }
        return argNamesToParams
    }
}