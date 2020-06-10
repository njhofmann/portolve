package portfolio

/**
 * Represents a potential portfolio of assets and their respective weights in that portfolio. Weights in the Portfolio
 * sum to 1, the assets in the Portfolio are guaranteed to be unique.
 */
interface Portfolio {

    /**
     * The number of assets making up the Portfolio
     */
    val size: Int

    /**
     * The Allocations making up this Portfolio, sorted
     * @return: Allocations of this Portfolio
     */
    val allocations: List<Allocation>

    /**
     * Overrides the equality operator to make it dependent on the Allocations making up this Portfolio
     * @return: if given item equals this Portfolio
     */
    override fun equals(other: Any?): Boolean

    /**
     * Overrides the hashcode of this Portfolio to make it dependent its Allocations
     * @return: hashcode
     */
    override fun hashCode(): Int
}