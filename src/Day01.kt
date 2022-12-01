fun main() {
    fun insertMaxHeap(maxHeap: MutableList<Int>, heapSize: Int, entry: Int) {
        var inserted = false;
        for (i in maxHeap.indices) {
            if (entry > maxHeap[i]) {
                maxHeap.add(i, entry)
                inserted = true
                break
            }
        }

        if (!inserted) {
            maxHeap.add(entry)
        }

        if (maxHeap.size > heapSize) {
            maxHeap.removeAt(heapSize)
        }
    }

    fun part1(input: List<String>): Int {
        var maxCalories = Int.MIN_VALUE
        var totalCalories = 0
        for (line in input) {
            if (line.isEmpty()) {
                if (totalCalories > maxCalories) {
                    maxCalories = totalCalories
                }
                totalCalories = 0
            } else {
                val calories = line.toInt()
                totalCalories += calories
            }
        }

        if (totalCalories > maxCalories) {
            maxCalories = totalCalories
        }

        return maxCalories
    }

    fun part2(input: List<String>): Int {
        val maxCaloriesHeap = mutableListOf<Int>()
        var totalCalories = 0
        for (line in input) {
            if (line.isEmpty()) {
                insertMaxHeap(maxCaloriesHeap, 3, totalCalories)
                totalCalories = 0
            } else {
                val calories = line.toInt()
                totalCalories += calories
            }
        }

        insertMaxHeap(maxCaloriesHeap, 3, totalCalories)

        return maxCaloriesHeap.sum()
    }

    val input = readInput("Day01")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
