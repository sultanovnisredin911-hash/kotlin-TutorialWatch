import kotlin.random.Random

// Цвета
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val BLUE = "\u001b[34m"
const val GRAY = "\u001b[90m"

// Настройки
const val WIDTH = 8
const val HEIGHT = 8
const val MINES = 10

fun main() {
    println("$CYAN=== 🕵️‍♂️ САПЁР ===$RESET")
    println("Управление:")
    println("  - Открыть: 'o <строка> <столбец>' (например: o 1 1)")
    println("  - Поставить/снять флаг: 'f <строка> <столбец>'")
    println("  - Выход: 'выход'")

    while (true) {
        val board = Board()
        board.print()

        while (board.inProgress) {
            print("$CYAN Ваш ход: $RESET")
            val input = readLine()?.trim()?.lowercase() ?: continue

            if (input == "выход") break

            val parts = input.split(" ").filter { it.isNotBlank() }
            if (parts.size != 3 || parts[0] !in listOf("o", "f")) {
                println("$RED❌ Формат: o/f строка столбец$RESET")
                continue
            }

            val action = parts[0]
            val row = parts[1].toIntOrNull()?.minus(1)
            val col = parts[2].toIntOrNull()?.minus(1)

            if (row == null || col == null || row !in 0 until HEIGHT || col !in 0 until WIDTH) {
                println("$RED❌ Введите числа от 1 до $WIDTH/$HEIGHT$RESET")
                continue
            }

            when (action) {
                "o" -> board.open(row, col)
                "f" -> board.toggleFlag(row, col)
            }
            board.print()
        }

        if (board.isWin()) {
            println("$GREEN🎉 ПОБЕДА! Вы нашли все мины!$RESET")
        } else {
            println("$RED💀 ВЫ ПРОИГРАЛИ!$RESET")
            board.revealAll()
            board.print()
        }

        println("\n$CYAN Сыграем ещё? (да/нет)$RESET")
        if (readLine()?.trim()?.lowercase() !in listOf("да", "yes", "д", "y")) break
    }
    println("$GREEN Спасибо за игру!$RESET")
}

class Board {
    private val hidden = Array(HEIGHT) { BooleanArray(WIDTH) { true } }
    private val hasMine = Array(HEIGHT) { BooleanArray(WIDTH) { false } }
    private val flagged = Array(HEIGHT) { BooleanArray(WIDTH) { false } }
    var inProgress = true

    init {
        placeMines()
    }

    private fun placeMines() {
        var placed = 0
        while (placed < MINES) {
            val r = Random.nextInt(HEIGHT)
            val c = Random.nextInt(WIDTH)
            if (!hasMine[r][c]) {
                hasMine[r][c] = true
                placed++
            }
        }
    }

    fun open(row: Int, col: Int) {
        if (!hidden[row][col] || flagged[row][col]) return
        if (hasMine[row][col]) {
            inProgress = false
            return
        }

        hidden[row][col] = false

        // Если вокруг нет мин — открываем рекурсивно
        if (countMinesAround(row, col) == 0) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = row + dr
                    val nc = col + dc
                    if (nr in 0 until HEIGHT && nc in 0 until WIDTH && hidden[nr][nc]) {
                        open(nr, nc)
                    }
                }
            }
        }
    }

    fun toggleFlag(row: Int, col: Int) {
        if (!hidden[row][col]) return
        flagged[row][col] = !flagged[row][col]
    }

    fun print() {
        print("   ")
        for (j in 0 until WIDTH) print("${j + 1} ")
        println()
        print("   ")
        for (j in 0 until WIDTH) print("- ")
        println()

        for (i in 0 until HEIGHT) {
            print("${i + 1}| ")
            for (j in 0 until WIDTH) {
                val cell = getCellDisplay(i, j)
                print("$cell ")
            }
            println()
        }
        println()
    }

    private fun getCellDisplay(row: Int, col: Int): String {
        if (flagged[row][col]) return "$RED F$RESET"
        if (hidden[row][col]) return "$CYAN?$RESET"
        if (hasMine[row][col]) return "$RED*$RESET"
        val count = countMinesAround(row, col)
        return when (count) {
            0 -> " "
            1 -> "$BLUE 1$RESET"
            2 -> "$GREEN 2$RESET"
            3 -> "$YELLOW 3$RESET"
            else -> "$RED$count$RESET"
        }
    }

    private fun countMinesAround(row: Int, col: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until HEIGHT && nc in 0 until WIDTH && hasMine[nr][nc]) {
                    count++
                }
            }
        }
        return count
    }

    fun isWin(): Boolean {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                if (hasMine[i][j] && !flagged[i][j]) return false
                if (!hasMine[i][j] && hidden[i][j]) return false
            }
        }
        inProgress = false
        return true
    }

    fun revealAll() {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                hidden[i][j] = false
            }
        }
    }
}