private fun part1(input: List<String>): Int {
    return input.count { line ->
        val assignment = line.split(",").map { it.split("-").map { section -> section.toInt() } }
        (assignment[0][0] <= assignment[1][0] && assignment[0][1] >= assignment[1][1])
            || (assignment[1][0] <= assignment[0][0] && assignment[1][1] >= assignment[0][1])
    }
}

private fun part2(input: List<String>): Int {
    return input.count { line ->
        val assignment = line.split(",").map { it.split("-").map { section -> section.toInt() } }
        (assignment[0][0] <= assignment[1][0] && assignment[1][0] <= assignment[0][1])
            || (assignment[0][0] <= assignment[1][1] && assignment[1][1] <= assignment[0][1])
            || (assignment[1][0] <= assignment[0][0] && assignment[0][0] <= assignment[1][1])
            || (assignment[1][0] <= assignment[0][1] && assignment[0][1] <= assignment[1][1])
    }
}

fun main() {
    val input = readInput("Day04")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
