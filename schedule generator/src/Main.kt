import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"

// Файл для хранения
private const val SCHEDULE_FILE = "schedule.txt"

data class Lesson(
    val subject: String,
    val day: String,
    val time: String,
    val week: String // "Все", "Верхняя", "Нижняя"
)

// Порядок дней недели
fun dayOrder(day: String): Int = when (day) {
    "Понедельник" -> 1
    "Вторник" -> 2
    "Среда" -> 3
    "Четверг" -> 4
    "Пятница" -> 5
    "Суббота" -> 6
    "Воскресенье" -> 7
    else -> 8
}

fun main() {
    val schedule = loadSchedule()
    println("$CYAN=== 📅 ГЕНЕРАТОР РАСПИСАНИЯ ===$RESET")
    println("Управляйте своим учебным графиком!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addLesson(schedule)
            "2" -> viewAll(schedule)
            "3" -> viewByDay(schedule)
            "4" -> searchSubject(schedule)
            "5" -> viewByWeek(schedule)
            "6" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–6$RESET")
        }
        saveSchedule(schedule)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить занятие")
    println("2. Все занятия")
    println("3. По дням недели")
    println("4. Поиск по предмету")
    println("5. По типу недели")
    println("6. Выход")
}

fun addLesson(schedule: MutableList<Lesson>) {
    print("Название предмета: ")
    val subject = readLine()?.trim() ?: ""
    if (subject.isEmpty()) {
        println("$RED❌ Название не может быть пустым.$RESET")
        return
    }

    println("День недели:")
    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    days.forEachIndexed { i, day -> println("${i + 1}. $day") }

    val dayIndex = readLine()?.toIntOrNull()?.minus(1)
    val day = if (dayIndex in days.indices) {
        dayIndex?.let { days[it] }
    } else {
        println("$RED❌ Неверный выбор. По умолчанию: Понедельник.$RESET")
        "Понедельник"
    }

    print("Время (например, 10:00): ")
    val time = readLine()?.trim() ?: "09:00"

    println("Тип недели:")
    println("1. Все недели")
    println("2. Верхняя неделя")
    println("3. Нижняя неделя")

    val week = when (readLine()?.trim()) {
        "2" -> "Верхняя"
        "3" -> "Нижняя"
        else -> "Все"
    }

    schedule.add(Lesson(subject, day.toString(), time, week))
    println("$GREEN✅ Занятие добавлено: '$subject' в $day в $time$RESET")
}

fun viewAll(schedule: List<Lesson>) {
    if (schedule.isEmpty()) {
        println("$YELLOW📌 Расписание пусто.$RESET")
        return
    }
    println("\n$CYAN=== ВСЕ ЗАНЯТИЯ ===$RESET")
    val sorted = schedule.sortedWith(compareBy({ dayOrder(it.day) }, { it.time }))
    for (lesson in sorted) {
        println("${lesson.week} | ${lesson.day} ${lesson.time} — ${lesson.subject}")
    }
}

fun viewByDay(schedule: List<Lesson>) {
    if (schedule.isEmpty()) {
        println("$YELLOW📌 Расписание пусто.$RESET")
        return
    }

    println("Выберите день:")
    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    days.forEachIndexed { i, day -> println("${i + 1}. $day") }

    val index = readLine()?.toIntOrNull()?.minus(1)

    if (index !in days.indices) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }
    val selectedDay = index?.let { days[it] }

    val lessons = schedule.filter { it.day == selectedDay }.sortedBy { it.time }
    if (lessons.isEmpty()) {
        println("$YELLOW📌 В $selectedDay нет занятий.$RESET")
    } else {
        println("\n$GREEN=== $selectedDay ===$RESET")
        for (lesson in lessons) {
            println("${lesson.time} — ${lesson.subject} (${lesson.week})")
        }
    }
}

fun searchSubject(schedule: List<Lesson>) {
    if (schedule.isEmpty()) {
        println("$YELLOW📌 Расписание пусто.$RESET")
        return
    }

    print("Поиск по названию предмета: ")
    val query = readLine()?.trim()?.lowercase() ?: ""
    if (query.isEmpty()) return

    val results = schedule.filter { it.subject.lowercase().contains(query) }

    if (results.isEmpty()) {
        println("$RED❌ Предмет не найден.$RESET")
    } else {
        println("\n$BLUE=== НАЙДЕНО (${results.size}) ===$RESET")
        for (lesson in results) {
            println("${lesson.day}, ${lesson.time} — ${lesson.subject} (${lesson.week})")
        }
    }
}

fun viewByWeek(schedule: List<Lesson>) {
    if (schedule.isEmpty()) {
        println("$YELLOW📌 Расписание пусто.$RESET")
        return
    }

    println("1. Верхняя неделя")
    println("2. Нижняя неделя")
    print("Выберите неделю: ")
    val input = readLine()?.trim()
    val week = when (input) {
        "1" -> "Верхняя"
        "2" -> "Нижняя"
        else -> {
            println("$RED❌ Неверный выбор.$RESET")
            return
        }
    }

    val filtered = schedule.filter { it.week == week || it.week == "Все" }
        .sortedWith(compareBy({ dayOrder(it.day) }, { it.time }))

    if (filtered.isEmpty()) {
        println("$YELLOW📌 Нет занятий на этой неделе.$RESET")
    } else {
        println("\n$CYAN=== $week НЕДЕЛЯ ===$RESET")
        var currentDay = ""
        for (lesson in filtered) {
            if (lesson.day != currentDay) {
                println("\n$YELLOW${lesson.day}:$RESET")
                currentDay = lesson.day
            }
            println("  ${lesson.time} — ${lesson.subject}")
        }
    }
}

// Загрузка из файла
fun loadSchedule(): MutableList<Lesson> {
    val file = File(SCHEDULE_FILE)
    if (!file.exists()) return mutableListOf()

    val lines = try {
        file.readLines()
    } catch (e: Exception) {
        println("$RED❌ Не удалось прочитать файл: ${e.message}$RESET")
        return mutableListOf()
    }

    val schedule = mutableListOf<Lesson>()
    for (line in lines) {
        if (line.isBlank()) continue
        val parts = line.split("|")
        if (parts.size == 4) {
            val (subject, day, time, week) = parts
            schedule.add(Lesson(subject, day, time, week))
        }
    }
    return schedule
}

// Сохранение в файл
fun saveSchedule(schedule: List<Lesson>) {
    try {
        val text = schedule.joinToString("\n") { "${it.subject}|${it.day}|${it.time}|${it.week}" }
        File(SCHEDULE_FILE).writeText(text)
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}