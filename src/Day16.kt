private val lineFormat = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.*)""".toRegex()

private data class Valve(
    val name: String,
    val flowRate: Int,
    val neighbours: List<String>
)

private const val TOTAL_TIME = 30

private fun valves(input: List<String>): List<Valve> {
    return input.map { line ->
        val matchResult = lineFormat.matchEntire(line)!!
        val name = matchResult.groupValues[1]
        val flowRate = matchResult.groupValues[2].toInt()
        val neighbours = matchResult.groupValues[3].split(", ")
        Valve(name, flowRate, neighbours)
    }.toList()
}

private fun shortestDistance(map: Map<String, Valve>, from: Valve, to: Valve): Int {
    val distanceTable = mutableMapOf(from.name to 0)
    val toProcess = ArrayDeque(listOf(from))

    do {
        val valve = toProcess.removeFirst()
        val valveDistance = distanceTable[valve.name]!!

        for (neighbour in valve.neighbours) {
            if (!distanceTable.containsKey(neighbour)) {
                if (neighbour == to.name) {
                    return valveDistance + 1
                } else {
                    distanceTable[neighbour] = valveDistance + 1
                    toProcess.addLast(map[neighbour]!!)
                }
            }
        }

    } while (toProcess.isNotEmpty())

    throw IllegalArgumentException("Map does not lead from ${from.name} to ${to.name}")
}

private fun distanceMap(
    map: Map<String, Valve>,
    valves: List<Valve>
): Map<Pair<String, String>, Int> {
    val distanceMap = mutableMapOf<Pair<String, String>, Int>()

    for (i in 0 until valves.size - 1) {
        for (j in i + 1 until valves.size) {
            val distance = shortestDistance(map, valves[i], valves[j])
            distanceMap[Pair(valves[i].name, valves[j].name)] = distance
            distanceMap[Pair(valves[j].name, valves[i].name)] = distance
        }
    }

    return distanceMap
}

private fun maxScore(
    currentLocation: String,
    valvesToVisit: List<Valve>,
    distanceMap: Map<Pair<String, String>, Int>,
    timeLeft: Int,
    currentScore: Int,
    currentFlowRate: Int
): Pair<Int, List<String>> {
    var maxScore = currentScore
    var pathVisited = listOf<String>()

    for (valve in valvesToVisit) {
        val distance = distanceMap[Pair(currentLocation, valve.name)]!!
        val timeElapsed = distance + 1
        val newTimeLeft = timeLeft - timeElapsed

        if (newTimeLeft > 0) {
            val newScore = currentScore + currentFlowRate * timeElapsed
            val newFlowRate = currentFlowRate + valve.flowRate
            val remainingValves = valvesToVisit - valve

            val (remainingMaxScore, remainingPathVisited) = if (remainingValves.isNotEmpty()) {
                maxScore(valve.name, remainingValves, distanceMap, newTimeLeft, newScore, newFlowRate)
            } else {
                // nothing left to visit
                Pair(newScore + newFlowRate * newTimeLeft, listOf())
            }

            if (remainingMaxScore > maxScore) {
                maxScore = remainingMaxScore
                pathVisited = listOf(valve.name) + remainingPathVisited
            }
        } else {
            // not enough time to move to this valve
        }
    }

    if (pathVisited.isEmpty()) {
        // no more valve to visit
        maxScore += timeLeft * currentFlowRate
    }

    return Pair(maxScore, pathVisited)
}

private fun part1(input: List<String>): Int {
    val valves = valves(input)
    val map = valves.associateBy { it.name }
    val usefulValves = valves.filter { it.flowRate > 0 }
    val distanceMap = distanceMap(map, (usefulValves + map["AA"]!!).toSet().toList())

    val (maxScore, pathVisited) = maxScore("AA", usefulValves, distanceMap, TOTAL_TIME, 0, 0)

    println("maxScore = ${maxScore}")
    println("pathVisited = ${pathVisited}")

    return maxScore
}

private fun generateCombinations(valves: List<Valve>, count: Int): List<List<Valve>> {
    return if (count == 1) {
        valves.map { listOf(it) }
    } else {
        val result = mutableListOf<List<Valve>>()
        for (i in 0 until valves.size - 1) {
            generateCombinations(valves.subList(i + 1, valves.size), count - 1).forEach { combination ->
                result.add(listOf(valves[i]) + combination)
            }
        }
        result
    }
}

private fun part2(input: List<String>): Int {
    val valves = valves(input)
    val map = valves.associateBy { it.name }
    val usefulValves = valves.filter { it.flowRate > 0 }
    val distanceMap = distanceMap(map, (usefulValves + map["AA"]!!).toSet().toList())

    val myCombinations = mutableListOf<List<Valve>>()
    val halfValvesCount = usefulValves.size / 2
    for (x in 1..halfValvesCount) {
        // add all combinations with x valves
        myCombinations.addAll(generateCombinations(usefulValves, x))
    }

    var maxScore = Int.MIN_VALUE
    var myMaxPath = listOf<String>()
    var elephantMaxPath = listOf<String>()

    for (myCombination in myCombinations) {
        val elephantCombination = usefulValves - myCombination
        val (myMaxScore, myPathVisited) = maxScore("AA", myCombination, distanceMap, TOTAL_TIME - 4, 0, 0)
        val (elephantMaxScore, elephantPathVisited) = maxScore("AA", elephantCombination, distanceMap, TOTAL_TIME - 4, 0, 0)
        val totalScore = myMaxScore + elephantMaxScore
        if (totalScore > maxScore) {
            maxScore = totalScore
            myMaxPath = myPathVisited
            elephantMaxPath = elephantPathVisited
        }
    }

    println("maxScore = ${maxScore}")
    println("myMaxPath = ${myMaxPath}")
    println("elephantMaxPath = ${elephantMaxPath}")

    return maxScore
}

fun main() {
    val input = readInput("Day16")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
