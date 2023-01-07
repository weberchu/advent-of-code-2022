private val northOffset = listOf(
    Pair(1, -1),
    Pair(0, -1),
    Pair(-1, -1),
)

private val eastOffset = listOf(
    Pair(1, -1),
    Pair(1, 0),
    Pair(1, 1),
)

private val southOffset = listOf(
    Pair(1, 1),
    Pair(0, 1),
    Pair(-1, 1),
)

private val westOffset = listOf(
    Pair(-1, -1),
    Pair(-1, 0),
    Pair(-1, 1),
)

private fun elves(input: List<String>): Set<Pair<Int, Int>> {
    val elves = mutableSetOf<Pair<Int, Int>>()
    for ((y, line) in input.withIndex()) {
        for ((x, char) in line.withIndex()) {
            if (char == '#') {
                elves.add((Pair(x, y)))
            }
        }
    }
    return elves
}

private fun eightNeighbour(coordinate: Pair<Int, Int>): List<Pair<Int, Int>> {
    return listOf(
        Pair(coordinate.first + 1, coordinate.second + 1),
        Pair(coordinate.first + 1, coordinate.second),
        Pair(coordinate.first + 1, coordinate.second - 1),
        Pair(coordinate.first, coordinate.second + 1),
        Pair(coordinate.first, coordinate.second - 1),
        Pair(coordinate.first - 1, coordinate.second + 1),
        Pair(coordinate.first - 1, coordinate.second),
        Pair(coordinate.first - 1, coordinate.second - 1),
    )
}

private fun directionNeighbour(coordinate: Pair<Int, Int>, offset: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    return offset.map {
        Pair(coordinate.first + it.first, coordinate.second + it.second)
    }
}

val considerationOrders = listOf(
    listOf(northOffset, southOffset, westOffset, eastOffset),
    listOf(southOffset, westOffset, eastOffset, northOffset),
    listOf(westOffset, eastOffset, northOffset, southOffset),
    listOf(eastOffset, northOffset, southOffset, westOffset),
)

private fun move(round: Int, elves: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
    val elvesDestinations = elves.toMutableSet()
    val considerationOrder = considerationOrders[round % 4]

    val destinationCount = mutableMapOf<Pair<Int, Int>, Int>()
    val proposedMoves = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    // first half
    for (elf in elves) {
        if (eightNeighbour(elf).any { elves.contains(it) }) {
            considerationOrder.firstOrNull() { consideration ->
                directionNeighbour(elf, consideration).all { !elves.contains(it) }
            }?.let { offset ->
                val moveDirection = offset[1]
                val proposedDestination = Pair(elf.first + moveDirection.first, elf.second + moveDirection.second)
                destinationCount[proposedDestination] = destinationCount.getOrDefault(proposedDestination, 0) + 1
                proposedMoves[elf] = proposedDestination
            }
        }
        // otherwise no elves around, don't move
    }

    for ((elf, destination) in proposedMoves) {
        if (destinationCount[destination] == 1) {
            elvesDestinations.remove(elf)
            elvesDestinations.add(destination)
        }
    }

    return elvesDestinations
}

private fun part1(input: List<String>): Int {
    var elves = elves(input)

    for (round in 0 until 10) {
        elves = move(round, elves)
    }

    val allX = elves.map { it.first }
    val allY = elves.map { it.second }

    return (allX.max() - allX.min() + 1) * (allY.max() - allY.min() + 1) - elves.size
}

private fun part2(input: List<String>): Int {
    var elves = elves(input)

    var round = 0
    do {
        val elvesNext = move(round, elves)
        if (elvesNext == elves) {
            return round + 1
        }

        elves = elvesNext
        round++
    } while (true)
}

fun main() {
    val input = readInput("Day23")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
