import kotlin.random.Random

// Цвета
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val BLUE = "\u001b[34m"
const val MAGENTA = "\u001b[35m"
const val CYAN = "\u001b[36m"
const val BG_GRAY = "\u001b[100m"

// Настройки
const val WIDTH = 10
const val HEIGHT = 20

// Начальная скорость (в миллисекундах)
const val START_TICK_MS = 7000
const val MIN_TICK_MS = 100  // Минимальная задержка
const val ACCELERATION = 50  // Ускорение каждые 500 очков

// Все фигуры (тетромино)
val SHAPES = listOf(
    listOf( // I
        listOf(1, 1, 1, 1)
    ),
    listOf( // O
        listOf(1, 1),
        listOf(1, 1)
    ),
    listOf( // T
        listOf(0, 1, 0),
        listOf(1, 1, 1)
    ),
    listOf( // L
        listOf(1, 0),
        listOf(1, 0),
        listOf(1, 1)
    ),
    listOf( // J
        listOf(0, 1),
        listOf(0, 1),
        listOf(1, 1)
    ),
    listOf( // S
        listOf(0, 1, 1),
        listOf(1, 1, 0)
    ),
    listOf( // Z
        listOf(1, 1, 0),
        listOf(0, 1, 1)
    )
)

// Цвета для фигур
val COLORS = listOf(CYAN, YELLOW, MAGENTA, RED, GREEN, BLUE, BG_GRAY)

fun main() {
    println("$CYAN=== 🎮 ТЕТРИС PRO ===$RESET")
    println("Управление: A=влево, D=вправо, S=вниз, W=поворот, Q=выход")

    val board = Array(HEIGHT) { CharArray(WIDTH) { ' ' } }
    var score = 0
    var lines = 0
    var level = 1
    var gameOver = false

    // Первая "следующая" фигура
    var nextShape = SHAPES.random()
    var nextColor = COLORS[SHAPES.indexOf(nextShape)]

    while (!gameOver) {
        // Текущая фигура = следующая
        val shape = nextShape
        val color = nextColor

        // Генерируем новую "следующую"
        nextShape = SHAPES.random()
        nextColor = COLORS[SHAPES.indexOf(nextShape)]

        var row = 0
        var col = WIDTH / 2 - shape[0].size / 2
        var rotated = shape

        // Рассчитываем текущую скорость
        val tickMs = calculateSpeed(score)

        while (true) {
            if (!canPlace(board, rotated, row, col)) {
                if (row <= 0) {
                    gameOver = true
                    break
                } else {
                    place(board, rotated, row - 1, col, color)
                    break
                }
            }

            // Отрисовка
            printBoardWithPreview(board, rotated, row, col, color, nextShape, nextColor, score, level)

            // Ввод и тик
            var moved = false
            val start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < tickMs) {
                if (System.`in`.available() > 0) {
                    when (readLine()?.trim()?.lowercase()) {
                        "a" -> if (canPlace(board, rotated, row, col - 1)) col-- else Unit
                        "d" -> if (canPlace(board, rotated, row, col + 1)) col++ else Unit
                        "s" -> if (canPlace(board, rotated, row + 1, col)) row++ else Unit
                        "w" -> {
                            val rotatedNew = rotate(rotated)
                            if (canPlace(board, rotatedNew, row, col)) rotated = rotatedNew
                        }
                        "q" -> {
                            println("$RED Игра завершена.$RESET")
                            return
                        }
                    }
                    moved = true
                }
                if (moved) break
                Thread.sleep(50)
            }

            row++
        }

        val cleared = removeFullLines(board)
        score += cleared * 100
        lines += cleared

        // Обновляем уровень
        val newLevel = (score / 500) + 1
        if (newLevel > level) {
            level = newLevel
            println("$YELLOW🎉 Уровень $level! Игра ускорилась!$RESET")
            Thread.sleep(1000)
        }
    }

    println("$RED💀 ИГРА ОКОНЧЕНА!$RESET")
    println("$GREEN Финальный счёт: $score$RESET")
    println("$BLUE Пройдено уровней: $level$RESET")
}

// Рассчитываем скорость падения
fun calculateSpeed(score: Int): Int {
    val level = score / 500
    val speed = START_TICK_MS - level * ACCELERATION
    return maxOf(speed, MIN_TICK_MS)
}

// Проверка размещения
fun canPlace(board: Array<CharArray>, shape: List<List<Int>>, row: Int, col: Int): Boolean {
    for (i in shape.indices) {
        for (j in shape[i].indices) {
            if (shape[i][j] == 1) {
                val r = row + i
                val c = col + j
                if (r < 0 || r >= HEIGHT || c < 0 || c >= WIDTH || board[r][c] != ' ') {
                    return false
                }
            }
        }
    }
    return true
}

// Размещение фигуры
fun place(board: Array<CharArray>, shape: List<List<Int>>, row: Int, col: Int, color: String) {
    for (i in shape.indices) {
        for (j in shape[i].indices) {
            if (shape[i][j] == 1) {
                val r = row + i
                val c = col + j
                if (r in 0 until HEIGHT && c in 0 until WIDTH) {
                    board[r][c] = '█'
                }
            }
        }
    }
}

// Поворот фигуры
fun rotate(shape: List<List<Int>>): List<List<Int>> {
    val n = shape.size
    val m = shape[0].size
    val rotated = MutableList(m) { MutableList<Int>(n) { 0 } }
    for (i in shape.indices) {
        for (j in shape[i].indices) {
            rotated[j][n - 1 - i] = shape[i][j]
        }
    }
    return rotated
}

// Удаление заполненных строк
fun removeFullLines(board: Array<CharArray>): Int {
    var lines = 0
    var i = HEIGHT - 1  // начинаем снизу

    while (i >= 0) {
        if (board[i].all { it == '█' }) {
            // Сдвигаем всё вниз
            for (j in i downTo 1) {
                board[j] = board[j - 1].copyOf()
            }
            board[0] = CharArray(WIDTH) { ' ' }
            lines++
            // Не увеличиваем i, потому что на место i пришла новая строка
            // Проверим её на следующей итерации
        } else {
            i--  // переходим к следующей строке
        }
    }

    return lines
}

// Отрисовка поля + следующей фигуры
fun printBoardWithPreview(
    board: Array<CharArray>,
    currentShape: List<List<Int>>,
    row: Int,
    col: Int,
    currentColor: String,
    nextShape: List<List<Int>>,
    nextColor: String,
    score: Int,
    level: Int
) {
    val preview = Array(HEIGHT) { CharArray(WIDTH) {
        val it2 = 0
        board[it][it2]
    } }

    // Рисуем текущую фигуру
    for (i in currentShape.indices) {
        for (j in currentShape[i].indices) {
            if (currentShape[i][j] == 1) {
                val r = row + i
                val c = col + j
                if (r in 0 until HEIGHT && c in 0 until WIDTH) {
                    preview[r][c] = '█'
                }
            }
        }
    }

    // Вывод
    println()

    // Заголовок
    print("  $CYAN Поле$RESET")
    print(" ".repeat(WIDTH * 2 - 4))
    print("  $MAGENTA Следующая$RESET")
    println()

    // Поле
    for (i in preview.indices) {
        print("  |")
        for (j in preview[i].indices) {
            print(if (preview[i][j] == '█') "$currentColor█$RESET " else "  ")
        }
        print("|  ")

        // Следующая фигура (в 4 строках)
        if (i < 4) {
            for (j in 0..3) {
                if (j < nextShape.getOrNull(i)?.size ?: 0 && nextShape[i][j] == 1) {
                    print("$nextColor█$RESET ")
                } else {
                    print("  ")
                }
            }
        } else {
            print("    ")
        }
        println()
    }

    // Низ
    print("  ")
    for (i in 0 until WIDTH * 2 + 2) print("-")
    print("    ")
    for (i in 0..7) print("-")
    println()

    // Информация
    println("  $YELLOW Счёт: $score$RESET | $GREEN Уровень: $level$RESET")
    println()
}