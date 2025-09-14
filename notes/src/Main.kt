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

// Папка для заметок
private const val NOTES_DIR = "notes"

// Формат даты
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun main() {
    // Создаём папку, если её нет
    File(NOTES_DIR).mkdirs()

    println("$CYAN=== 📝 ЗАМЕТКИ ===$RESET")
    println("Ваш личный блокнот в консоли!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> createNote()
            "2" -> listNotes()
            "3" -> readNote()
            "4" -> searchNotes()
            "5" -> deleteNote()
            "6" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–6$RESET")
        }
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Создать заметку")
    println("2. Список заметок")
    println("3. Прочитать заметку")
    println("4. Поиск")
    println("5. Удалить заметку")
    println("6. Выход")
}

fun createNote() {
    print("Заголовок: ")
    val title = readLine()?.trim()
    if (title.isNullOrBlank()) {
        println("$RED❌ Заголовок не может быть пустым.$RESET")
        return
    }

    println("Текст заметки (введите 'END' на новой строке для завершения):")
    val lines = mutableListOf<String>()
    while (true) {
        val line = readLine() ?: break
        if (line == "END") break
        lines.add(line)
    }

    val content = lines.joinToString("\n")
    val filename = "${title.replace(Regex("[/\\\\:*?\"<>|]"), "_")}.txt"
    val file = File(NOTES_DIR, filename)

    if (file.exists()) {
        println("$YELLOW⚠️ Заметка с таким именем уже есть. Перезаписать? (д/н)$RESET")
        if (readLine()?.lowercase() !in listOf("д", "да", "y", "yes")) return
    }

    try {
        file.writeText("""# $title
|Создано: ${LocalDateTime.now().format(formatter)}
|
|$content
""".trimMargin("|"))
        println("$GREEN✅ Заметка '$title' сохранена!$RESET")
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}

fun listNotes() {
    val files = File(NOTES_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет заметок.$RESET")
        return
    }

    println("\n$CYAN=== СПИСОК ЗАМЕТОК ===$RESET")
    files.sortByDescending { it.lastModified() }
    for ((i, file) in files.withIndex()) {
        val title = file.nameWithoutExtension
        val content = file.useLines { it.firstOrNull() } ?: "Без заголовка"
        val dateLine = file.useLines { it.elementAtOrNull(1) } ?: ""
        val date = dateLine.removePrefix("Создано: ")
        println("${i + 1}. $GREEN$title$RESET — $date")
    }
}

fun readNote() {
    val files = File(NOTES_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет заметок.$RESET")
        return
    }

    listNotes()
    print("Номер заметки: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in files.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val file = files[index]
    println("\n$BLUE--- ${file.nameWithoutExtension} ---$RESET")
    file.useLines { lines ->
        var first = true
        for (line in lines) {
            if (first && line.startsWith("# ")) continue
            if (line.startsWith("Создано:")) {
                println("$YELLOW$line$RESET")
                continue
            }
            if (line.isEmpty()) continue
            println(line)
        }
    }
    println()
}

fun searchNotes() {
    val files = File(NOTES_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет заметок.$RESET")
        return
    }

    print("Поиск по заголовку: ")
    val query = readLine()?.trim()?.lowercase() ?: ""
    if (query.isEmpty()) return

    val results = files.filter {
        it.nameWithoutExtension.lowercase().contains(query)
    }

    if (results.isEmpty()) {
        println("$RED❌ Ничего не найдено.$RESET")
    } else {
        println("\n$GREEN=== НАЙДЕНО (${results.size}) ===$RESET")
        for (file in results) {
            println("🔹 ${file.nameWithoutExtension}")
        }
    }
}

fun deleteNote() {
    val files = File(NOTES_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет заметок.$RESET")
        return
    }

    listNotes()
    print("Номер для удаления: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in files.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val file = files[index]
    print("$RED Удалить '${file.nameWithoutExtension}'? (д/н): $RESET")
    if (readLine()?.lowercase() in listOf("д", "да", "y", "yes")) {
        if (file.delete()) {
            println("$YELLOW🗑 Заметка удалена.$RESET")
        } else {
            println("$RED❌ Не удалось удалить.$RESET")
        }
    }
}