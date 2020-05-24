/**
 * Simple argument parser that defines arguments by a name, a count of required parameters, and
 * a required flag
 */
class ArgParser {

    private data class Arg(val name: String, val abbrev: String, val paramCount: Int,
                           val required: Boolean)

    private val args: MutableList<Arg> = ArrayList()

    fun addArg(name: String, abbrev: String, paramCount: Int, required: Boolean) {
        if (paramCount < 0) {
            throw IllegalArgumentException("argument must have >= 0 params")
        }
        else if (name.isBlank() || abbrev.isBlank()) {
            throw IllegalArgumentException("can't have empty or blank arguments")
        }
        else if (this.args.any { x: Arg -> name == x.name || abbrev == x.abbrev }) {
            throw IllegalArgumentException(
                    "duplicate argument with name %s and abbrev %s".format(name, abbrev))
        }

        args.add(Arg(name, abbrev, paramCount, required))
    }

    private fun getRequiredArgs(): MutableSet<String> {
        return args.filter { it.required }.map { it.name }.toMutableSet()
    }

    private fun isValidArgName(arg: String, namedArg: Arg): Boolean {
        return arg == namedArg.name || arg == ("-" + namedArg.abbrev)
    }

    private fun removeRequiredArg(curArg: String, namedArg: Arg,
                                  requiredArgs: MutableSet<String>): MutableSet<String> {
        if (namedArg.required) {
            if (requiredArgs.contains(curArg)) {
                requiredArgs.remove(curArg)
            }
            else {
                throw IllegalArgumentException("required argument %s found again".format(curArg))
            }
        }
        return requiredArgs
    }

    fun parse(arguments: List<String>): Map<String, List<String>> {
        // TODO check all required arguments are required
        // TODO check no duplicate arguments
        // TODO check for unassigned arguments
        var requiredArgs: MutableSet<String> = this.getRequiredArgs()
        val argsToParams: MutableMap<String, List<String>> = HashMap()
        var idx = 0
        var curArg: String?
        while (idx < arguments.size) {
            curArg = arguments[idx]
            // TODO prevent duplicate args!!
            for (namedArg in this.args) {
                if (this.isValidArgName(curArg, namedArg)) {
                    argsToParams[curArg] = arguments.subList(idx + 1, idx + namedArg.paramCount + 1)
                    idx += namedArg.paramCount
                    requiredArgs = removeRequiredArg(curArg, namedArg, requiredArgs)
                    break
                }
            }
            idx++
        }

        if (requiredArgs.isNotEmpty()) {
            throw IllegalArgumentException(
                    "missing required args %s".format(requiredArgs.toString()))
        }
        return argsToParams
    }
}