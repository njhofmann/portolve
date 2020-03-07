/**
 * Represents a potential portfolio of assets and their respective weights in that portfolio.
 */
interface Portfolio {
    /**
     * Returns the raw representation of this portfolio as array of N doubles, where N is the number
     * of assets being considered to make up the portfolio (size of the universe of available assets).
     * ith item represents the weight of the associated asset. All weights are greater than zero,
     * weight of zero means portfolio does not include the associated asset. All weights should sum
     * to around 1 (within some tolerance threshold).
     * @return array of doubles representing the weights currently assigned to each associated asset
     */
    val rawRepresentation: DoubleArray

    /**
     * Returns a mapping of assets to current portfolio weights, for all non-zero assets.
     * @return mapping of assets to current portfolio weights
     */
    fun assetsToWeights(): Map<String, Double>
}