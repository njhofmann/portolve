/**
 * Represents a potential portfolio of assets and their respective weights in that portfolio.
 */
interface Portfolio {
    /**
     * Returns a raw representation of this portfolio as array of N doubles, where N is the
     * number of assets being considered to make up the portfolio (size of the universe of assests).
     * ith item represents the weight of the associated asset. All weights are greater than zero,
     * weight of zero means portfolio does not include the associated asset. All weights should sum
     * to around 1 (within some tolerance threshold).
     */
    val assetWeights: List<Double>

    /**
     * List of assets being considered for this portfolio.
     */
    val assets: List<String>

    /**
     * Number of assets being considered for this Portfolio.
     */
    val numOfAssets: Int

    /**
     * Returns the number of assets that are actually currently being considered for this portfolio,
     * i.e. those that have a non-zero weight.
     */
    val numOfWeightedAssets: Int

    /**
     * Returns a mapping of assets to current portfolio weights, for all non-zero assets.
     * @return mapping of assets to current portfolio weights
     */
    fun assetsToWeights(): Map<String, Double>
}