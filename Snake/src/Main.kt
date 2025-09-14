import java.util.*

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val RED = "\u001b[31m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val BG_GRAY = "\u001b[100m"

// Настройки
const val WIDTH = 20
const val HEIGHT = 10
const val TICK_MS = 300 // скорость игры (мс)

fun main() {
    println("$CYAN=== 🐍 ЗМЕЙКА ===$RESET")
    println("Управление: W=вверх, A=влево, S=вниз, D=вправо, Q=выход")

    // Змейка: список (строка, столбец)
    var snake = listOf(Pair(HEIGHT / 2, WIDTH / 2))
    var direction = Pair(0, 1) // вправо
    var food = generateFood(snake)
    var score = 0
    var gameOver = false

    val scanner = Scanner(System.`in`)

    while (!gameOver) {
        // Отрисовка
        print("\u001b[2J\u001b[H") // очистка консоли (работает в большинстве терминалов)
        printBoard(snake, food)
        println("$YELLOW Счёт: $score$RESET")

        // Проверка: есть ли ввод?
        var input: String? = null
        if (scanner.hasNextLine()) {
            input = scanner.nextLine().trim().lowercase()
        }

        // Обработка ввода
        when (input) {
            "w" -> if (direction != Pair(1, 0)) direction = Pair(-1, 0)
            "s" -> if (direction != Pair(-1, 0)) direction = Pair(1, 0)
            "a" -> if (direction != Pair(0, 1)) direction = Pair(0, -1)
            "d" -> if (direction != Pair(0, -1)) direction = Pair(0, 1)
            "q" -> {
                println("Выход из игры.")
                return
            }
        }

        // Движение змеи
        val head = snake.first()
        val newHead = Pair(head.first + direction.first, head.second + direction.second)

        // Проверка на стену и себя
        if (newHead.first !in 1 until HEIGHT - 1 ||
            newHead.second !in 1 until WIDTH - 1 ||
            newHead in snake) {
            println("$RED💀 СТОЛКНОВЕНИЕ!$RESET")
            println("$GREEN Финальный счёт: $score$RESET")
            gameOver = true
            continue
        }

        // Добавляем голову
        snake = listOf(newHead) + snake

        // Проверка на еду
        if (newHead == food) {
            score += 10
            food = generateFood(snake)
        } else {
            snake = snake.dropLast(1) // убираем хвост
        }

        // Пауза — основной таймер игры
        Thread.sleep(TICK_MS.toLong())
    }

    scanner.close()
}

// Отрисовка поля
fun printBoard(snake: List<Pair<Int, Int>>, food: Pair<Int, Int>) {
    for (i in 0 until HEIGHT) {
        for (j in 0 until WIDTH) {
            // Границы
            if (i == 0 || i == HEIGHT - 1 || j == 0 || j == WIDTH - 1) {
                print("$BG_GRAY█$RESET")
            }
            // Голова змеи
            else if (Pair(i, j) == snake.first()) {
                print("$GREEN█$RESET")
            }
            // Тело змеи
            else if (Pair(i, j) in snake) {
                print("$GREEN·$RESET")
            }
            // Еда
            else if (Pair(i, j) == food) {
                print("$RED●$RESET")
            }
            // Пусто
            else {
                print(" ")
            }
        }
        println()
    }
}

// Генерация еды
fun generateFood(snake: List<Pair<Int, Int>>): Pair<Int, Int> {
    while (true) {
        val food = Pair(
            (1..HEIGHT - 2).random(),
            (1..WIDTH - 2).random()
        )
        if (food !in snake) return food
    }
}