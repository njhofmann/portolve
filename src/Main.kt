
fun printSolution(solution: List<Pair<String, Double>>) {
    solution.forEach {
        print("Asset: %s, Weight: %d\n".format(it.first, it.second))
    }
}

fun main() {


    //val solution: List<Pair<String, Double>> = Evolver().findSolution()
    //printSolution(solution)
}