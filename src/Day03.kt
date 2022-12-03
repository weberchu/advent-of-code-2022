private fun priority(item: Char): Int {
    return if (item in 'a'..'z') {
        item - 'a' + 1
    } else if (item in 'A' .. 'Z') {
        item - 'A' + 27
    } else {
        throw IllegalArgumentException("Unknown priority for item $item")
    }
}

private fun part1(input: List<String>): Int {
    var totalPriority = 0

    for (line in input) {
        val duplicatedItems = mutableSetOf<Char>()

        val compartment1 = line.substring(0, line.length / 2)
        val compartment2 = line.substring(line.length / 2)

        for (item in compartment1) {
            if (compartment2.contains(item) && !duplicatedItems.contains(item)) {
                totalPriority += priority(item)
                duplicatedItems.add(item)
            }
        }
    }

    return totalPriority
}

private fun part2(input: List<String>): Int {
    var totalPriority = 0

    for (i in input.indices step 3) {
        val rucksack1 = input[i]
        val rucksack2 = input[i + 1]
        val rucksack3 = input[i + 2]

        var commonItemFound = false
        for (item in rucksack1) {
            if (rucksack2.contains(item) && rucksack3.contains(item)) {
                totalPriority += priority(item)
                commonItemFound = true
                break
            }
        }

        if (!commonItemFound) {
            throw IllegalArgumentException("Common item not found")
        }
    }

    return totalPriority
}

fun main() {
    val input = readInput("Day03")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
