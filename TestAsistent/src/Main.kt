fun main() {
    println("=== 📝 Тестовый ассистент ===")
    println("Отвечайте номером варианта (1, 2, 3, 4)")

    // Симулируем содержимое файла JSON
    val jsonContent = """
        [
          {
            "question": "Сколько будет 2 + 2?",
            "options": ["3", "4", "5", "6"],
            "correct": 1
          },
          {
            "question": "Какой цвет у неба?",
            "options": ["Зелёный", "Красный", "Синий", "Чёрный"],
            "correct": 2
          },
          {
            "question": "На каком языке написан этот бот?",
            "options": ["Python", "Java", "C++", "Kotlin"],
            "correct": 3
          }
        ]
    """.trimIndent()

    val questions = parseTestJson(jsonContent)

    if (questions.isEmpty()) {
        println("❌ Не удалось загрузить вопросы.")
        return
    }

    var correctAnswers = 0

    for ((index, q) in questions.withIndex()) {
        println("\nВопрос ${index + 1}: ${q.question}")
        for (i in q.options.indices) {
            println("  ${i + 1}. ${q.options[i]}")
        }
        print("Ваш ответ (1–${q.options.size}): ")
        val input = readLine()?.trim()

        val answer = input?.toIntOrNull()?.minus(1) // в индекс (0-based)

        if (answer == q.correct) {
            println("✅ Правильно!")
            correctAnswers++
        } else {
            println("❌ Неверно. Правильный ответ: ${q.options[q.correct]}")
        }
    }

    println("\n🏁 Тест завершён!")
    println("Результат: $correctAnswers из ${questions.size}")
    val percent = correctAnswers * 100 / questions.size
    when {
        percent == 100 -> println("👑 Идеально! Ты гений!")
        percent >= 80 -> println("👏 Отлично!")
        percent >= 60 -> println("👍 Хорошо!")
        else -> println("📚 Продолжай учиться!")
    }
}

// Класс для вопроса
data class Question(
    val question: String,
    val options: List<String>,
    val correct: Int // индекс правильного ответа (0-based)
)

// Простейший "парсер" JSON (для обучения)
fun parseTestJson(json: String): List<Question> {
    val questions = mutableListOf<Question>()
    var i = 0
    val len = json.length

    while (i < len) {
        if (json.startsWith("[", i) || json.startsWith("{", i) || json.startsWith("}", i) || json[i] in " \t\n") {
            i++
            continue
        }

        if (json.startsWith("\"question\"", i)) {
            i += 10 // пропускаем "question"
            val qText = parseStringValue(json, i).also { i = it.second }
            val options = mutableListOf<String>()
            var correctIndex = 0

            // Ищем "options"
            while (i < len && !json.startsWith("\"options\"", i)) i++
            if (json.startsWith("\"options\"", i)) {
                i += 9
                i = skipTo(json, i, '[')
                while (i < len && json[i] != ']') {
                    if (json[i] == '"') {
                        val (opt, newIndex) = parseStringValue(json, i + 1)
                        options.add(opt)
                        i = newIndex
                    } else {
                        i++
                    }
                }
                i++
            }

            // Ищем "correct"
            while (i < len && !json.startsWith("\"correct\"", i)) i++
            if (json.startsWith("\"correct\"", i)) {
                i += 9
                while (i < len && json[i] !in "0123456789") i++
                correctIndex = json[i] - '0' - 1 // делаем 0-based
            }

            if (options.isNotEmpty()) {
                questions.add(Question(qText.toString(), options, correctIndex))
            }
        } else {
            i++
        }
    }

    return questions
}

// Вспомогательная: извлекает строку в кавычках
fun parseStringValue(json: String, start: Int): Pair<String, Int> {
    var i = start
    val result = StringBuilder()
    while (i < json.length) {
        when (json[i]) {
            '\\' -> {
                if (i + 1 < json.length) {
                    when (json[i + 1]) {
                        'n' -> result.append('\n')
                        't' -> result.append('\t')
                        else -> result.append(json[i + 1])
                    }
                    i += 2
                } else break
            }
            '"' -> return result.toString() to i + 1
            else -> {
                result.append(json[i])
                i++
            }
        }
    }
    return result.toString() to i
}

// Вспомогательная: пропускает до нужного символа
fun skipTo(json: String, start: Int, char: Char): Int {
    var i = start
    while (i < json.length && json[i] != char) i++
    return i
}