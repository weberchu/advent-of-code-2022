import kotlin.math.abs

private fun ropeOffset(headPosition: Int, tailPosition: Int): Int {
    return if (headPosition > tailPosition) {
        1
    } else if (headPosition < tailPosition) {
        -1
    } else {
        0
    }
}

private fun ropeSimulation(input: List<String>, ropeLength: Int): Int {
    val visitedPositions = mutableSetOf<Pair<Int, Int>>()
    val rope = MutableList(ropeLength) { Pair(0, 0) }

    visitedPositions.add(rope[ropeLength - 1])

    for (line in input) {
        val motion = line.split(" ")
        val direction = motion[0]
        val magnitude = motion[1].toInt()

        for (i in 0 until magnitude) {
            rope[0] = when (direction) {
                "U" -> Pair(rope[0].first, rope[0].second + 1)
                "D" -> Pair(rope[0].first, rope[0].second - 1)
                "L" -> Pair(rope[0].first - 1, rope[0].second)
                "R" -> Pair(rope[0].first + 1, rope[0].second)
                else -> throw IllegalArgumentException("Unknown motion $motion")
            }

            for (j in 1 until ropeLength) {
                if (abs(rope[j - 1].first - rope[j].first) > 1 || abs(rope[j - 1].second - rope[j].second) > 1) {
                    rope[j] = Pair(
                        rope[j].first + ropeOffset(rope[j - 1].first, rope[j].first),
                        rope[j].second + ropeOffset(rope[j - 1].second, rope[j].second)
                    )

                    if (j == ropeLength - 1) {
                        visitedPositions.add(rope[j])
                    }
                } else {
                    break
                }
            }
        }
    }

    return visitedPositions.size
}

fun main() {
    val input = readInput("Day09")
//    val input = readInput("Test")

    println("Part 1: " + ropeSimulation(input, 2))
    println("Part 2: " + ropeSimulation(input, 10))
}
