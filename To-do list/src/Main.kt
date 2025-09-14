import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val STRIKE = "\u001b[9m" // зачёркнутый текст

// Файл для хранения задач
val TASKS_FILE = "tasks.txt"

data class Task(
    val text: String,
    var isDone: Boolean = false
)

fun main() {
    val tasks = loadTasks()
    println("$CYAN=== 📝 МЕНЕДЖЕР ЗАДАЧ ===$RESET")
    println("Управляй своими делами!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addTask(tasks)
            "2" -> viewTasks(tasks)
            "3" -> markTaskDone(tasks)
            "4" -> deleteTask(tasks)
            "5" -> {
                println("До свидания! 👋")
                break
            }
            else -> println("$RED❌ Выберите 1–5$RESET")
        }
        saveTasks(tasks)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить задачу")
    println("2. Просмотреть задачи")
    println("3. Отметить как выполненную")
    println("4. Удалить задачу")
    println("5. Выход")
}

fun addTask(tasks: MutableList<Task>) {
    print("Введите задачу: ")
    val text = readLine()?.trim()
    if (text.isNullOrBlank()) {
        println("$RED❌ Задача не может быть пустой.$RESET")
        return
    }
    tasks.add(Task(text))
    println("$GREEN✅ Задача добавлена!$RESET")
}

fun viewTasks(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        println("$YELLOW📌 Нет задач.$RESET")
        return
    }
    println("\n$CYAN=== ВАШИ ЗАДАЧИ ===$RESET")
    for (i in tasks.indices) {
        val task = tasks[i]
        val status = if (task.isDone) "$GREEN✅$RESET" else "$RED❌$RESET"
        val text = if (task.isDone) "$STRIKE${task.text}$RESET" else task.text
        println("${i + 1}. $status $text")
    }
}

fun markTaskDone(tasks: MutableList<Task>) {
    viewTasks(tasks)
    if (tasks.isEmpty()) return

    print("Номер задачи для отметки: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in tasks.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }
    tasks[index].isDone = true
    println("$GREEN🎉 Задача выполнена!$RESET")
}

fun deleteTask(tasks: MutableList<Task>) {
    viewTasks(tasks)
    if (tasks.isEmpty()) return

    print("Номер задачи для удаления: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in tasks.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }
    val removed = tasks.removeAt(index)
    println("$YELLOW🗑 Удалено: '${removed.text}'$RESET")
}

// Загрузка задач из файла
fun loadTasks(): MutableList<Task> {
    val file = File(TASKS_FILE)
    if (!file.exists()) return mutableListOf()

    return file.readLines().mapNotNull { line ->
        if (line.startsWith("DONE:")) {
            Task(line.substring(5), true)
        } else if (line.startsWith("TODO:")) {
            Task(line.substring(5), false)
        } else null
    }.toMutableList()
}

// Сохранение задач в файл
fun saveTasks(tasks: List<Task>) {
    try {
        File(TASKS_FILE).writeText(
            tasks.joinToString("\n") { task ->
                if (task.isDone) "DONE:${task.text}" else "TODO:${task.text}"
            }
        )
    } catch (e: Exception) {
        println("$RED❌ Не удалось сохранить задачи: ${e.message}$RESET")
    }
}