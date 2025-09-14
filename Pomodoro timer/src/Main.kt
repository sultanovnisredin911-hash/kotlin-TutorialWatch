import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"

// Настройки времени (в секундах)
private const val WORK_TIME = 25 * 60      // 25 минут
private const val SHORT_BREAK = 5 * 60     // 5 минут
private const val LONG_BREAK = 15 * 60     // 15 минут
private const val SESSIONS_BEFORE_LONG = 4

// Файл для логов
private const val LOG_FILE = "pomodoro_log.txt"

// Формат даты
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun main() {
    var sessionCount = 0
    println("$CYAN=== 🍅 POMODORO-ТАЙМЕР ===$RESET")
    println("Техника Помодоро: 25 мин работы → 5 мин отдыха")
    println("После 4 сессий — длинный перерыв (15 мин)")

    while (true) {
        printMenu(sessionCount)
        when (readLine()?.trim()) {
            "1" -> {
                sessionCount = startWorkSession(sessionCount)
            }
            "2" -> showLog()
            "3" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–3$RESET")
        }
    }
}

fun printMenu(sessionCount: Int) {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("Сессий завершено: $sessionCount")
    println("1. Начать сессию работы")
    println("2. Просмотреть лог")
    println("3. Выход")
}

fun startWorkSession(currentCount: Int): Int {
    val sessionCount = currentCount + 1
    val isLongBreak = sessionCount % SESSIONS_BEFORE_LONG == 0

    log("Сессия $sessionCount: начало работы")
    println("\n$GREEN=== 🍅 РАБОЧАЯ СЕССИЯ ($sessionCount) ===$RESET")
    countdown("Работа", WORK_TIME)

    // Перерыв
    if (isLongBreak) {
        log("Сессия $sessionCount: длинный перерыв (15 мин)")
        println("\n$BLUE=== 🌊 ДЛИННЫЙ ПЕРЕРЫВ (15 мин) ===$RESET")
        countdown("Длинный перерыв", LONG_BREAK)
    } else {
        log("Сессия $sessionCount: короткий перерыв (5 мин)")
        println("\n$YELLOW=== ⏸ КОРОТКИЙ ПЕРЕРЫВ (5 мин) ===$RESET")
        countdown("Перерыв", SHORT_BREAK)
    }

    return sessionCount
}

// Обратный отсчёт
fun countdown(label: String, seconds: Int) {
    for (remaining in seconds downTo 1) {
        val min = remaining / 60
        val sec = remaining % 60
        print("\r$label: ${min}м ${sec}с    ")
        Thread.sleep(1000)
    }
    print("\r✅ $label завершён!     \n")
    print('\u0007') // системный звук
}

// Запись в лог
fun log(message: String) {
    val timestamp = LocalDateTime.now().format(formatter)
    try {
        File(LOG_FILE).appendText("[$timestamp] $message\n")
    } catch (e: Exception) {
        println("$RED❌ Не удалось записать в лог: ${e.message}$RESET")
    }
}

// Просмотр лога
fun showLog() {
    val file = File(LOG_FILE)
    if (!file.exists()) {
        println("$YELLOW📌 Лог пока пуст.$RESET")
        return
    }

    val lines = file.readLines()
    if (lines.isEmpty()) {
        println("$YELLOW📌 Лог пока пуст.$RESET")
        return
    }

    println("\n$BLUE=== ИСТОРИЯ СЕССИЙ ===$RESET")
    lines.forEach { println(it) }
}