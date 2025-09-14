import java.security.SecureRandom
import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"

// Файл для сохранения имён паролей
private const val PASSWORDS_FILE = "passwords.txt"

// Наборы символов
private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val DIGITS = "0123456789"
private const val SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?"

fun main() {
    println("$CYAN=== 🔐 ГЕНЕРАТОР ПАРОЛЕЙ ===$RESET")
    println("Создавайте и анализируйте надёжные пароли!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> generatePassword()
            "2" -> analyzePassword()
            "3" -> viewSaved()
            "4" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–4$RESET")
        }
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Сгенерировать пароль")
    println("2. Проанализировать пароль")
    println("3. Сохранённые имена")
    println("4. Выход")
}

fun generatePassword() {
    print("Длина пароля (6–100): ")
    val length = readLine()?.toIntOrNull()
    if (length == null || length !in 6..100) {
        println("$RED❌ Длина: от 6 до 100.$RESET")
        return
    }

    var chars = ""
    println("Включить: (введите да/нет)")
    print("  Буквы (a-z, A-Z)? ")
    if (readLine()?.lowercase() in listOf("да", "y", "yes")) {
        chars += LOWER + UPPER
    }

    print("  Цифры (0-9)? ")
    if (readLine()?.lowercase() in listOf("да", "y", "yes")) {
        chars += DIGITS
    }

    print("  Спецсимволы? (!@#...)? ")
    if (readLine()?.lowercase() in listOf("да", "y", "yes")) {
        chars += SPECIAL
    }

    if (chars.isEmpty()) {
        println("$RED❌ Выберите хотя бы один тип символов.$RESET")
        return
    }

    val password = generateRandomString(length, chars)
    println("\n$GREEN🔐 Сгенерированный пароль: $password$RESET")

    print("Сохранить имя (не сам пароль!)? (например: 'Gmail'): ")
    val name = readLine()?.trim()
    if (name.isNullOrBlank()) {
        println("Имя не сохранено.")
    } else {
        savePasswordName(name)
        println("$YELLOW📌 Имя '$name' сохранено.$RESET")
    }
}

fun generateRandomString(length: Int, chars: String): String {
    val random = SecureRandom()
    return (1..length).map { chars[random.nextInt(chars.length)] }.joinToString("")
}

fun analyzePassword() {
    print("Введите пароль для анализа: ")
    val password = readLine() ?: return

    var score = 0
    val feedback = mutableListOf<String>()

    if (password.length >= 8) score += 2 else feedback.add("❌ Слишком короткий (мин. 8)")
    if (password.length >= 12) score += 1
    if (password.any { it in LOWER }) score += 1 else feedback.add("❌ Нет строчных букв")
    if (password.any { it in UPPER }) score += 1 else feedback.add("❌ Нет заглавных букв")
    if (password.any { it in DIGITS }) score += 1 else feedback.add("❌ Нет цифр")
    if (password.any { it in SPECIAL }) score += 1 else feedback.add("❌ Нет спецсимволов")
    if (password.length >= 16) score += 1

    val strength = when {
        score >= 6 -> "$GREEN Надёжный$RESET"
        score >= 4 -> "$YELLOW Средний$RESET"
        else -> "$RED Слабый$RESET"
    }

    println("\n$CYAN=== АНАЛИЗ ПАРОЛЯ ===$RESET")
    println("Длина: ${password.length}")
    println("Оценка: $strength ($score/7)")
    if (feedback.isEmpty()) {
        println("$GREEN✅ Отличный пароль!$RESET")
    } else {
        feedback.forEach { println(it) }
    }
}

fun savePasswordName(name: String) {
    try {
        File(PASSWORDS_FILE).appendText("$name\n")
    } catch (e: Exception) {
        println("$RED❌ Не удалось сохранить: ${e.message}$RESET")
    }
}

fun viewSaved() {
    val file = File(PASSWORDS_FILE)
    if (!file.exists()) {
        println("$YELLOW📌 Нет сохранённых имён.$RESET")
        return
    }

    val names = file.readLines().filter { it.isNotBlank() }
    if (names.isEmpty()) {
        println("$YELLOW📌 Нет сохранённых имён.$RESET")
        return
    }

    println("\n$BLUE=== СОХРАНЁННЫЕ ИМЕНА ===$RESET")
    names.forEachIndexed { i, name ->
        println("${i + 1}. $name")
    }
}