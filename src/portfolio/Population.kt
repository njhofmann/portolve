package portfolio

import fitness.FitnessMetric

fun getRandomPopulation(assetUniverse: Int, popSize: Int, portfolioSize: Int): List<Portfolio> {
    return (0..popSize).map { DefaultPortfolio(assetUniverse, portfolioSize) }
}

fun getBest(population: List<Portfolio>, fitnessMetric: FitnessMetric): Portfolio {
    val scores = fitnessMetric.evaluate(population)
    var maxScore = 0.0
    var maxScoreIdx = 0
    scores.forEachIndexed { idx: Int, score: Double ->
        if (score > maxScore) {
            maxScore = score
            maxScoreIdx = idx
        }
    }
    return population[maxScoreIdx]
}