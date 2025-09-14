import kotlin.random.Random
import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val BLUE = "\u001b[34m"
const val MAGENTA = "\u001b[35m"

// Структура: слово и категория
data class WordWithHint(val word: String, val hint: String)

fun main() {
    // Загружаем слова из файла
    val wordsFile = File("words.txt")
    if (!wordsFile.exists()) {
        println("$RED Файл words.txt не найден!$RESET")
        return
    }

    val allWords = wordsFile.readLines()
        .filter { it.contains(",") }
        .map {
            val (word, hint) = it.split(",", limit = 2)
            WordWithHint(word.trim().uppercase(), hint.trim())
        }

    if (allWords.isEmpty()) {
        println("$RED Нет слов в файле!$RESET")
        return
    }

    var wins = 0
    var losses = 0
    val guessedWords = mutableSetOf<String>() // чтобы не повторять

    println("$CYAN=== 🪢 Виселица PRO ===$RESET")
    println("Угадайте слово по буквам. У вас 6 попыток.")

    while (true) {
        // Фильтруем слова по уровню
        val availableWords = allWords.filter { it.word !in guessedWords }
        if (availableWords.isEmpty()) {
            println("$YELLOW Вы угадали все слова!$RESET")
            break
        }

        val difficulty = chooseDifficulty()
        val filteredWords = when (difficulty) {
            "лёгкий" -> availableWords.filter { it.word.length <= 4 }
            "средний" -> availableWords.filter { it.word.length in 5..7 }
            "сложный" -> availableWords.filter { it.word.length > 7 }
            else -> availableWords
        }.ifEmpty { availableWords }

        val wordData = filteredWords.random()
        val word = wordData.word
        val hint = wordData.hint

        // Играем
        val won = playGame(word, hint)
        if (won) {
            wins++
            guessedWords.add(word)
        } else {
            losses++
        }

        // Статистика
        println("$CYAN Счёт: $GREEN$wins$CYAN побед | $RED$losses$CYAN поражений$RESET")

        println("\n$CYAN Продолжим? (да/нет)$RESET")
        val again = readLine()?.trim()?.lowercase()
        if (again !in listOf("да", "yes", "д", "y")) break
    }

    println("$GREEN Спасибо за игру!$RESET")
    println("Итого: $wins побед, $losses поражений. Угадано слов: ${guessedWords.size}")
}

// Выбор уровня сложности
fun chooseDifficulty(): String {
    while (true) {
        println("\n$CYAN Выберите уровень:$RESET")
        println("1. $GREEN Лёгкий$RESET (до 4 букв)")
        println("2. $YELLOW Средний$RESET (5–7 букв)")
        println("3. $RED Сложный$RESET (8+ букв)")
        print("$CYAN Ваш выбор: $RESET")

        return when (readLine()?.trim()) {
            "1" -> "лёгкий"
            "2" -> "средний"
            "3" -> "сложный"
            else -> {
                println("$RED Выберите 1, 2 или 3$RESET")
                continue
            }
        }
    }
}

// Сама игра
fun playGame(word: String, hint: String): Boolean {
    val guessedLetters = mutableSetOf<Char>()
    var mistakes = 0
    val maxMistakes = 6
    var usedHint = false

    while (mistakes < maxMistakes) {
        val displayWord = buildDisplayWord(word, guessedLetters)
        if (displayWord.replace(" ", "") == word) {
            println("$GREEN Поздравляю! Вы угадали слово: $word!$RESET")
            return true
        }

        printBoard(mistakes)
        println("$BLUE Слово: $displayWord$RESET")
        println("$YELLOW Угаданные буквы: ${guessedLetters.sorted().joinToString(" ")}$RESET")
        println("$CYAN Ошибки: $mistakes/$maxMistakes$RESET")

        // Подсказка
        if (!usedHint && mistakes >= 3 && readYesNo("Хотите подсказку?")) {
            println("$MAGENTA Подсказка: это $hint.$RESET")
            usedHint = true
        }

        print("$CYAN Введите букву: $RESET")
        val input = readLine()?.trim()?.uppercase()

        if (input?.lowercase() in listOf("выход", "exit")) {
            println("$YELLOW Игра завершена.$RESET")
            return false
        }

        if (input == null || input.length != 1 || !input[0].isLetter()) {
            println("$RED Введите одну букву.$RESET")
            continue
        }

        val letter = input[0]

        if (letter in guessedLetters) {
            println("$YELLOW о" +
                    "е" +
                    "Вы уже угадывали '$letter'!$RESET")
            continue
        }

        guessedLetters.add(letter)

        if (letter in word) {
            println("$GREEN Правильно!$RESET")
        } else {
            mistakes++
            println("$RED Нет такой буквы.$RESET")
        }

        Thread.sleep(600)
    }

    printBoard(mistakes)
    println("$RED Вы проиграли! Загаданное слово: $word$RESET")
    return false
}

// Показывает слово с подчёркиваниями
fun buildDisplayWord(word: String, guessed: Set<Char>): String {
    return word.map { if (it in guessed) it else '_' }.joinToString(" ")
}

// Рисует виселицу
fun printBoard(mistakes: Int) {
    val parts = listOf(
        "   +---+  ",
        "   |   |  ",
        when (mistakes) {
            0 -> "       |  "
            1 -> "   O   |  "
            2 -> "   O   |  \n   |   |  "
            3 -> "   O   |  \n  /|   |  "
            4 -> "   O   |  \n  /|\\  |  "
            5 -> "   O   |  \n  /|\\  |  \n  /    |  "
            6 -> "   O   |  \n  /|\\  |  \n  / \\  |  "
            else -> ""
        },
        "       |  ",
        "========="
    )

    println()
    for (part in parts) {
        if (part.isNotBlank()) println(part)
    }
    println()
}

// Универсальная функция: "да/нет"
fun readYesNo(prompt: String): Boolean {
    while (true) {
        print("$CYAN$prompt (да/нет): $RESET")
        val input = readLine()?.trim()?.lowercase()
        if (input in listOf("да", "yes", "д", "y")) return true
        if (input in listOf("нет", "no", "н", "n", "")) return false
        println("$RED Введите 'да' или 'нет'.$RESET")
    }
}