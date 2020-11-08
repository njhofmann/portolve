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
    parser.addArg("fitness", "f", true,
        """
        fitness / scoring strategy to rate the quality of each portfolio, each score is trying to be maximized:
        - Mean Variance (mean-var): from Modern Portfolio Theory; weights the expected return and risk are combined into
          a single objective control by unit weight c as (c * risk) - ((1 - c) * return)
            - takes in a single unit double c to control the weighting between expected risk and return, if 0 only 
              considers return and only risk if 1
        - Sharpe ratio (sharpe): ratio of expected excess returns (portfolio returns - risk free return rate) to its
          volatility as measured by standard deviations of excess returns
            - takes in another path to risk-free return rates
        - Sortino ratio (sortino): like the Sharpe ratio, but differentiating harmful volatility from overall volatility
          by only considering negative returns
            - takes in another path to risk-free return rates
        - Treynor measure (treynor): reward-to-volatility ratio of a portfolio; defines the ratio of expected excess
          returns (expected return of the portfolio minus a risk-free return rate), to systemic risk as a portfolio's
          beta (tendency of a portfolio's return to change in response to the overall market)
            - takes in another path to risk-free return rates
            - takes in another path to overall market prices
    """.trimIndent())
    parser.addArg("weightMutate", "wm", true,
        """
        strategy for mutating the weights of a portfolio, requires:
        - a double as the likelihood an asset is selected
        - double for the range of percentages a weight can be mutated from, from [-r, +r] where r is the double
        - (optional) double as a "final" weight mutation rate that will be linearly annealed to over the course of
              evolution
                
        following strategies to select from the mutation range
        - uniform: select as a uniform distribution
        - gaussian: select as a Gaussian distribution, bounded at +/- two standard deviation
        - boundary: just return the mutation rate, positive or negative
    """.trimIndent())
    parser.addArg("assetMutate", "am", true,
    """
        strategy for mutating assets making up a portfolio, requires:
        - double as the likelihood an asset is chosen for mutation
        - (optional) double as the "final" mutation rate that will be linearly annealed to over the course of evolution
          from the starting mutation rate
          
        supports the following strategies for selecting a new asset:
        - random: select a random asset from the universe assets not in the same portfolio
    """.trimIndent())
    parser.addArg("selector", "s", true,
    """
        strategy for pruning the weakest / selecting the best portfolios of a generation, requires:
        - double as the percentage of each generation's size to keep
        - (optional) double as the "final" selection rate that will be linearly annealed to over the course of evolution
          from the starting selection rate
          
        supports the following strategies:
        - tournament selection (tourny): 
            - takes the integer value of k before other parameters
        - truncation (trunc): keep the top percentage of each generation
        - roulette wheel (roulette): 
        - stochastic (stoch):
    """.trimIndent())
    parser.addArg("populator", "p", true, "repopulator strategy")
    parser.addArg("iterations", "i", false,
        """number of iterations / generations to run""")
    parser.addArg("popSize", "pp", true,
    """number of portfolios within each generation's population""")
    parser.addArg("portSize", "pf", true,
        """number of assets making up a portfolio""")
    parser.addArg("assetsFile", "af", true, "path to CSV info of assets prices")
    parser.addArg("boundary", "b", false,
    """largest weight an asset in a portfolio can take""")
    parser.addArg(
        "termThresh", "tt", false,
        """double that once a portfolio with a score greater than it is found, terminates the program"""
    )
    parser.addArg(
        "collect", "c", false,
        """"collection" threshold, stores any portfolios with a score above it"""
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
        val bestCurSolution = generation.maxByOrNull { it.second }!!
        if (bestSolution == null || bestSolution.second < bestCurSolution.second) {
            bestSolution = bestCurSolution
        }

        if (collectSolutions != null) {
            validSolutions.addAll(generation.filter { it.second > collectSolutions })
        }

        println("Generation $i, " +
                "best all: ${bestSolution.second}, " +
                "best: ${bestCurSolution.second}, " +
                "average: ${generation.map { it.second }.average()}")
        i++
    }

    if (bestSolution != null) {
        println("\nBest Solution: score - ${bestSolution.second}")
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