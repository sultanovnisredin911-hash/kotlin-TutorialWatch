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

// Папка для отчётов
private const val REPORTS_DIR = "reports"

// Формат даты
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun main() {
    // Создаём папку, если её нет
    File(REPORTS_DIR).mkdirs()

    println("$CYAN=== 📄 ГЕНЕРАТОР ОТЧЁТОВ ===$RESET")
    println("Создавайте профессиональные отчёты за пару минут!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> createReport()
            "2" -> listReports()
            "3" -> readReport()
            "4" -> searchReports()
            "5" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–5$RESET")
        }
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Создать отчёт")
    println("2. Список отчётов")
    println("3. Прочитать отчёт")
    println("4. Поиск")
    println("5. Выход")
}

fun createReport() {
    print("Название отчёта: ")
    val title = readLine()?.trim()
    if (title.isNullOrBlank()) {
        println("$RED❌ Название не может быть пустым.$RESET")
        return
    }

    print("Автор: ")
    val author = readLine()?.trim() ?: "Аноним"

    // Создаём шаблон
    val date = LocalDateTime.now()
    val filename = "${title.replace(Regex("[/\\\\:*?\"<>|]"), "_")}.txt"
    val file = File(REPORTS_DIR, filename)

    if (file.exists()) {
        println("$YELLOW⚠️ Файл уже существует. Перезаписать? (д/н)$RESET")
        if (readLine()?.lowercase() !in listOf("д", "да", "y", "yes")) return
    }

    println("Введите содержимое разделов. Введите 'END' на новой строке для завершения ввода раздела.")

    print("Введение: ")
    val intro = readMultiLine()

    print("Поставленные задачи: ")
    val tasks = readMultiLine()

    print("Результаты: ")
    val results = readMultiLine()

    print("Выводы: ")
    val conclusions = readMultiLine()

    // Формируем отчёт
    val content = """
        # $title
        
        **Автор:** $author
        **Дата:** ${date.format(formatter)}
        
        ## Введение
        $intro
        
        ## Поставленные задачи
        $tasks
        
        ## Результаты
        $results
        
        ## Выводы
        $conclusions
        
        --- 
        Сгенерировано с помощью ReportGen Kotlin
    """.trimIndent()

    try {
        file.writeText(content)
        println("$GREEN✅ Отчёт '$title' успешно создан!$RESET")
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}

// Универсальная функция для многострочного ввода
fun readMultiLine(): String {
    val lines = mutableListOf<String>()
    while (true) {
        val line = readLine() ?: break
        if (line == "END") break
        lines.add(line)
    }
    return lines.joinToString("\n")
}

fun listReports() {
    val files = File(REPORTS_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет отчётов.$RESET")
        return
    }

    println("\n$CYAN=== СПИСОК ОТЧЁТОВ ===$RESET")
    files.sortByDescending { it.lastModified() }
    for ((i, file) in files.withIndex()) {
        val title = file.nameWithoutExtension
        val content = file.useLines { it.firstOrNull { it.startsWith("# ") }?.removePrefix("# ") }
            ?: title
        val dateLine = file.useLines { it.elementAtOrNull(2) } ?: ""
        val date = if (dateLine.contains("Дата:")) dateLine.substringAfter("Дата:").trim() else "Неизвестно"
        println("${i + 1}. $GREEN$content$RESET — $date")
    }
}

fun readReport() {
    val files = File(REPORTS_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет отчётов.$RESET")
        return
    }

    listReports()
    print("Номер отчёта: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in files.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val file = files[index]
    println("\n$BLUE--- ${file.nameWithoutExtension} ---$RESET")
    println()
    file.useLines { lines ->
        for (line in lines) {
            val colored = when {
                line.startsWith("# ") -> "$CYAN$line$RESET"
                line.startsWith("## ") -> "$YELLOW$line$RESET"
                line.startsWith("**") -> "$GREEN$line$RESET"
                line.startsWith("---") -> "$BLUE$line$RESET"
                else -> "  $line"
            }
            println(colored)
        }
    }
    println()
}

fun searchReports() {
    val files = File(REPORTS_DIR).listFiles { it.extension == "txt" } ?: emptyArray()
    if (files.isEmpty()) {
        println("$YELLOW📌 Нет отчётов.$RESET")
        return
    }

    print("Поиск по названию: ")
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