import fitness.FitnessMetric
import mutator.asset.AssetMutator
import mutator.weight.WeightMutator
import populator.Populator
import selector.Selector

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

fun getWeightMutator(args: List<String>): WeightMutator {

}

fun getAssetMutator(args: List<String>): AssetMutator {

}

fun getFitnessMetric(args: List<String>): FitnessMetric {

}

fun getPopulator(args: List<String>): Populator {

}

fun getSelector(args: List<String>): Selector {

}

fun getSingleInt(args: List<String>): Int {
    checkArgsSize(args, 1)
    try {
        return args[0].toInt()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("expected single argument to be an integer")
    }
}

fun getSingleDouble(args: List<String>): Double {
    checkArgsSize(args, 1)
    try {
        return args[0].toDouble()
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
    val selector = getSelector(parsedArgs["selector"]!!)
    val populator = getPopulator(parsedArgs["populator"]!!)
    val fitnessMetric = getFitnessMetric(parsedArgs["fitness"]!!)
    val weightMutator = getWeightMutator(parsedArgs["weightMutate"]!!)
    val assetMutator = getAssetMutator(parsedArgs["assertMutate"]!!)
    val iterations = getSingleInt(parsedArgs["iterations"]!!)
    val populationSize = getSingleInt(parsedArgs["popSize"]!!)
    val portfolioSize = getSingleInt(parsedArgs["portSize"]!!)
    val terminationThreshold = getSingleDouble(parsedArgs["terminate"]!!)
    val assets = getAssetsFile(parsedArgs["assetsFile"]!!)

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