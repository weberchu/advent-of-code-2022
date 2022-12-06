private fun numberOfStack(input: List<String>): Int {
    return input.find { it.startsWith(" 1 ") }?.let {
        1 + it.length / 4
    }!!
}

private fun startingStacks(input: List<String>): Pair<List<MutableList<Char>>, Int> {
    val numberOfStack = numberOfStack(input)
    val stacks = List(numberOfStack) {
        mutableListOf<Char>()
    }

    var i = 0
    while (!input[i].startsWith(" 1 ")) {
        for (s in 0 until numberOfStack) {
            val crateIndex = 1 + s * 4
            if (crateIndex >= input[i].length) {
                break
            }

            val crate = input[i][crateIndex]
            if (crate != ' ') {
                stacks[s].add(0, crate)
            }
        }
        i++
    }

    return Pair(stacks, i)
}

private fun part1(input: List<String>): String {
    var (stacks, i) = startingStacks(input)

    i += 2

    while (i < input.size) {
        val line = input[i]
        val moveCount = line.substring(5, line.indexOf(" from ")).toInt()
        val from = line.substring(line.indexOf(" from ") + 6, line.indexOf(" to ")).toInt()
        val to = line.substring(line.indexOf(" to ") + 4).toInt()

        for (c in 0 until moveCount) {
            val crate = stacks[from - 1].removeLast()
            stacks[to - 1].add(crate)
        }

        i++
    }

    return stacks.map { it.last() }.toCharArray().concatToString()
}

private fun part2(input: List<String>): String {
    var (stacks, i) = startingStacks(input)

    i += 2

    while (i < input.size) {
        val line = input[i]
        val moveCount = line.substring(5, line.indexOf(" from ")).toInt()
        val from = line.substring(line.indexOf(" from ") + 6, line.indexOf(" to ")).toInt()
        val to = line.substring(line.indexOf(" to ") + 4).toInt()

        for (c in moveCount downTo 1) {
            val crate = stacks[from - 1].removeAt(stacks[from - 1].size - c)
            stacks[to - 1].add(crate)
        }

        i++
    }

    return stacks.map { it.last() }.toCharArray().concatToString()
}

fun main() {
    val input = readInput("Day05")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
