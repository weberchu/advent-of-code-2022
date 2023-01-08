import java.util.*
import kotlin.math.abs

typealias Location = Pair<Int, Int>

private operator fun Location.plus(offset: Pair<Int, Int>): Location {
    return Location(this.first + offset.first, this.second + offset.second)
}

private fun Location.distanceFrom(another: Location): Int {
    return abs(this.first - another.first) + abs(this.second - another.second)
}

private enum class MoveDirection(val offset: Pair<Int, Int>) {
    Up(Pair(0, -1)), Down(Pair(0, 1)), Left(Pair(-1, 0)), Right(Pair(1, 0))
}

private data class Valley(
    val width: Int,
    val height: Int,
    val entrance: Location,
    val exit: Location,
    val blizzards: List<Blizzard>
)

private data class Blizzard(
    var location: Location,
    val direction: MoveDirection,
)

private fun parseValley(input: List<String>): Valley {
    val width = input[0].length
    val height = input.size
    var entrance: Location? = null
    var exit: Location? = null
    val blizzards = mutableListOf<Blizzard>()

    for ((y, line) in input.withIndex()) {
        if (y == 0) {
            entrance = Location(line.indexOf('.'), y)
        } else if (y == input.size - 1) {
            exit = Location(line.indexOf('.'), y)
        } else {
            for ((x, c) in line.withIndex()) {
                when (c) {
                   '>' -> blizzards.add(Blizzard(Location(x, y), MoveDirection.Right))
                   '<' -> blizzards.add(Blizzard(Location(x, y), MoveDirection.Left))
                   '^' -> blizzards.add(Blizzard(Location(x, y), MoveDirection.Up))
                   'v' -> blizzards.add(Blizzard(Location(x, y), MoveDirection.Down))
                }
            }
        }
    }

    return Valley(width, height, entrance!!, exit!!, blizzards)
}

private data class PossibleStep(
    val location: Location,
    val stepsTaken: List<Location>,
    val estimateMinTimeToDestination: Int
) : Comparable<PossibleStep> {
    override fun compareTo(other: PossibleStep): Int {
        val totalTimeDiff =
            this.stepsTaken.size + this.estimateMinTimeToDestination - other.stepsTaken.size - other.estimateMinTimeToDestination
        if (totalTimeDiff != 0) {
            return totalTimeDiff
        }

        val timeSpentDiff = this.stepsTaken.size - other.stepsTaken.size
        if (timeSpentDiff != 0) {
            return timeSpentDiff
        }

        val xDiff = this.location.first - other.location.first
        if (xDiff != 0) {
            return xDiff
        }

        return this.location.second - other.location.second
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PossibleStep

        if (location != other.location) return false
        if (stepsTaken.size != other.stepsTaken.size) return false
        if (estimateMinTimeToDestination != other.estimateMinTimeToDestination) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + stepsTaken.size.hashCode()
        result = 31 * result + estimateMinTimeToDestination
        return result
    }
}

private fun moveBlizzards(blizzards: List<Blizzard>, width: Int, height: Int): List<Blizzard> {
    return blizzards.map { blizzard ->
        var newX = blizzard.location.first + blizzard.direction.offset.first
        var newY = blizzard.location.second + blizzard.direction.offset.second

        if (newX == 0) {
            newX = width - 2
        } else if (newX == width - 1) {
            newX = 1
        }

        if (newY == 0) {
            newY = height - 2
        } else if (newY == height - 1) {
            newY = 1
        }

        Blizzard(Pair(newX, newY), blizzard.direction)
    }
}

private fun blizzardsLoop(initialBlizzards: List<Blizzard>, width: Int, height: Int, loop: Int): List<List<Blizzard>> {
    val blizzardsLoop = mutableListOf(initialBlizzards)
    var blizzards = initialBlizzards
    for (i in 2..loop) {
        blizzards = moveBlizzards(blizzards, width, height)
        blizzardsLoop.add(blizzards)
    }
    return blizzardsLoop
}

private fun findSteps(
    valley: Valley,
    start: Location,
    end: Location,
    blizzardsLoop: List<List<Blizzard>>,
    stepsTaken: Int
): Int {
    val possibleSteps = TreeSet<PossibleStep>()
    possibleSteps.add(PossibleStep(start, emptyList(), start.distanceFrom(end)))

    do {
        val possibleStep = possibleSteps.first()
        possibleSteps.remove(possibleStep)

        val nextBlizzardState = blizzardsLoop[(possibleStep.stepsTaken.size + 1 + stepsTaken) % blizzardsLoop.size]
        (MoveDirection.values().map { it.offset } + Pair(0, 0)).forEach { offset ->
            val nextLocation = possibleStep.location + offset
            if (nextLocation == end) {
                println("stepsTaken = ${possibleStep.stepsTaken + nextLocation}")
                return possibleStep.stepsTaken.size + 1
            }

            if ((possibleStep.location == start && nextLocation == start) ||
                (nextLocation.first > 0 && nextLocation.first < valley.width - 1 &&
                    nextLocation.second > 0 && nextLocation.second < valley.height - 1 &&
                    nextBlizzardState.all { it.location != nextLocation })
            ) {
                possibleSteps.add(
                    PossibleStep(
                        nextLocation,
                        possibleStep.stepsTaken + nextLocation,
                        nextLocation.distanceFrom(end)
                    )
                )
            }
        }
    } while (possibleSteps.isNotEmpty())

    throw IllegalStateException("Never found a path to exit")
}

private fun part1(input: List<String>): Int {
    val valley = parseValley(input)
    val blizzardsLoop = blizzardsLoop(valley.blizzards, valley.width, valley.height, (valley.width - 2) * (valley.height - 2))

    return findSteps(valley, valley.entrance, valley.exit, blizzardsLoop, 0)
}

private fun part2(input: List<String>): Int {
    val valley = parseValley(input)
    val blizzardsLoop = blizzardsLoop(valley.blizzards, valley.width, valley.height, (valley.width - 2) * (valley.height - 2))

    val trip1 = findSteps(valley, valley.entrance, valley.exit, blizzardsLoop, 0)
    val trip2 = findSteps(valley, valley.exit, valley.entrance, blizzardsLoop, trip1)
    val trip3 = findSteps(valley, valley.entrance, valley.exit, blizzardsLoop, trip1 + trip2)

    return trip1 + trip2 + trip3
}

fun main() {
    val input = readInput("Day24")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
