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
const val LANE_COUNT = 5
const val CAR_SYMBOL = "🚗"
const val OBSTACLE_SYMBOL = "🟥"
const val ROAD_SYMBOL = "🟫"
const val PLAYER_SYMBOL = "🚙"

fun main() {
    println("$CYAN=== 🚙 ГОНКИ ===$RESET")
    println("Управление: A = влево, D = вправо, Q = выход")
    println("Избегайте препятствий! Счёт растёт со временем.")

    var playerLane = LANE_COUNT / 2  // начальная полоса
    val road = Array(10) { IntArray(LANE_COUNT) { 0 } } // 0 = пусто, 1 = препятствие
    var score = 0
    var speedMs = 800L
    var gameOver = false

    while (!gameOver) {
        // Двигаем дорогу вверх (новая строка снизу)
        for (i in 0 until road.size - 1) {
            road[i] = road[i + 1].copyOf()
        }

        // Новая строка (возможно, с препятствием)
        val newLine = IntArray(LANE_COUNT) { 0 }
        if (Random.nextFloat() < 0.3) { // 30% шанс
            val obsLane = Random.nextInt(LANE_COUNT)
            if (obsLane != playerLane) {
                newLine[obsLane] = 1
            }
        }
        road[road.size - 1] = newLine

        // Проверка столкновения
        if (road[0][playerLane] == 1) {
            println("$RED💀 СТОЛКНОВЕНИЕ!$RESET")
            gameOver = true
            continue
        }

        // Отрисовка
        print("\u001b[2J\u001b[H") // Очистка консоли (работает в большинстве терминалов)
        println("$GREEN Счёт: $score$RESET")
        println("Полосы: 1     2     3     4     5")

        for (i in road.indices) {
            for (j in 0 until LANE_COUNT) {
                if (i == 0 && j == playerLane) {
                    print("$PLAYER_SYMBOL ")
                    continue
                }
                if (road[i][j] == 1) {
                    print("$OBSTACLE_SYMBOL ")
                } else {
                    print("$ROAD_SYMBOL ")
                }
            }
            println()
        }

        // Ввод
        print("$CYAN Ваш ход (A/D/Q): $RESET")
        val input = readLine()?.trim()?.lowercase()

        when (input) {
            "a" -> if (playerLane > 0) playerLane--
            "d" -> if (playerLane < LANE_COUNT - 1) playerLane++
            "q" -> {
                println("Выход из игры.")
                return
            }
        }

        score++
        speedMs = maxOf(200, speedMs - 5) // ускорение
        Thread.sleep(speedMs)
    }

    println("$RED💀 ИГРА ОКОНЧЕНА!$RESET")
    println("$YELLOW Финальный счёт: $score$RESET")
    if (score > 100) println("$GREEN Молодец! Ты проехал далеко!$RESET")
}