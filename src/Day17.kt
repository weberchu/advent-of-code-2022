import kotlin.math.max

private data class RockShape(
    val rocks: List<Pair<Int, Int>>,
    val width: Int,
    val height: Int
)

private val fallingRockShapes = listOf(
    RockShape(listOf(Pair(2, 0), Pair(3, 0), Pair(4, 0), Pair(5, 0)), 4, 1),
    RockShape(listOf(Pair(2, 1), Pair(3, 0), Pair(3, 1), Pair(3, 2), Pair(4, 1)), 3, 3),
    RockShape(listOf(Pair(2, 0), Pair(3, 0), Pair(4, 0), Pair(4, 1), Pair(4, 2)), 3, 3),
    RockShape(listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(2, 3)), 1, 4),
    RockShape(listOf(Pair(2, 0), Pair(2, 1), Pair(3, 0), Pair(3, 1)), 2, 2)
)

private enum class Direction {
    LEFT, RIGHT, DOWN
}

private class Jet(
    private val pattern: String
) {
    var nextIndex = 0

    fun nextDirection(): Direction {
        val direction = if (pattern[nextIndex] == '>') Direction.RIGHT else Direction.LEFT
        nextIndex = (nextIndex + 1) % pattern.length
        return direction
    }
}

private fun isRock(chamber: List<List<Char>>, x:Int, y:Int): Boolean {
    if (y < 0) {
        return true
    }
    if (x < 0 || x >= 7) {
        return true
    }
    if (y >= chamber.size) {
        return false
    }
    return chamber[y][x] == '#'
}

private fun canMove(chamber: List<List<Char>>, rocks: List<Pair<Int, Int>>, direction: Direction): Boolean {
    return when (direction) {
        Direction.LEFT -> !rocks.any { isRock(chamber, it.first - 1, it.second) }
        Direction.RIGHT -> !rocks.any { isRock(chamber, it.first + 1, it.second) }
        Direction.DOWN -> !rocks.any { isRock(chamber, it.first, it.second - 1) }
    }
}

private fun move(rocks: List<Pair<Int, Int>>, direction: Direction): List<Pair<Int, Int>> {
    return when (direction) {
        Direction.LEFT -> rocks.map { Pair(it.first - 1, it.second) }
        Direction.RIGHT -> rocks.map { Pair(it.first + 1, it.second) }
        Direction.DOWN -> rocks.map { Pair(it.first, it.second - 1) }
    }
}

private fun printChamber(chamber: List<List<Char>>, printSize: Int = 20) {
    for (i in chamber.size - 1 downTo max(0, chamber.size - printSize)) {
        println(String(chamber[i].toCharArray()) + " $i")
    }
}

private class Simulator(
    private val jet: Jet
) {
    val chamber = mutableListOf<MutableList<Char>>()
    val heightCache = mutableListOf<Int>()
    var nextRock = 0

    fun simulate(numberOfRocks: Int) {
        for (i in 0 until numberOfRocks) {
            val fallingRockShape = fallingRockShapes[nextRock]
            nextRock = (nextRock + 1) % 5

            // rock always appears 3 unit above highest rock, so move down and jet it 3 times first
            var rocks = fallingRockShape.rocks.map { Pair(it.first, it.second + chamber.size) }
            for (j in 1..3) {
                val jetDirection = jet.nextDirection()
                if (canMove(chamber, rocks, jetDirection)) {
                    rocks = move(rocks, jetDirection)
                }
            }

            var canMoveDown: Boolean
            do {
                val jetDirection = jet.nextDirection()
                if (canMove(chamber, rocks, jetDirection)) {
                    rocks = move(rocks, jetDirection)
                }

                canMoveDown = canMove(chamber, rocks, Direction.DOWN)
                if (canMoveDown) {
                    rocks = move(rocks, Direction.DOWN)
                } else {
                    val rockMaxY = rocks.maxOf { it.second }
                    if (rockMaxY > chamber.size - 1) {
                        for (y in chamber.size - 1 until rockMaxY) {
                            chamber.add(MutableList(7) { '.' })
                        }
                    }
                    rocks.forEach { rock ->
                        chamber[rock.second][rock.first] = '#'
                    }
                }

            } while (canMoveDown)

            heightCache.add(chamber.size)
        }
    }

    fun nextJetIndex(): Int {
        return jet.nextIndex
    }
}

private fun part1(input: List<String>): Int {
    val jet = Jet(input[0])

    val simulator = Simulator(jet)
    simulator.simulate(2022)

    printChamber(simulator.chamber)

    return simulator.chamber.size
}

private fun part2(input: List<String>): Long {
    val simulator = Simulator(Jet(input[0]))
    val cachedPattern = mutableListOf<Pair<List<String>, Int>>() // cache of chamber chunk and jet index
    var lastChamberSize = 0
    val numberOfRocksPerRound = input[0].length
    val roundsToFindPattern = 5000
    val simulationRounds = 1000000000000L


    for (i in 0 until roundsToFindPattern) {
        val beginJetIndex = simulator.nextJetIndex()
        simulator.simulate(numberOfRocksPerRound)

        println("i = ${i} chamber size = ${simulator.chamber.size}, startJetI=$beginJetIndex, endJetI=${simulator.nextJetIndex()}")
        cachedPattern.add(Pair(simulator.chamber.subList(lastChamberSize, simulator.chamber.size).map { String(it.toCharArray()) }, beginJetIndex))

        if (i > 0) {
            for (j in 0 until i - 1) {
                if (cachedPattern[j] == cachedPattern[i]) {
                    // matched pattern found repeating from j to i-1
                    println("pattern matched = $j $i")

                    val beginningNonRepeatingHeight = (0 until j).sumOf { k ->
                        cachedPattern[k].first.size.toLong()
                    }
                    val beginningNonRepeatingRocksCount = j * numberOfRocksPerRound.toLong()

                    val repeatingPatternRocksCount = (i - j) * numberOfRocksPerRound.toLong()
                    val repeatingPatternHeight = (j until i).sumOf { k ->
                        cachedPattern[k].first.size.toLong()
                    }

                    val repeatingCycles = (simulationRounds - beginningNonRepeatingRocksCount) / repeatingPatternRocksCount
                    val remainingRocks = (simulationRounds - beginningNonRepeatingRocksCount) % repeatingPatternRocksCount

                    // simulate again right before the repeating pattern. Ideally we can remember the height after each rock to reuse it.
                    println("re-simulate from = ${simulator.chamber.size - cachedPattern[i].first.size}, jetI=${cachedPattern[j].second}")

                    println("numberOfRocksPerRound = ${numberOfRocksPerRound}")
                    println("beginningNonRepeatingHeight = ${beginningNonRepeatingHeight}")
                    println("beginningNonRepeatingRocksCount = ${beginningNonRepeatingRocksCount}")
                    println("repeatingPatternRocksCount = ${repeatingPatternRocksCount}")
                    println("repeatingPatternHeight = ${repeatingPatternHeight}")
                    println("repeatingCycles = ${repeatingCycles}")
                    println("remainingRocks = ${remainingRocks}")

                    println("getting height cache from ${beginningNonRepeatingRocksCount.toInt() - 1} to ${beginningNonRepeatingRocksCount.toInt() + remainingRocks.toInt() - 1}")

                    val incompleteCycleHeight =
                        simulator.heightCache[beginningNonRepeatingRocksCount.toInt() + remainingRocks.toInt() - 1] - simulator.heightCache[beginningNonRepeatingRocksCount.toInt() - 1]

                    println("incompleteCycleHeight = ${incompleteCycleHeight}")

                    return beginningNonRepeatingHeight + repeatingPatternHeight * repeatingCycles + incompleteCycleHeight
                }
            }
        }
        lastChamberSize = simulator.chamber.size
    }

    throw IllegalStateException("No repeating pattern found within $roundsToFindPattern rounds")
}

fun main() {
    val input = readInput("Day17")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
