import kotlin.random.Random

// Цвета для плиток
const val RESET = "\u001b[0m"
const val BG_2 = "\u001b[47;30m"     // Серый фон, чёрный текст
const val BG_4 = "\u001b[47;30m"
const val BG_8 = "\u001b[43;30m"     // Жёлтый
const val BG_16 = "\u001b[43;30m"
const val BG_32 = "\u001b[42;37m"    // Зелёный
const val BG_64 = "\u001b[42;37m"
const val BG_128 = "\u001b[46;37m"   // Бирюзовый
const val BG_256 = "\u001b[46;37m"
const val BG_512 = "\u001b[45;37m"   // Фиолетовый
const val BG_1024 = "\u001b[45;37m"
const val BG_2048 = "\u001b[41;37m"  // Красный

// Размер поля
const val SIZE = 4

fun main() {
    var highScore = loadHighScore()

    while (true) {
        println("$BG_2048=== 🎯 2048 PRO ===$RESET")
        println("Рекорд: $highScore")
        println("Управление: W (вверх), A (влево), S (вниз), D (вправо)")
        println("1. Новая игра")
        println("2. Очистить рекорд")
        println("3. Выход")

        print("Ваш выбор: ")
        when (readLine()?.trim()) {
            "1" -> {
                val score = playGame()
                if (score > highScore) {
                    highScore = score
                    saveHighScore(highScore)
                    println("$BG_2048🎉 НОВЫЙ РЕКОРД: $highScore!$RESET")
                }
            }
            "2" -> {
                highScore = 0
                saveHighScore(0)
                println("Рекорд сброшен.")
            }
            "3" -> {
                println("Спасибо за игру! До свидания! 👋")
                break
            }
            else -> println("❌ Выберите 1, 2 или 3")
        }
    }
}

fun playGame(): Int {
    val board = Array(SIZE) { IntArray(SIZE) { 0 } }
    var score = 0
    var won = false

    addRandomTile(board)
    addRandomTile(board)

    while (true) {
        printBoard(board)

        if (hasWon(board) && !won) {
            println("$BG_2048🎉 ПОЗДРАВЛЯЕМ! Вы собрали 2048!$RESET")
            won = true
            // Можно продолжить играть
        }

        if (isGameOver(board)) {
            println("💀 Игра окончена! Ваш счёт: $score")
            return score
        }

        print("Ваш ход (W/A/S/D): ")
        val input = readLine()?.trim()?.lowercase()

        when (input) {
            "w" -> {
                val (moved, points) = moveUp(board)
                if (moved) {
                    addRandomTile(board)
                    score += points
                }
            }
            "s" -> {
                val (moved, points) = moveDown(board)
                if (moved) {
                    addRandomTile(board)
                    score += points
                }
            }
            "a" -> {
                val (moved, points) = moveLeft(board)
                if (moved) {
                    addRandomTile(board)
                    score += points
                }
            }
            "d" -> {
                val (moved, points) = moveRight(board)
                if (moved) {
                    addRandomTile(board)
                    score += points
                }
            }
            "выход", "exit" -> {
                println("До свидания!")
                return score
            }
            else -> {
                println("❌ Используйте W, A, S, D")
                continue
            }
        }
    }
}

// Вывод доски с цветами
fun printBoard(board: Array<IntArray>) {
    println("\n┌─────┬─────┬─────┬─────┐")
    for (i in board.indices) {
        print("│")
        for (j in board[i].indices) {
            val value = board[i][j]
            val (bg, text) = getColor(value)
            val str = if (value == 0) "     " else "%3d  ".format(value)
            print("$bg$text$RESET│")
        }
        println()
        if (i < board.size - 1) {
            println("├─────┼─────┼─────┼─────┤")
        }
    }
    println("└─────┴─────┴─────┴─────┘")
}

// Возвращает цвет фона и текста
fun getColor(value: Int): Pair<String, String> {
    return when (value) {
        0 -> RESET to "     "
        2 -> BG_2 to "  2  "
        4 -> BG_4 to "  4  "
        8 -> BG_8 to "  8  "
        16 -> BG_16 to " 16  "
        32 -> BG_32 to " 32  "
        64 -> BG_64 to " 64  "
        128 -> BG_128 to " 128 "
        256 -> BG_256 to " 256 "
        512 -> BG_512 to " 512 "
        1024 -> BG_1024 to "1024 "
        2048 -> BG_2048 to "2048 "
        else -> BG_2048 to "${value} "
    }
}

// Добавляем случайную плитку
fun addRandomTile(board: Array<IntArray>) {
    val empty = mutableListOf<Pair<Int, Int>>()
    for (i in 0 until SIZE) {
        for (j in 0 until SIZE) {
            if (board[i][j] == 0) {
                empty.add(i to j)
            }
        }
    }
    if (empty.isNotEmpty()) {
        val (i, j) = empty.random()
        board[i][j] = if (Random.nextFloat() < 0.9) 2 else 4
    }
}

// Проверка победы
fun hasWon(board: Array<IntArray>): Boolean {
    for (row in board) {
        for (cell in row) {
            if (cell >= 2048) return true
        }
    }
    return false
}

// Проверка конца игры
fun isGameOver(board: Array<IntArray>): Boolean {
    for (i in 0 until SIZE) {
        for (j in 0 until SIZE) {
            if (board[i][j] == 0) return false
            if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) return false
            if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) return false
        }
    }
    return true
}

// Движение с подсчётом очков
fun moveLeft(board: Array<IntArray>): Pair<Boolean, Int> {
    var moved = false
    var points = 0
    for (row in board) {
        val (newRow, wasMoved, rowPoints) = compressAndMergeWithScore(row)
        for (j in newRow.indices) {
            if (row[j] != newRow[j]) moved = true
            row[j] = newRow[j]
        }
        if (wasMoved) moved = true
        points += rowPoints
    }
    return moved to points
}

fun compressAndMergeWithScore(row: IntArray): Triple<IntArray, Boolean, Int> {
    val temp = row.filter { it != 0 }
    val merged = mutableListOf<Int>()
    var i = 0
    var points = 0
    var moved = false

    while (i < temp.size) {
        if (i < temp.size - 1 && temp[i] == temp[i + 1]) {
            val value = temp[i] * 2
            merged.add(value)
            points += value
            i += 2
            moved = true
        } else {
            merged.add(temp[i])
            i++
        }
    }

    val result = IntArray(SIZE) { 0 }
    for (j in merged.indices) {
        result[j] = merged[j]
    }

    return Triple(result, moved, points)
}

// Остальные движения
fun moveRight(board: Array<IntArray>): Pair<Boolean, Int> {
    for (row in board) row.reverse()
    val (moved, points) = moveLeft(board)
    for (row in board) row.reverse()
    return moved to points
}

fun moveUp(board: Array<IntArray>): Pair<Boolean, Int> {
    transpose(board)
    val (moved, points) = moveLeft(board)
    transpose(board)
    return moved to points
}

fun moveDown(board: Array<IntArray>): Pair<Boolean, Int> {
    transpose(board)
    val (moved, points) = moveRight(board)
    transpose(board)
    return moved to points
}

fun transpose(board: Array<IntArray>) {
    for (i in 0 until SIZE) {
        for (j in i + 1 until SIZE) {
            val temp = board[i][j]
            board[i][j] = board[j][i]
            board[j][i] = temp
        }
    }
}

// Работа с рекордом
fun loadHighScore(): Int {
    return try {
        val file = java.io.File("highscore.txt")
        if (file.exists()) file.readText().trim().toIntOrNull() ?: 0
        else 0
    } catch (e: Exception) {
        0
    }
}

fun saveHighScore(score: Int) {
    try {
        java.io.File("highscore.txt").writeText(score.toString())
    } catch (e: Exception) {
        println("❌ Не удалось сохранить рекорд.")
    }
}