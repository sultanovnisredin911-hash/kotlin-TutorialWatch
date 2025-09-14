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
const val SIZE = 6

fun main() {
    println("$CYAN=== ⚓ МОРСКОЙ БОЙ ===$RESET")
    println("Расставьте свои корабли: 3-палубный, 2×2-палубных, 4×1-палубных")

    val playerBoard = Board()
    val botBoard = Board()

    // Игрок расставляет корабли
    playerBoard.placeShipsManually()
    botBoard.placeShipsRandom()

    val playerShots = Array(SIZE) { BooleanArray(SIZE) { false } }
    val botShots = Array(SIZE) { BooleanArray(SIZE) { false } }

    // Логика бота
    var botTargetMode = false // true = попал, нужно добивать
    var botLastHit = Pair(0, 0)
    val botSearchDirections = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0) // П, В, Л, Н
    var botCurrentDirection = 0

    while (true) {
        // Ход игрока
        println("\n$BLUE=== Ваш ход ===$RESET")
        botBoard.print(playerShots, isPlayer = true)

        var (x, y) = getPlayerShot(playerShots)
        playerShots[x][y] = true

        if (botBoard.hit(x, y)) {
            println("$RED🔥 Попадание!$RESET")
            if (botBoard.isSunk(x, y)) {
                println("$YELLOW⚓ Корабль потоплен!$RESET")
            }
        } else {
            println("$CYAN💧 Промах.$RESET")
        }

        if (botBoard.allSunk()) {
            println("$GREEN🎉 ПОБЕДА! Вы победили бота!$RESET")
            break
        }

        // Ход бота
        println("\n$RED=== Ход бота ===$RESET")
        playerBoard.print(playerShots, isPlayer = false)

        val (bx, by) = getBotShot(botShots, playerBoard, botTargetMode, botLastHit, botCurrentDirection)
        botShots[bx][by] = true

        if (playerBoard.hit(bx, by)) {
            println("$RED🤖 Бот попал в ($bx+1, $by+1)!$RESET")
            botTargetMode = true
            botLastHit = bx to by
        } else {
            println("$CYAN🤖 Бот промахнулся.$RESET")
        }

        if (playerBoard.allSunk()) {
            println("$RED💀 Бот победил! Все ваши корабли потоплены.$RESET")
            playerBoard.reveal()
            break
        }

        Thread.sleep(1200)
    }
}

class Board {
    private val board = Array(SIZE) { CharArray(SIZE) { ' ' } }
    private val ships = mutableListOf<Ship>()

    fun placeShipsRandom() {
        val shipSizes = listOf(3, 2, 2, 1, 1, 1, 1)
        for (size in shipSizes) {
            var placed = false
            while (!placed) {
                val horizontal = Random.nextBoolean()
                val row = Random.nextInt(SIZE)
                val col = Random.nextInt(if (horizontal) SIZE - size + 1 else SIZE)
                if (canPlaceShip(row, col, size, horizontal)) {
                    placeShip(row, col, size, horizontal)
                    placed = true
                }
            }
        }
    }

    fun placeShipsManually() {
        val shipSizes = listOf(3, 2, 2, 1, 1, 1, 1)
        val shots = Array(SIZE) { BooleanArray(SIZE) { false } } // пока нет выстрелов

        for (size in shipSizes) {
            // Показываем текущее поле
            this.print(shots, isPlayer = true)
            println("Разместите корабль из $size палубы(ы):")

            while (true) {
                print("Горизонтально? (д/н): ")
                val horStr = readLine()?.lowercase() ?: "н"
                val horizontal = horStr in listOf("д", "да", "y", "yes")

                print("Строка (1-6): ")
                val row = (readLine()?.toIntOrNull() ?: 0) - 1
                print("Столбец (1-6): ")
                val col = (readLine()?.toIntOrNull() ?: 0) - 1

                if (row !in 0 until SIZE || col !in 0 until SIZE) {
                    println("$RED❌ Неверные координаты.$RESET")
                    continue
                }

                if (!canPlaceShip(row, col, size, horizontal)) {
                    println("$RED❌ Нельзя разместить здесь. Слишком близко к другому кораблю.$RESET")
                    continue
                }

                placeShip(row, col, size, horizontal)
                break
            }
        }
    }

    private fun canPlaceShip(row: Int, col: Int, size: Int, horizontal: Boolean): Boolean {
        for (i in 0 until size) {
            val r = if (horizontal) row else row + i
            val c = if (horizontal) col + i else col
            if (r !in 0 until SIZE || c !in 0 until SIZE) return false
            if (board[r][c] != ' ') return false
        }
        // Проверка соседних клеток
        for (i in 0 until size) {
            val r = if (horizontal) row else row + i
            val c = if (horizontal) col + i else col
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until SIZE && nc in 0 until SIZE && board[nr][nc] != ' ') {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun placeShip(row: Int, col: Int, size: Int, horizontal: Boolean) {
        val ship = Ship()
        for (i in 0 until size) {
            val r = if (horizontal) row else row + i
            val c = if (horizontal) col + i else col
            board[r][c] = '█'
            ship.parts.add(r to c)
        }
        ships.add(ship)
    }

    fun hit(x: Int, y: Int): Boolean {
        if (board[x][y] == '█') {
            board[x][y] = 'X'
            return true
        }
        if (board[x][y] == ' ') {
            board[x][y] = '•'
        }
        return false
    }

    fun isSunk(x: Int, y: Int): Boolean {
        for (ship in ships) {
            if ((x to y) in ship.parts) {
                return ship.parts.all { (r, c) -> board[r][c] == 'X' }
            }
        }
        return false
    }

    fun allSunk(): Boolean = ships.all { it.isSunk(board) }

    fun print(shots: Array<BooleanArray>, isPlayer: Boolean) {
        print("   ")
        for (j in 0 until SIZE) print("${j + 1} ")
        println()
        print("   ")
        for (j in 0 until SIZE) print("- ")
        println()

        for (i in 0 until SIZE) {
            print("${i + 1}| ")
            for (j in 0 until SIZE) {
                val cell = getCellDisplay(i, j, shots[i][j], isPlayer)
                print("$cell ")
            }
            println()
        }
    }

    private fun getCellDisplay(i: Int, j: Int, shot: Boolean, isPlayer: Boolean): String {
        if (!shot) return if (isPlayer) "$CYAN░$RESET" else "$CYAN?$RESET"
        return when (board[i][j]) {
            'X' -> "$RED X$RESET"
            '█' -> "$RED█$RESET"
            '•', ' ' -> "$GRAY•$RESET"
            else -> "$GRAY•$RESET"
        }
    }

    fun reveal() {
        println("\n$RED=== ВАШЕ ПОЛЕ ===$RESET")
        print("   ")
        for (j in 0 until SIZE) print("${j + 1} ")
        println()
        for (i in 0 until SIZE) {
            print("${i + 1} ")
            for (j in 0 until SIZE) {
                print("${board[i][j]} ")
            }
            println()
        }
    }
}

class Ship {
    val parts = mutableListOf<Pair<Int, Int>>()
    fun isSunk(board: Array<CharArray>): Boolean = parts.all { (r, c) -> board[r][c] == 'X' }
}

// Получить ход игрока
fun getPlayerShot(shots: Array<BooleanArray>): Pair<Int, Int> {
    while (true) {
        print("Ваш выстрел (строка столбец): ")
        val input = readLine()?.split(" ")?.filter { it.isNotBlank() }
        if (input?.size != 2) continue
        val x = input[0].toIntOrNull()?.minus(1)
        val y = input[1].toIntOrNull()?.minus(1)
        if (x == null || y == null || x !in 0 until SIZE || y !in 0 until SIZE) {
            println("$RED❌ Введите числа от 1 до $SIZE$RESET")
            continue
        }
        if (shots[x][y]) {
            println("$YELLOW Вы уже стреляли туда!$RESET")
            continue
        }
        return x to y
    }
}

// Получить ход бота (средний уровень)
fun getBotShot(
    shots: Array<BooleanArray>,
    playerBoard: Board,
    targetMode: Boolean,
    lastHit: Pair<Int, Int>,
    currentDirection: Int
): Pair<Int, Int> {

    if (targetMode) {
        // Ищем в направлениях
        val (x, y) = lastHit
        for (i in 0..3) {
            val dirIndex = (currentDirection + i) % 4
            val (dx, dy) = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)[dirIndex]
            val nx = x + dx
            val ny = y + dy
            if (nx in 0 until SIZE && ny in 0 until SIZE && !shots[nx][ny]) {
                return nx to ny
            }
        }
    }

    // Обычный режим — случайный выстрел
    val empty = mutableListOf<Pair<Int, Int>>()
    for (i in 0 until SIZE) {
        for (j in 0 until SIZE) {
            if (!shots[i][j]) {
                empty.add(i to j)
            }
        }
    }
    return empty.random()
}