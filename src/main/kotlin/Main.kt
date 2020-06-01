import fitness.FitnessMetric
import fitness.MeanVarianceMetric
import fitness.SharpeMetric
import mutator.asset.AssetMutator
import mutator.asset.RandomAssetMutator
import mutator.weight.BoundaryWeightMutator
import mutator.weight.GaussianWeightMutator
import mutator.weight.UniformWeightWeightMutator
import mutator.weight.WeightMutator
import populator.MultiPointPopulator
import populator.Populator
import populator.ShufflePopulator
import selector.*

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
    val params = if (args.size == 1) ArrayList() else args.subList(1, args.size)
    return Pair(args[0], params)
}

fun getWeightMutator(args: List<String>, iterations: Int): WeightMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "uniform" -> when (params.size) {
            2 -> UniformWeightWeightMutator(boundary = toDouble(params[0]),
                                            mutationRate = toDouble(params[1]))
            3 -> UniformWeightWeightMutator(boundary = toDouble(params[0]),
                                            mutationRate = toDouble(params[1]),
                                            finalMutationRate = toDouble(params[2]),
                                            iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        "gaussian" -> when (params.size) {
            2 -> GaussianWeightMutator(boundary = toDouble(params[0]),
                                        mutationRate = toDouble(params[1]))
            3 -> GaussianWeightMutator(boundary = toDouble(params[0]),
                                        mutationRate = toDouble(params[1]),
                                        finalMutationRate = toDouble(params[2]),
                                        iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        "boundary" -> when (params.size) {
            2 -> BoundaryWeightMutator(boundary = toDouble(params[0]),
                                            mutationRate = toDouble(params[1]))
            3 -> BoundaryWeightMutator(boundary = toDouble(params[0]),
                                        mutationRate = toDouble(params[1]),
                                        finalMutationRate = toDouble(params[2]),
                                        iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
    }
}

fun getAssetMutator(args: List<String>, assetUniverse: Int, iterations: Int): AssetMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "random" -> when (params.size) {
            1 -> RandomAssetMutator(assetUniverse = assetUniverse,
                                    mutationRate = getSingleDouble(params))
            2 -> RandomAssetMutator(assetUniverse = assetUniverse,
                                    mutationRate = toDouble(params[0]),
                                    finalMutationRate = toDouble(params[1]),
                                    iterations = iterations)
                else -> throw IllegalArgumentException()
            }
        else -> throw IllegalArgumentException("invalid asset mutator %s".format(args[0]))
    }
}

fun getFitnessMetric(args: List<String>, assetReturns: List<Pair<String, List<Double>>>): FitnessMetric {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "sharpe" -> {
            // TODO file name
            checkArgsSize(params, 1)
            SharpeMetric(assetsToReturns = assetReturns,
                            avgRateFreeReturn = toDouble(params[0]))
        }
        "mean-var" -> {
            checkArgsSize(params, 2)
            MeanVarianceMetric(assetsToReturns = assetReturns,
                                lambda = toDouble(params[0]))
        }
        else -> throw IllegalArgumentException("invalid fitness metric %s".format(args[0]))
    }
}

fun getPopulator(args: List<String>, assetUniverse: Int): Populator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "multi-point" -> {
            checkArgsSize(params, 1)
            MultiPointPopulator(crossoverPoints = getSingleInt(params),
                                assetUniverse = assetUniverse)
        }
        "shuffle" -> {
            checkArgsSize(params, 0)
            ShufflePopulator(assetUniverse = assetUniverse)
        }
        else -> throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
    }
}

fun getSelector(args: List<String>, iterations: Int): Selector {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "tourny" -> when (params.size) {
            2 -> TournamentSelector(tournySize = toInteger(params[0]),
                                    keepPercent = toDouble(params[1]))
            3 -> TournamentSelector(tournySize = toInteger(params[0]),
                                    keepPercent = toDouble(params[1]),
                                    endKeepPercent = toDouble(params[2]),
                                    iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        "trunc" -> when (params.size) {
            1 -> TruncateSelector(keepPercent = toDouble(params[0]))
            2 -> TruncateSelector(keepPercent = toDouble(params[0]),
                                    endKeepPercent = toDouble(params[1]),
                                    iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        "roulette" -> when (params.size) {
            1 -> RouletteWheelSelector(keepPercent = toDouble(params[0]))
            2 -> RouletteWheelSelector(keepPercent = toDouble(params[0]),
                                        endKeepPercent = toDouble(params[1]),
                                        iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        "stoch" -> when (params.size) {
            1 -> StochasticUniversalSelector(keepPercent = toDouble(params[0]))
            2 -> StochasticUniversalSelector(keepPercent = toDouble(params[0]),
                                                endKeepPercent = toDouble(params[1]),
                                                iterations = iterations)
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException("invalid weight mutator %s".format(args[0]))
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

    val iterations = getSingleInt(parsedArgs["iterations"]!!)
    val selector = getSelector(parsedArgs["selector"]!!, iterations)
    val populator = getPopulator(parsedArgs["populator"]!!, assetUniverse)
    val fitnessMetric = getFitnessMetric(parsedArgs["fitness"]!!, assets)
    val weightMutator = getWeightMutator(parsedArgs["weightMutate"]!!, iterations)
    val assetMutator = getAssetMutator(parsedArgs["assertMutate"]!!, assetUniverse, iterations)
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