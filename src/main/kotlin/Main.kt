import evolver.Evolver
import portfolio.Portfolio
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Creates the ArgParser with all the necessary parameters for this project
 * @return: formatted ArgParser
 */
fun getParser(): ArgParser {
    val parser = ArgParser()
    parser.addArg("fitness", "f", true, "fitness / scoring strategy to use")
    parser.addArg("weightMutate", "wm", true, "weight mutation strategy")
    parser.addArg("assetMutate", "am", true, "asset mutation strategy")
    parser.addArg("selector", "s", true, "populator selection strategy")
    parser.addArg("populator", "p", true, "repopulator strategy")
    parser.addArg("iterations", "i", false, "number of iterations / generations to produce")
    parser.addArg("popSize", "pp", true, "size of generation's population")
    parser.addArg("portSize", "pf", true, "size of each portfolio")
    parser.addArg("assetsFile", "af", true, "path to assets file")
    parser.addArg("boundary", "b", false, "greatest weight in portfolio an asset can take")
    parser.addArg(
        "termThresh", "tt", false,
        "fitness score to terminate program once a solution reaches it"
    )
    parser.addArg(
        "collect", "c", false,
        "to collect any solutions that are above the terminate threshold"
    )
    return parser
}

fun parseMaxAllocation(args: List<String>?, portfolioSize: Int): MaxAllocation? {
    val value: Double? = getSingleDouble(args)
    return if (value == null) null else MaxAllocation(value, portfolioSize)
}

fun getAssetsFile(args: List<String>): List<Pair<String, List<Double>>> {
    checkArgsSize(args, 1)
    return loadAssetReturns(args[0])
}

fun printSolution(solution: List<Pair<String, Double>>) {
    solution.forEach { println("Asset: ${it.first}, Weight: ${it.second}") }
}

fun configFileToRawUserArgs(): Map<String, List<String>> {
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
        0 -> parser.parse(configFileToRawUserArgs())
        else -> parser.parse(args.toList())
    }

    val assets = getAssetsFile(parsedArgs["assetsFile"]!!)
    val assetUniverse = assets.size

    val iterations: PositiveInt? = parsePositiveInt(parsedArgs["iterations"])
    val populationSize = getSingleInt(parsedArgs["popSize"]!!)!!
    val portfolioSize = getSingleInt(parsedArgs["portSize"]!!)!!
    val maxAllocation: MaxAllocation? = parseMaxAllocation(parsedArgs["boundary"], portfolioSize)
    val terminationThreshold: Double? = getSingleDouble(parsedArgs["termThresh"])
    val collect: Double? = getSingleDouble(parsedArgs["collect"])

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