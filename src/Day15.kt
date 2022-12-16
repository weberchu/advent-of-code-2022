import kotlin.math.abs
import kotlin.math.max

private val lineFormat =
    """Sensor at x=([\d\-]+), y=([\d\-]+): closest beacon is at x=([\d\-]+), y=([\d\-]+)""".toRegex()

private fun distance(point1: Pair<Int, Int>, point2: Pair<Int, Int>): Int {
    return abs(point1.first - point2.first) + abs(point1.second - point2.second)
}

private fun sensors(input: List<String>): Map<Pair<Int, Int>, Pair<Int, Int>> {
    val sensors = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

    for (line in input) {
        val matchResult = lineFormat.matchEntire(line)
        val coordinates = matchResult!!.groupValues.subList(1, matchResult.groupValues.size).map { it.toInt() }
        val sensor = Pair(coordinates[0], coordinates[1])
        val beacon = Pair(coordinates[2], coordinates[3])
        sensors[sensor] = beacon
    }

    return sensors
}

private fun combinedRanges(
    sensorToBeacons: Map<Pair<Int, Int>, Pair<Int, Int>>,
    targetRow: Int
): MutableList<IntRange> {
    val impossibleRanges = mutableListOf<IntRange>()

    for ((sensor, beacon) in sensorToBeacons) {
        val distance = distance(sensor, beacon)
        val targetRowFromSensor = abs(targetRow - sensor.second)
        if (targetRowFromSensor <= distance) {
            val halfRange = distance - targetRowFromSensor
            val impossibleRange = sensor.first - halfRange..sensor.first + halfRange
            impossibleRanges.add(impossibleRange)
        } else {
            // not in range
        }
    }

    impossibleRanges.sortBy { it.first }

    val combinedRanges = mutableListOf<IntRange>()
    var currentRange: IntRange? = null
    for (range in impossibleRanges) {
        if (currentRange == null) {
            currentRange = range
        } else {
            if (range.first <= currentRange.last) {
                currentRange = currentRange.first..max(currentRange.last, range.last)
            } else {
                combinedRanges.add(currentRange)
                currentRange = range
            }
        }
    }
    combinedRanges.add(currentRange!!)

    return combinedRanges
}

private fun part1(sensorToBeacons: Map<Pair<Int, Int>, Pair<Int, Int>>, targetRow: Int): Int {
    val combinedRanges = combinedRanges(sensorToBeacons, targetRow)

    return combinedRanges.sumOf { it.last - it.first + 1 } -
        sensorToBeacons.values.toSet().filter { it.second == targetRow }.size
}

private fun part2(sensorToBeacons: Map<Pair<Int, Int>, Pair<Int, Int>>, maxCoordinate: Int): Long {
    var uncoveredX = -1
    var uncoveredY = -1
    for (y in 0..maxCoordinate) {
        val combinedRanges = combinedRanges(sensorToBeacons, y)
        var isFullyCovered = false
        for (combinedRange in combinedRanges) {
            if (combinedRange.first <= 0 && combinedRange.last >= maxCoordinate) {
                isFullyCovered = true
                continue
            }
        }

        if (!isFullyCovered) {
            uncoveredY = y

            if (combinedRanges[0].first == 1) {
                uncoveredX = 0
            } else {
                for ((i, combinedRange) in combinedRanges.withIndex()) {
                    if (combinedRange.first <= 0 && combinedRange.last >= 0 && combinedRange.last < maxCoordinate) {
                        uncoveredX = combinedRange.last + 1
                        assert(i + 1 == combinedRanges.size || combinedRanges[i + 1].first == uncoveredX + 1)
                        break
                    }
                }
            }

            break
        }
    }

    return uncoveredX * 4000000L + uncoveredY
}

fun main() {
    val input = readInput("Day15")
    val targetRow = 2000000
    val maxCoordinate = 4000000
//    val input = readInput("Test")
//    val targetRow = 10
//    val maxCoordinate = 20

    val sensors = sensors(input)

    println("Part 1: " + part1(sensors, targetRow))
    println("Part 2: " + part2(sensors, maxCoordinate))
}
