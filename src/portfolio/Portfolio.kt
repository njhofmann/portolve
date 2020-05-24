package portfolio

/**
 * Represents a potential portfolio of assets and their respective weights in that portfolio.
 */
interface Portfolio {

    /**
     * The number of assets that are actually currently being considered for this portfolio,
     * i.e. those that have a non-zero weight.
     */
    val size: Int

    /**
     * Returns the Allocations making up this portfolio.Portfolio, sorted
     * @return Allocations of this portfolio.Portfolio
     */
    val allocations: List<Allocation>

    /**
     * Overrides equals to make equals dependent on the Allocations making up this portfolio.Portfolio
     */
    override fun equals(other: Any?): Boolean

    /**
     * Overrides the hashcode of this portfolio.Portfolio dependent its Allocations
     */
    override fun hashCode(): Int
}