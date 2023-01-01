private interface MonkeyJob

private data class NumberJob(
    val number: Long
): MonkeyJob

private data class MathsJob(
    val left: String,
    val right: String,
    val operation: String
): MonkeyJob

private val mathsFormat = """(\w+) ([\+\-*/]) (\w+)""".toRegex()

private fun parseJobs(input: List<String>): Map<String, MonkeyJob> {
    return input.map {
        val split = it.split(": ")
        val name = split[0]
        name to try {
            NumberJob(split[1].toLong())
        } catch (e: NumberFormatException) {
            val matchResult = mathsFormat.matchEntire(split[1])
            MathsJob(
                matchResult!!.groupValues[1],
                matchResult.groupValues[3],
                matchResult.groupValues[2]
            )
        }
    }.toMap()
}

private fun resolve(name: String, jobs: Map<String, MonkeyJob>): Long {
    val monkey = jobs[name]!!
    if (monkey is NumberJob) {
        return monkey.number
    } else if (monkey is MathsJob) {
        val left = resolve(monkey.left, jobs)
        val right = resolve(monkey.right, jobs)
        return when (monkey.operation) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> left / right
            else -> throw IllegalStateException("Unknown operation $monkey")
        }
    }

    throw IllegalStateException("Unknown job $monkey")
}

private fun part1(input: List<String>): Long {
    val jobs = parseJobs(input)

    return resolve("root", jobs)
}

private fun hasHumn(name: String, jobs: Map<String, MonkeyJob>): Boolean {
    if (name == "humn") {
        return true
    }

    val job = jobs[name]

    if (job is NumberJob) {
        return false
    }

    job as MathsJob
    return hasHumn(job.left, jobs) || hasHumn(job.right, jobs)
}

private fun findHumn(name: String, expectedValue: Long, jobs: Map<String, MonkeyJob>): Long {
    if (name == "humn") {
        return expectedValue
    }

    val job = jobs[name] as MathsJob
    return if (hasHumn(job.left, jobs)) {
        val right = resolve(job.right, jobs)
        val leftExpectedValue = when (job.operation) {
            "+" -> expectedValue - right
            "-" -> expectedValue + right
            "*" -> expectedValue / right
            "/" -> expectedValue * right
            else -> throw IllegalStateException("Unknown operation $job")
        }
        findHumn(job.left, leftExpectedValue, jobs)
    } else {
        val left = resolve(job.left, jobs)
        val rightExpectedValue = when (job.operation) {
            "+" -> expectedValue - left
            "-" -> left - expectedValue
            "*" -> expectedValue / left
            "/" -> left / expectedValue
            else -> throw IllegalStateException("Unknown operation $job")
        }
        findHumn(job.right, rightExpectedValue, jobs)
    }
}

private fun part2(input: List<String>): Long {
    val jobs = parseJobs(input)

    val root = jobs["root"] as MathsJob
    return if (hasHumn(root.left, jobs)) {
        val expectedValue = resolve(root.right, jobs)
        findHumn(root.left, expectedValue, jobs)
    } else {
        val expectedValue = resolve(root.left, jobs)
        findHumn(root.right, expectedValue, jobs)
    }
}

fun main() {
    val input = readInput("Day21")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
