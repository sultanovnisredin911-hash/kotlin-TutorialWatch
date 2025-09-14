import kotlin.random.Random

fun main() {
    println("===  Викторина: Угадай страну по флагу ===")
    println("Попробуй угадать страну по её флагу из эмодзи!")
    println("Для выхода — введи 'выход'\n")


    val flags = mapOf(
        "Россия" to "⬜️🟥🟥",
        "Франция" to "🟦⬜️🟥",
        "Германия" to "⬛️🟥🟨",
        "Италия" to "🟩⬜️🟥",
        "Япония" to "🟥⚪️",
        "Канада" to "🟥⬜️",
        "Бразилия" to "🟩🟨🟦",
        "Мексика" to "🟩⬜️🟥",
        "Испания" to "🟥🟨🟥",
        "Польша" to "⬜️🟥",
        "Украина" to "🟦🟨",
        "Нидерланды" to "🟥⬜️🟦",
        "Бельгия" to "⬛️🟨🟥",
        "Австрия" to "🟥⬜️🟥",
        "Румыния" to "🟦🟨🟥"
    )

    var score = 0
    val totalQuestions = 5

    repeat(totalQuestions) { questionNumber ->
        val (country, flag) = flags.toList().random()

        println("Вопрос ${questionNumber + 1}:")
        println("Какая страна скрывается под этим флагом?")
        println("  $flag")
        print("Твой ответ: ")

        val answer = readLine()?.trim()?.lowercase()

        if (answer == "выход") {
            println("Спасибо за игру! 🏁 Счёт: $score из $questionNumber")
            return
        }

        if (answer == country.lowercase()) {
            println("\uD83D\uDC4D\uD83C\uDFFD Правильно! Это $country $flag \n")
            score++
        } else {
            println("❌ Неверно. Это $country $flag\n")
        }
    }

    println("🏁 Викторина завершена!")
    println("Твой результат: $score из $totalQuestions")

    when (score) {
        5 -> println(" Идеально! Ты — мастер флагов!")
        4 -> println("\uD83D\uDC4D Отлично! Почти идеально!")
        3 -> println(" Хорошо! Продолжай в том же духе!")
        2 -> println(" Неплохо, но можно лучше!")
        in 0..1 -> println(" Флаги — это интересно! Попробуй ещё раз!")
    }
}