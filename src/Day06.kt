private fun isUniqueMarker(s: String): Boolean {
    for (i in s.indices) {
        if (s.indexOf(s[i], i + 1) != -1) {
            return false
        }
    }

    return true
}

private fun endIndexOfMarker(input: List<String>, markerLength: Int): Int {
    val data = input[0]
    for (i in data.indices) {
        if (isUniqueMarker(data.substring(i, i + markerLength))) {
            return i + markerLength
        }
    }

    throw IllegalArgumentException("There is no start marker")
}

private fun part1(input: List<String>): Int {
    return endIndexOfMarker(input, 4)
}

private fun part2(input: List<String>): Int {
    return endIndexOfMarker(input, 14)
}

fun main() {
    val input = readInput("Day06")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
