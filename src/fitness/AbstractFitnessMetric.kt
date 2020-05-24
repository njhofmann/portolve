package fitness

import portfolio.Portfolio

abstract class AbstractFitnessMetric : FitnessMetric {

    abstract fun metric(portfolio: Portfolio): Double

    override fun evaluate(population: List<Portfolio>): List<Double> {
        return population.map { metric(it) }
    }
}