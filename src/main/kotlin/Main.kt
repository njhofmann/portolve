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
    parser.addArg("iterations", "m", true)
    parser.addArg("popSize", "pp", true)
    parser.addArg("portfolioSize", "pf", true)
    return parser
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

fun printSolution(solution: List<Pair<String, Double>>) {
    solution.forEach {
        print("Asset: %s, Weight: %d\n".format(it.first, it.second))
    }
}

fun main(args: Array<String>) {
    val parsedArgs = getParser().parse(args.toList())
    // val solution: List<Pair<String, Double>> = Evolver().findSolution()
    //printSolution(solution)
}