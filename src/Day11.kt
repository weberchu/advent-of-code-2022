private data class Monkey (
    val id: Int,
    val items: MutableList<Long> = mutableListOf(),
    val operation: (Long) -> Long,
    val testDivisor: Int,
    val testTrueMonkey: Int,
    val testFalseMonkey: Int,
    var inspectedItems:Int = 0
)

private fun monkeys(input: List<String>): List<Monkey> {
    var currentLine = 0

    val monkeys = mutableListOf<Monkey>()
    while (currentLine < input.size) {
        var line = input[currentLine]

        assert(line.startsWith("Monkey "))
        val id = line.substring(7, line.length - 1).toInt()
        assert(id == monkeys.size)
        line = input[++currentLine]

        assert(line.startsWith("  Starting items: "))
        val items = line.substring(18).split(", ").map { it.toLong() }.toMutableList()
        line = input[++currentLine]

        assert(line.startsWith("  Operation: new = old "))
        val operand = line[23]
        val num = line.substring(25)
        val operation = if (operand == '+') {
            if (num == "old") {
                { old: Long -> old + old }
            } else {
                { old: Long -> old + num.toLong() }
            }
        } else if (operand == '*') {
            if (num == "old") {
                { old: Long -> old * old }
            } else {
                { old: Long -> old * num.toLong() }
            }
        } else {
            throw IllegalArgumentException("Unknown operand $operand")
        }
        line = input[++currentLine]

        assert(line.startsWith("  Test: divisible by "))
        val testDivisor = line.substring(21).toInt()
        line = input[++currentLine]

        assert(line.startsWith("    If true: throw to monkey "))
        val testTrueMonkey = line.substring(29).toInt()
        assert(testTrueMonkey != id)
        line = input[++currentLine]

        assert(line.startsWith("    If false: throw to monkey "))
        val testFalseMonkey = line.substring(30).toInt()
        assert(testFalseMonkey != id)
        currentLine += 2

        monkeys.add(Monkey(id, items, operation, testDivisor, testTrueMonkey, testFalseMonkey))
    }

    return monkeys
}

private fun simulateRounds(monkeys: List<Monkey>, numOfRounds: Int, worryLevelDivisor: Int) {
    val longDivisorLCM = monkeys
        .map { it.testDivisor.toLong() }
        .reduce { d1, d2 -> d1 * d2 }

    for (round in 1..numOfRounds) {
        for (monkey in monkeys) {
            while (monkey.items.isNotEmpty()) {
                val item = monkey.items.removeFirst()
                val newItem = (monkey.operation(item) / worryLevelDivisor) % longDivisorLCM
                val nextMonkey = if (newItem % monkey.testDivisor == 0L) {
                    monkey.testTrueMonkey
                } else {
                    monkey.testFalseMonkey
                }
                monkey.inspectedItems++
                monkeys[nextMonkey].items.add(newItem)
            }
        }
    }
}

private fun monkeyBusiness(monkeys: List<Monkey>): Long {
    var mostActive = Int.MIN_VALUE
    var secondActive = Int.MIN_VALUE

    for (monkey in monkeys) {
        if (monkey.inspectedItems > mostActive) {
            secondActive = mostActive
            mostActive = monkey.inspectedItems
        } else if (monkey.inspectedItems > secondActive) {
            secondActive = monkey.inspectedItems
        }
    }

    return mostActive.toLong() * secondActive
}

private fun part1(input: List<String>): Long {
    val monkeys = monkeys(input)
    simulateRounds(monkeys, 20, 3)
    return monkeyBusiness(monkeys)
}

private fun part2(input: List<String>): Long {
    val monkeys = monkeys(input)
    simulateRounds(monkeys, 10000, 1)
    return monkeyBusiness(monkeys)
}

fun main() {
    val input = readInput("Day11")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
