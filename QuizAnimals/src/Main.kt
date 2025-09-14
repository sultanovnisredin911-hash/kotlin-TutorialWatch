import kotlin.random.Random

fun main() {
    println("=== 🐰 Викторина: Угадай животное! ===")
    println("Попробуй угадать животное по описанию и эмодзи.")
    println("Для выхода — введи 'выход'\n")

    // База данных: список пар (животное, (эмодзи, подсказка))
    val animals = listOf(
        "слон" to ("🐘" to "Он огромный, у него длинный нос и большие уши."),
        "жираф" to ("🦒" to "Самое высокое животное в мире с пятнистой шеей."),
        "пингвин" to ("🐧" to "Он не умеет летать, но отлично плавает в холодных водах."),
        "зебра" to ("🦓" to "Чёрно-белая полосатая лошадка из Африки."),
        "крокодил" to ("🐊" to "Живёт в реках, у него сильные челюсти и кожа как броня."),
        "обезьяна" to ("🐒" to "Любит бананы, прыгает по деревьям и очень умная."),
        "медведь" to ("🐻" to "Большой и сильный, зимой спит в берлоге."),
        "лиса" to ("🦊" to "Хитрая, с пушистым хвостом и острыми ушами."),
        "кенгуру" to ("🦘" to "Прыгает по просторам Австралии с малышом в сумке."),
        "носорог" to ("🦏" to "У него рог на носу и очень толстая кожа.")
    )

    var score = 0
    val totalQuestions = 5

    repeat(totalQuestions) { questionNumber ->
        // Выбираем случайное животное
        val (animal, emojiAndHint) = animals.random()
        val emoji = emojiAndHint.first
        val hint = emojiAndHint.second

        println("Вопрос ${questionNumber + 1}:")
        println("  $emoji")
        println("  Подсказка: $hint")
        print("  Кто это? → ")

        val input = readLine()
        if (input == null || input.trim() == "") {
            println("❌ Ничего не введено. Пропускаем.\n")
        }
        val answer = input?.trim()?.lowercase()

        if (answer == "выход") {
            println("Спасибо за игру! 🏁 Счёт: $score из $questionNumber")
            return
        }

        if (answer == animal) {
            println("✅ Правильно! Это $emoji $animal! 🎉\n")
            score++
        } else {
            println("❌ Неверно. Это $emoji $animal.\n")
        }
    }

    println("🏁 Викторина завершена!")
    println("Твой результат: $score из $totalQuestions")

    when (score) {
        5 -> println("👑 Ты — гений зоологии!")
        4 -> println("👏 Отлично! Ты отлично знаешь животных!")
        3 -> println("👍 Хорошо! Продолжай учиться!")
        2 -> println("📚 Неплохо, но можно лучше!")
        else -> println("🌍 Животные — удивительны! Попробуй ещё раз!")
    }
}