import kotlin.math.min

private enum class Material {
    Geode, Obsidian, Clay, Ore
}

private data class Blueprint(
    val id: Int,
    val robots: Map<Material, Map<Material, Int>>,
    val maxRobotRequired: Map<Material, Int> = Material.values().associateWith { material ->
        robots.maxOf { if (material == Material.Geode) Int.MAX_VALUE else if (it.value.contains(material)) it.value[material]!! else 0 }
    }
)

private data class Inventory(
    val materials: Map<Material, Int>,
    val robots: Map<Material, Int>
)

private fun blueprints(input: List<String>): List<Blueprint> {
    val blueprintRegex = """Blueprint (\d+).*""".toRegex()
    val oreRobotRegex = """.*Each ore robot costs (\d+) ore\..*""".toRegex()
    val clayRobotRegex = """.*Each clay robot costs (\d+) ore\..*""".toRegex()
    val obsidianRobotRegex = """.*Each obsidian robot costs (\d+) ore and (\d+) clay\..*""".toRegex()
    val geodeRobotRegex = """.*Each geode robot costs (\d+) ore and (\d+) obsidian\..*""".toRegex()
    return input.map { line ->
        val id = blueprintRegex.matchEntire(line)!!.groupValues[1].toInt()
        val oreRobot = oreRobotRegex.matchEntire(line)!!
        val clayRobot = clayRobotRegex.matchEntire(line)!!
        val obsidianRobot = obsidianRobotRegex.matchEntire(line)!!
        val geodeRobot = geodeRobotRegex.matchEntire(line)!!
        Blueprint(
            id, mapOf(
                Material.Ore to mapOf(Material.Ore to oreRobot.groupValues[1].toInt()),
                Material.Clay to mapOf(Material.Ore to clayRobot.groupValues[1].toInt()),
                Material.Obsidian to mapOf(
                    Material.Ore to obsidianRobot.groupValues[1].toInt(),
                    Material.Clay to obsidianRobot.groupValues[2].toInt()
                ),
                Material.Geode to mapOf(
                    Material.Ore to geodeRobot.groupValues[1].toInt(),
                    Material.Obsidian to geodeRobot.groupValues[2].toInt()
                )
            )
        )
    }
}

private fun canBuild(robot: Map<Material, Int>, inventory: Inventory): Boolean {
    return robot.all { robotMaterial ->
        inventory.materials[robotMaterial.key]!! >= robotMaterial.value
    }
}

private fun build(robot: Material, cost: Map<Material, Int>, inventory: Inventory): Inventory {
    val materials = inventory.materials.toMutableMap()
    val robots = inventory.robots.toMutableMap()
    cost.forEach { (material, count) ->
        if (materials[material]!! - count < 0) {
            throw IllegalStateException("Not enough material to build")
        }
        materials[material] = materials[material]!! - count
    }
    robots[robot] = robots[robot]!! + 1
    return Inventory(materials, robots)
}

private fun timeTick(inventory: Inventory): Inventory {
    val materials = inventory.materials.toMutableMap()
    inventory.robots.forEach { (robot, productivity) ->
        materials[robot] = materials[robot]!! + productivity
    }
    return Inventory(materials, inventory.robots)
}

private data class Decision(
    val built: String,
    val notToBuilt: Set<Material>,
    val inventory: Inventory
)

private fun simulateMaxGeode(
    blueprint: Blueprint,
    inventory: Inventory,
    time: Int,
    decisions: List<Decision>
): Pair<Int, List<Decision>> {
    if (time == 0) {
        return Pair(inventory.materials[Material.Geode]!!, decisions)
    }

    var maxGeode = Int.MIN_VALUE
    var maxDecision = emptyList<Decision>()

    val buildableRobots = mutableSetOf<Material>()

    for (material in Material.values()) {
        val robot = blueprint.robots[material]!!

        if (canBuild(robot, inventory)) {
            if (inventory.robots[material]!! < blueprint.maxRobotRequired[material]!! && !decisions.last().notToBuilt.contains(material)) {
                // 1. don't build more robot than max material needed in one round
                // 2. don't build something intentionally skipped last time
                // simulate build a robot
                var newInventory = timeTick(inventory)
                newInventory = build(material, robot, newInventory)
                val newDecisions = decisions.toMutableList()
                newDecisions.add(Decision(material.name, emptySet(), newInventory))
                val simulationResult = simulateMaxGeode(blueprint, newInventory, time - 1, newDecisions)
                if (simulationResult.first > maxGeode) {
                    maxGeode = simulationResult.first
                    maxDecision = simulationResult.second
                }
            }

            buildableRobots.add(material)

            if (material == Material.Geode) {
                break
            }
        }
    }

    val newInventory = timeTick(inventory)
    val newDecisions = decisions.toMutableList()
    newDecisions.add(Decision("Noop", buildableRobots, newInventory))
    val simulationResult = simulateMaxGeode(blueprint, newInventory, time - 1, newDecisions)
    if (simulationResult.first > maxGeode) {
        maxGeode = simulationResult.first
        maxDecision = simulationResult.second
    }

    return Pair(maxGeode, maxDecision)
}

private fun part1(input: List<String>): Int {
    val blueprints = blueprints(input)

    val inventory = Inventory(
        mapOf(
            Material.Ore to 0,
            Material.Clay to 0,
            Material.Obsidian to 0,
            Material.Geode to 0
        ),
        mapOf(
            Material.Ore to 1,
            Material.Clay to 0,
            Material.Obsidian to 0,
            Material.Geode to 0
        )
    )

    return blueprints.sumOf { blueprint ->
        val startTime = System.currentTimeMillis()
        val simulation = simulateMaxGeode(blueprint, inventory, 24, listOf())
        println("part 1 >>>>>>")
        println("blueprint = ${blueprint}")
        println("max Geode = ${simulation.first}")
        println("simulation time = ${System.currentTimeMillis() - startTime}")
        simulation.first * blueprint.id
    }
}

private fun part2(input: List<String>): Int {
    val blueprints = blueprints(input).subList(0, min(3, input.size))

    val inventory = Inventory(
        mapOf(
            Material.Ore to 0,
            Material.Clay to 0,
            Material.Obsidian to 0,
            Material.Geode to 0
        ),
        mapOf(
            Material.Ore to 1,
            Material.Clay to 0,
            Material.Obsidian to 0,
            Material.Geode to 0
        )
    )

    return blueprints.map { blueprint ->
        val startTime = System.currentTimeMillis()
        val simulation = simulateMaxGeode(blueprint, inventory, 32, listOf())
        println("part 2 >>>>>>")
        println("blueprint = ${blueprint}")
        println("max Geode = ${simulation.first}")
        println("simulation time = ${System.currentTimeMillis() - startTime}")
        simulation.first
    }.reduce { acc, numOfGeode -> acc * numOfGeode }
}

fun main() {
    val input = readInput("Day19")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
