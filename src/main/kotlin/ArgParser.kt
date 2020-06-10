/**
 * Simple argument parser that defines arguments via a name, an abbreviation, and a required flag
 */
class ArgParser {

    /**
     * An argument comprised of a name, an abbreviation, and required flag
     */
    private data class Arg(val name: String, val abbrev: String, val required: Boolean)

    /**
     * Args that have been added to this Parser
     */
    private val args: MutableList<Arg> = ArrayList()

    /**
     * Adds an argument to this parser, name and abbreviation must not match an previously added named or abbreviation
     * @param name: name of the argument
     * @param abbrev: abbreviation of the argument
     * @param required: if the argument should be required
     */
    fun addArg(name: String, abbrev: String, required: Boolean) {
        if (name.isBlank() || abbrev.isBlank()) {
            throw IllegalArgumentException("can't have empty or blank arguments")
        } else if (this.args.any { name == it.name || abbrev == it.abbrev || name == it.abbrev || abbrev == it.name }) {
            throw IllegalArgumentException("duplicate argument with name %s and abbrev %s".format(name, abbrev))
        }
        args.add(Arg(name, abbrev, required))
    }

    /**
     * Retrieves the set of required argument names registered with this parser so far
     * @return: parser's required argument names
     */
    private fun getRequiredArgs(): Set<String> {
        return args.filter { it.required }.map { it.name }.toSet()
    }

    /**
     * Returns if the given string is a valid match to the given argument - ie does it match the argument's name or
     * abbreviation, with a '-' before it
     * @param arg: String to match
     * @param namedArg: argument to match against
     * @return: if given String matches given Arg
     */
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

    /**
     * Returns if the given 'small' set is a subset of the 'large' set
     * @param small: smaller set
     * @param large: larger set
     * @return: if smaller is subset of large
     */
    private fun <T> isSubset(small: Set<T>, large: Set<T>): Boolean {
        if (small.size > large.size) {
            throw IllegalArgumentException("small set is larger than large set")
        }
        return small.all { large.contains(it)  }
    }

    /**
     * Parses the given list of string into a mapping of argument to any parameters that may follow. List must contain
     * all the requirements arguments of this parser, must not contain any arguments not registered with this parser.
     * @param arguments: list of strings to parse
     * @return: mapping of entered arguments to parameters
     */
    fun parse(arguments: List<String>): Map<String, List<String>> {
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