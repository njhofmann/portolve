import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.asset.RandomAssetMutator
import mutator.weight.WeightMutator
import populator.MultiPointPopulator
import populator.Populator
import populator.ShufflePopulator
import selector.Selector
import kotlin.reflect.KClass

fun getParser(): ArgParser {
    val parser = ArgParser()
    parser.addArg("fitness", "f", true)
    parser.addArg("weightMutate", "wm", true)
    parser.addArg("assetMutate", "am", true)
    parser.addArg("selector", "s", true)
    parser.addArg("populator", "p", true)
    parser.addArg("iterations", "m", true)
    parser.addArg("popSize", "pp", true)
    parser.addArg("portSize", "pf", true)
    parser.addArg("terminate", "t", false)
    parser.addArg("assetsFile", "af", true)
    return parser
}

fun checkArgsSize(args: List<String>, expectedSize: Int) {
    if (args.size != expectedSize) {
        throw IllegalArgumentException("expected a %d arguments".format(expectedSize))
    }
}

fun checkNonZeroArgs(args: List<String>) {
    if (args.isEmpty()) {
        throw IllegalArgumentException("expected at least one arg")
    }
}

fun splitListHead(args: List<String>): Pair<String, List<String>> {
    val params = if (args.size == 1) ArrayList<String>() else args.subList(1, args.size)
    return Pair(args[0], params)
}

fun getWeightMutator(args: List<String>): WeightMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    when (type) {
        "uniform" -> null
        "gaussian" -> null
        "boundary" -> null
        else -> {
            throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
        }
    }
}

fun getAssetMutator(args: List<String>, assetUniverse: Int): AssetMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    when (type) {
        "random" -> {
            return when (params.size) {
                1 -> RandomAssetMutator(assetUniverse = assetUniverse,
                                        mutationRate = getSingleDouble(params))
                3 -> RandomAssetMutator(assetUniverse = assetUniverse,
                                        mutationRate = toDouble(params[0]),
                                        finalMutationRate = toDouble(params[1]),
                                        iterations = toInteger(params[2]))
                else -> {
                    // TODO fill out
                    throw IllegalArgumentException()
                }
            }
        }
        else -> {
            throw IllegalArgumentException("invalid asset mutator %s".format(args[0]))
        }
    }
}

fun getFitnessMetric(args: List<String>): FitnessMetric {
    checkNonZeroArgs(args)
    when (args[0]) {
        "sharpe" -> null
        "mean-var" -> null
        "treynor" -> null
        else -> {
            throw IllegalArgumentException("invalid fitness metric %s".format(args[0]))
        }
    }
}

fun getPopulator(args: List<String>, assetUniverse: Int): Populator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "multi-point" -> {
            checkArgsSize(params, 1)
            MultiPointPopulator(getSingleInt(params), assetUniverse)
        }
        "shuffle" -> {
            checkArgsSize(params, 0)
            ShufflePopulator(assetUniverse)
        }
        else -> {
            throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
        }
    }
}

fun getSelector(args: List<String>): Selector {
    checkNonZeroArgs(args)
    when (args[0]) {
        "tourny" -> null
        "trunc" -> null
        "roulette" -> null
        "stoch" -> null
        else -> {
            throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
        }
    }
}

fun getSingleInt(args: List<String>): Int {
    checkArgsSize(args, 1)
    return toInteger(args[0])
}

fun getSingleDouble(args: List<String>): Double {
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

fun getAssetsFile(args: List<String>): List<Pair<String, List<Double>>> {
    checkArgsSize(args, 1)
    return loadAssetReturns(args[0])
}

fun printSolution(solution: List<Pair<String, Double>>) {
    solution.forEach { print("Asset: %s, Weight: %d\n".format(it.first, it.second)) }
}

fun main(args: Array<String>) {
    val parsedArgs = getParser().parse(args.toList())

    val assets = getAssetsFile(parsedArgs["assetsFile"]!!)
    val assetUniverse = assets.size

    val selector = getSelector(parsedArgs["selector"]!!)
    val populator = getPopulator(parsedArgs["populator"]!!, assetUniverse)
    val fitnessMetric = getFitnessMetric(parsedArgs["fitness"]!!)
    val weightMutator = getWeightMutator(parsedArgs["weightMutate"]!!)
    val assetMutator = getAssetMutator(parsedArgs["assertMutate"]!!)
    val iterations = getSingleInt(parsedArgs["iterations"]!!)
    val populationSize = getSingleInt(parsedArgs["popSize"]!!)
    val portfolioSize = getSingleInt(parsedArgs["portSize"]!!)
    val terminationThreshold = getSingleDouble(parsedArgs["terminate"]!!)


    val evolver = Evolver(
        selector = selector,
        populator = populator,
        weightMutator = weightMutator,
        assetMutator = assetMutator,
        fitnessMetric = fitnessMetric,
        iterations = iterations,
        popSize = populationSize,
        portfolioSize = portfolioSize,
        terminateThreshold = terminationThreshold,
        assets = assets.map { it.first }
    )
    val solution: List<Pair<String, Double>> = evolver.findSolution()
    printSolution(solution)
}