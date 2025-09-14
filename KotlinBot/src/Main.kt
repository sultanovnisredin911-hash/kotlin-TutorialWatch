import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val botName = "KotlinBot"
    var userName: String? = null
    val jokes = listOf(
        "Почему программисты не ходят в лес? Страшно: вдруг nullPointerException!",
        "— Ты чистил зубы? — Да. — А boolean?",
        "Если бы программисты были поварами, то в каждом супе была бы отладка.",
        "— Сколько программистов нужно, чтобы поменять лампочку? — Ни одного: это аппаратная проблема!"
    )

    println("Привет! Я — $botName, твой виртуальный собеседник. 🤖")
    println("Напиши 'пока', чтобы закончить разговор.")

    while (true) {
        print("Ты: ")
        val input = readLine()?.trim()?.lowercase() ?: continue

        if (userName == null) {
            println("$botName: Привет! Как тебя зовут?")
            print("Ты: ")
            userName = readLine()?.trim()
            if (userName.isNullOrBlank()) {
                userName = "Друг"
            }
            println("$botName: Приятно познакомиться, $userName! Задавай любой вопрос.")
            continue
        }

        when {
            input.contains("привет") || input.contains("здравствуй") -> {
                println("$botName: Привет, $userName! Как дела?")
            }
            input.contains("дела") || input.contains("настроение") -> {
                println("$botName: У меня всё отлично! Я на Kotlin работаю — и всё быстро!")
            }
            input.contains("имя") -> {
                println("$botName: Меня зовут $botName. А ты уже представился — $userName. 😉")
            }
            input.contains("анекдот") || input.contains("шутка") -> {
                val joke = jokes.random()
                println("$botName: $joke")
            }
            input.contains("время") || input.contains("часы") -> {
                val time = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                println("$botName: Сейчас $time.format(formatter)")
            }
            input.contains("дата") -> {
                val date = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                println("$botName: Сегодня $date.format(formatter)")
            }
            input.contains("помощь") || input.contains("что ты умеешь") -> {
                println("$botName: Я умею:")
                println("  • Приветствовать")
                println("  • Рассказывать анекдоты")
                println("  • Показывать время и дату")
                println("  • Запоминать твоё имя")
                println("  • Отвечать на простые вопросы")
                println("  • И просто болтать! 😊")
            }
            input == "пока" || input == "выход" -> {
                println("$botName: Пока, $userName! Возвращайся, буду ждать! 👋")
                break
            }
            input.contains("спасибо") -> {
                println("$botName: Пожалуйста, $userName! Всегда рад помочь!")
            }
            input.contains("как") && input.contains("дела") -> {
                println("$botName: У меня всё отлично! Спасибо, что спросил! ❤️")
            }
            else -> {
                val responses = listOf(
                    "Интересно! Расскажи подробнее.",
                    "Я пока не понял, но запомню!",
                    "Ага, понятно. А что дальше?",
                    "Это очень важно. Я подумаю над этим.",
                    "Хм... А ты пробовал перезагрузить мир?"
                )
                println("$botName: ${responses.random()}")
            }
        }
    }
}