import evolver.Evolver
import evolver.PopulateThenPruneEvolver
import evolver.PruneThenPopulateEvolver
import fitness.*
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
            else -> throw IllegalArgumentException("invalid number of parameters for uniform weight mutator")
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
            else -> throw IllegalArgumentException("invalid number of parameters for Gaussian weight mutator")
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
            else -> throw IllegalArgumentException("invalid number of parameters for boundary weight mutator")
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
            else -> throw IllegalArgumentException("invalid number of parameters for random asset mutation")
        }
        else -> throw IllegalArgumentException("invalid asset mutator $type")
    }
}

fun getFitnessMetric(args: List<String>, assetReturns: List<Pair<String, List<Double>>>): FitnessMetric {
    checkNonZeroArgs(args)
    val (type, params) = splitListHead(args)
    return when (type) {
        "return-risk" -> when (params.size) {
            // TODO figure out how to do optional arguments for 2-3 arg counts. subparsing?
            1 -> ReturnRiskRatio(
                assetsToReturns = assetReturns
            )
            4 -> {
                val rateFreeReturns = loadAssetReturns(params[0], compute = false).first().second
                val annualizationRate = params[1].toInt()
                val onlyNegativeReturns = params[2].toBoolean()
                ReturnRiskRatio(
                    assetsToReturns = assetReturns,
                    rateFreeReturns = rateFreeReturns,
                    annualizationRate = PositiveInt(annualizationRate),
                    onlyNegativeReturns = onlyNegativeReturns
                )
            }
            else -> throw IllegalArgumentException("invalid number of parameters for return risk score")
        }
        "mean-var" -> {
            checkArgsSize(params, 1)
            MeanVarianceMetric(
                assetsToReturns = assetReturns,
                lambda = toDouble(params[0])
            )
        }
        // TODO treynor metric
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
            else -> throw IllegalArgumentException("invalid number of parameters for tournament selection")
        }
        "trunc" -> when (params.size) {
            1 -> TruncateSelector(keepPercent = toDouble(params[0]))
            2 -> TruncateSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException("invalid number of parameters for truncation selection")
        }
        "roulette" -> when (params.size) {
            1 -> RouletteWheelSelector(keepPercent = toDouble(params[0]))
            2 -> RouletteWheelSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException("invalid number of parameters for roulette wheel selection")
        }
        "stoch" -> when (params.size) {
            1 -> StochasticUniversalSelector(keepPercent = toDouble(params[0]))
            2 -> StochasticUniversalSelector(
                keepPercent = toDouble(params[0]),
                endKeepPercent = toDouble(params[1]),
                iterations = iterations
            )
            else -> throw IllegalArgumentException("invalid number of parameters for stochastic selection")
        }
        else -> throw IllegalArgumentException("invalid weight mutator $type")
    }
}