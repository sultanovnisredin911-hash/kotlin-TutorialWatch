import kotlin.random.Random
fun main(){
 var totalWins=0
 var bestScore=Int.MAX_VALUE
    println("=== Угадай число ===")

    while (true) {
        println("Выберите сложность:")
        println("1.Лёгкий(1-50)-10 попыток")
        println("2. Средний(1-100) - 7 попыток")
        println("3. Сложный(1-200 - 8 попыток")
        println("4. Выход")

        print("Ваш выбор: ")
        val choice =readLine()?.trim()

        val(min,max,maxAttempts)=when(choice){
            "1"-> Triple(1,50,10)
            "2"-> Triple(1,100,7)
            "3"-> Triple(1,200,8)
            "4"->{
                println("Спасибо за игру! Выиграно:$totalWins | Рекорд:${if (bestScore == Int.MAX_VALUE) 0 else bestScore}")
                return
            }
            else -> {
                println("Выберите 1,2,3 или 4")
                continue
            }
        }
      var secretNumber = Random.nextInt(min,max + 1)
        var attempts = 0
        val guesses = mutableSetOf<Int>()
        var hintGiven = false

        println("\n Я загадал число от $min до $max.")
        println("У вас есть $maxAttempts попыток!")

        while (attempts < maxAttempts) {
            val remaining = maxAttempts - attempts
            print("Попытка $attempts/$maxAttempts | Ваше число:  ")
            val input = readLine()?.trim()

            if (input?.lowercase()in listOf("выход","в","exit")){
                println("Загаданное число:$secretNumber. До новых встреч!")
                break
            }
            val guess = input?.toIntOrNull()
            if (guess == null ){
                println("Введите число или 'выход' ")
                continue

    }
       if (guess !in min..max){
           println("число должно быть от $min до $max " )
           continue
       }

            if (guess in guesses) {
                println(" Ты уже пробовал $guess! Подумай лучше…")
                continue
            }

            attempts++
            guesses.add(guess)

            // Подсказка, если игрок долго не угадывает
            if (!hintGiven && attempts >= maxAttempts / 2) {
                val near = if (guess < secretNumber) "немного больше" else "немного меньше"
                println(" Подсказка: попробуй $near ")
                hintGiven = true
            }

            when {
                guess < secretNumber -> {
                    println(" Больше! Осталось: $remaining")
                }
                guess > secretNumber -> {
                    println(" Меньше! Осталось: $remaining")
                }
                else -> {
                    val score = maxAttempts - attempts + 1
                    println(" УГАДАЛ! Число: $secretNumber")
                    println("Потрачено попыток: $attempts из $maxAttempts")

                    totalWins++
                    if (attempts < bestScore) {
                        bestScore = attempts
                        println(" НОВЫЙ РЕКОРД!")
                    }

                    // Оценка по звёздочкам
                    val stars = "=)".repeat(score.coerceAtMost(5))
                    println("=( Оценка: $stars ($score из $maxAttempts)")

                    break
                }
            }
        }

        // Если проиграл
        if (attempts >= maxAttempts && !guesses.contains(secretNumber)) {
            println(" ВЫ ПРОИГРАЛИ! Загаданное число: $secretNumber")
            println(" Ты пробовал: ${guesses.sorted()}")
        }

        // Статистика
        println("\n Статистика: побед — $totalWins | лучший результат — $bestScore")
    }
}