import evolver.Evolver
import evolver.PopulateThenPruneEvolver
import evolver.PruneThenPopulateEvolver
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
import portfolio.Portfolio
import selector.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Creates the ArgParser with all the necessary parameters for this project
 * @return: formatted ArgParser
 */
fun getParser(): ArgParser {
    val parser = ArgParser()
    parser.addArg("fitness", "f", true)
    parser.addArg("weightMutate", "wm", true)
    parser.addArg("assetMutate", "am", true)
    parser.addArg("selector", "s", true)
    parser.addArg("populator", "p", true)
    parser.addArg("iterations", "i", false)
    parser.addArg("popSize", "pp", true)
    parser.addArg("portSize", "pf", true)
    parser.addArg("assetsFile", "af", true)
    parser.addArg("boundary", "b", false)
    parser.addArg("terminate", "t", false)
    parser.addArg("collect", "c", false)
    return parser
}

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

fun getWeightMutator(args: List<String>, iterations: PositiveInt?): WeightMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "uniform" -> when (params.size) {
            2 -> UniformWeightWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1])
            )
            3 -> UniformWeightWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1]),
                finalMutationRate = toDouble(params[2]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        "gaussian" -> when (params.size) {
            2 -> GaussianWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1])
            )
            3 -> GaussianWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1]),
                finalMutationRate = toDouble(params[2]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        "boundary" -> when (params.size) {
            2 -> BoundaryWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1])
            )
            3 -> BoundaryWeightMutator(
                boundary = toDouble(params[0]),
                mutationRate = toDouble(params[1]),
                finalMutationRate = toDouble(params[2]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException("invalid weight mutator $type")
    }
}

fun getAssetMutator(args: List<String>, assetUniverse: Int, iterations: PositiveInt?): AssetMutator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "random" -> when (params.size) {
            1 -> RandomAssetMutator(
                assetUniverse = assetUniverse,
                mutationRate = getSingleDouble(params)!!
            )
            2 -> RandomAssetMutator(
                assetUniverse = assetUniverse,
                mutationRate = toDouble(params[0]),
                finalMutationRate = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException("invalid asset mutator $type")
    }
}

fun getFitnessMetric(args: List<String>, assetReturns: List<Pair<String, List<Double>>>): FitnessMetric {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "sharpe" -> {
            // TODO file name
            checkArgsSize(params, 0)
            SharpeMetric(
                assetsToReturns = assetReturns,
                avgRateFreeReturn = LinkedList<Double>()
            )
        }
        "mean-var" -> {
            checkArgsSize(params, 1)
            MeanVarianceMetric(
                assetsToReturns = assetReturns,
                lambda = toDouble(params[0])
            )
        }
        else -> throw IllegalArgumentException("invalid fitness metric $type")
    }
}

fun getPopulator(args: List<String>, assetUniverse: Int, maxAllocation: MaxAllocation?): Populator {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "multi-point" -> {
            checkArgsSize(params, 1)
            MultiPointPopulator(
                crossoverPoints = getSingleInt(params)!!,
                assetUniverse = assetUniverse,
                maxAllocation = maxAllocation
            )
        }
        "shuffle" -> {
            checkArgsSize(params, 0)
            ShufflePopulator(
                assetUniverse = assetUniverse,
                maxAllocation = maxAllocation
            )
        }
        else -> throw IllegalArgumentException("invalid weight mutator $type")
    }
}


fun getEvolver(
    args: List<String>, selector: Selector, populator: Populator, weightMutator: WeightMutator,
    assetMutator: AssetMutator, fitnessMetric: FitnessMetric, iterations: PositiveInt?, populationSize: Int,
    portfolioSize: Int, terminationThreshold: Double?, assetsCount: Int
): Evolver {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    checkArgsSize(params, 0)
    return when (type) {
        "pop-prune" -> {
            PopulateThenPruneEvolver(
                selector = selector,
                populator = populator,
                weightMutator = weightMutator,
                assetMutator = assetMutator,
                fitnessMetric = fitnessMetric,
                iterations = iterations,
                popSize = populationSize,
                portfolioSize = portfolioSize,
                terminateThreshold = terminationThreshold,
                assets = assetsCount
            )
        }
        "prune-pop" -> {
            PruneThenPopulateEvolver(
                selector = selector,
                populator = populator,
                weightMutator = weightMutator,
                assetMutator = assetMutator,
                fitnessMetric = fitnessMetric,
                iterations = iterations,
                popSize = populationSize,
                portfolioSize = portfolioSize,
                terminateThreshold = terminationThreshold,
                assets = assetsCount
            )
        }
        else -> throw IllegalArgumentException("invalid evolver strategy $type")
    }
}


fun getSelector(args: List<String>, iterations: PositiveInt?): Selector {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "tourny" -> when (params.size) {
            2 -> TournamentSelector(
                tournySize = toInteger(params[0]),
                keepPercent = toDouble(params[1])
            )
            3 -> TournamentSelector(
                tournySize = toInteger(params[0]),
                keepPercent = toDouble(params[1]),
                endKeepPercent = toDouble(params[2]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        "trunc" -> when (params.size) {
            1 -> TruncateSelector(keepPercent = toDouble(params[0]))
            2 -> TruncateSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        "roulette" -> when (params.size) {
            1 -> RouletteWheelSelector(keepPercent = toDouble(params[0]))
            2 -> RouletteWheelSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        "stoch" -> when (params.size) {
            1 -> StochasticUniversalSelector(keepPercent = toDouble(params[0]))
            2 -> StochasticUniversalSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException("invalid weight mutator $type")
    }
}

fun getSingleInt(args: List<String>?): Int? {
    if (args == null) {
        return null
    }
    checkArgsSize(args, 1)
    return toInteger(args[0])
}

fun getSingleDouble(args: List<String>?): Double? {
    if (args == null) {
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

fun parseMaxAllocation(args: List<String>?, portfolioSize: Int): MaxAllocation? {
    val value: Double? = getSingleDouble(args)
    return if (value == null) null else MaxAllocation(value, portfolioSize)
}

fun parsePositiveInt(args: List<String>?): PositiveInt? {
    val value: Int? = getSingleInt(args)
    return if (value == null) null else PositiveInt(value)
}

fun getAssetsFile(args: List<String>): List<Pair<String, List<Double>>> {
    checkArgsSize(args, 1)
    return loadAssetReturns(args[0])
}

fun printSolution(solution: List<Pair<String, Double>>) {
    solution.forEach { println("Asset: ${it.first}, Weight: ${it.second}") }
}

fun configToParser(): Map<String, List<String>> {
    val configLines = File("config").readLines().filter { it.isNotBlank() && !it.startsWith("#") }
    return configLines.associate {
        val tokens = it.split(" ").toList()
        val key = tokens.subList(0, 1)[0]
        val params = if (tokens.size <= 2) ArrayList() else tokens.subList(2, tokens.size)
        Pair(key, params)
    }
}

/**
 * "Names" the given Portfolio by reassociating the assets in the Portfolio with their respective String names,
 * in the given list of asset names. i-th asset corresponds to the i-th asset name. Returns a list of asset names
 * to their respective weights, from the original Portfolio.
 */
fun namePortfolio(portfolio: Portfolio, assets: List<String>): List<Pair<String, Double>> {
    return portfolio.allocations.map { Pair(assets[it.asset], it.amount) }
}


fun runEvolver(evolver: Evolver, assetNames: List<String>, collectSolutions: Double?) {
    var bestSolution: Pair<Portfolio, Double>? = null
    val validSolutions: MutableList<Pair<Portfolio, Double>> = LinkedList()
    var i = 0
    for (generation in evolver) {
        val bestCurSolution = generation.maxBy { it.second }!!
        if (bestSolution == null || bestSolution.second > bestCurSolution.second) {
            bestSolution = bestCurSolution
        }

        if (collectSolutions != null) {
            validSolutions.addAll(generation.filter { it.second > collectSolutions })
        }

        println("Generation $i: best - ${bestCurSolution.second}, average - ${generation.map { it.second }.average()}")
        i++
    }

    if (bestSolution != null) {
        println("Best Solution: score - ${bestSolution.second}")
        printSolution(namePortfolio(bestSolution.first, assetNames))
    }

    if (collectSolutions != null) {
        validSolutions.forEach {
            println("Score - ${it.second}")
            printSolution(namePortfolio(it.first, assetNames))
            println()
        }
    }
}

fun main(args: Array<String>) {
    val parser = getParser()
    val parsedArgs = when (args.size) {
        0 -> parser.parse(configToParser())
        else -> parser.parse(args.toList())
    }

    val assets = getAssetsFile(parsedArgs["assetsFile"]!!)
    val assetUniverse = assets.size

    val iterations: PositiveInt? = parsePositiveInt(parsedArgs["iterations"])
    val populationSize = getSingleInt(parsedArgs["popSize"]!!)!!
    val portfolioSize = getSingleInt(parsedArgs["portSize"]!!)!!
    val maxAllocation: MaxAllocation? = parseMaxAllocation(parsedArgs["boundary"], portfolioSize)
    val terminationThreshold: Double? = getSingleDouble(parsedArgs["terminate"])
    val collect: Double? = getSingleDouble(parsedArgs["collect"])  // TODO ME

    val selector = getSelector(parsedArgs["selector"]!!, iterations)
    val populator = getPopulator(parsedArgs["populator"]!!, assetUniverse, maxAllocation)
    val fitnessMetric = getFitnessMetric(parsedArgs["fitness"]!!, assets)
    val weightMutator = getWeightMutator(parsedArgs["weightMutate"]!!, iterations)
    val assetMutator = getAssetMutator(parsedArgs["assetMutate"]!!, assetUniverse, iterations)

    val assetNames = assets.map { it.first }

    val evolver = getEvolver(
        args = parsedArgs["evolver"]!!,
        selector = selector,
        populator = populator,
        weightMutator = weightMutator,
        assetMutator = assetMutator,
        fitnessMetric = fitnessMetric,
        iterations = iterations,
        populationSize = populationSize,
        portfolioSize = portfolioSize,
        terminationThreshold = terminationThreshold,
        assetsCount = assetNames.size
    )

    runEvolver(evolver, assetNames, collect)
}