import kotlin.random.Random

fun main() {
    // Словарь: английское слово → русское
 val dictionary = mutableMapOf(
        "hello" to "привет",
        "world" to "мир",
        "cat" to "кот",
        "dog" to "собака",
        "sun" to "солнце",
        "moon" to "луна",
        "book" to "книга",
        "friend" to "друг",
        "water" to "вода",
        "fire" to "огонь",
        "house" to "дом",
        "car" to "машина",
        "tree" to "дерево",
        "flower" to "цветок",
        "child" to "ребёнок",
        "mother" to "мама",
        "father" to "папа",
        "school" to "школа",
        "teacher" to "учитель",
        "student" to "студент",
        "city" to "город",
        "street" to "улица",
        "door" to "дверь",
        "window" to "окно",
        "table" to "стол",
        "chair" to "стул",
        "food" to "еда",
        "apple" to "яблоко",
        "milk" to "молоко",
        "bread" to "хлеб",
        "coffee" to "кофе",
        "time" to "время",
        "day" to "день",
        "night" to "ночь",
        "week" to "неделя",
        "year" to "год",
        "man" to "мужчина",
        "woman" to "женщина",
        "hand" to "рука",
        "eye" to "глаз",
        "ear" to "ухо",
        "nose" to "нос",
        "face" to "лицо",
        "love" to "любовь",
        "peace" to "мир",
        "war" to "война",
        "game" to "игра",
        "music" to "музыка",
        "song" to "песня",
        "rain" to "дождь",
        "snow" to "снег",
        "cold" to "холод",
        "hot" to "жарко"
    )

    println("=== 📚 Англо-русский словарь ===")

    while (true) {
        println("\nМеню:")
        println("1. Учить слова (викторина)")
        println("2. Добавить слово")
        println("3. Показать все слова")
        println("4. Выход")
        print("Выберите действие (1-4): ")

        when (readLine()?.trim()) {
            "1" -> startQuiz(dictionary)
            "2" -> addWord(dictionary)
            "3" -> showAllWords(dictionary)
            "4" -> {
                println("До новых встреч! 👋")
                return
            }
            else -> println("❌ Неверный выбор. Введите 1, 2, 3 или 4.")
        }
    }
}

// Режим викторины
fun startQuiz(dictionary: Map<String, String>) {
    if (dictionary.isEmpty()) {
        println("Словарь пуст. Сначала добавьте слова!")
        return
    }

    println("\n🎯 Режим: Учим слова! Отвечайте на перевод.")
    var correct = 0
    var total = 0

    // Случайные 5 слов (или меньше, если слов мало)
    val wordsToQuiz = dictionary.toList().shuffled().take(5)

    for ((english, russian) in wordsToQuiz) {
        println("\nКак переводится слово: '$english'?")
        print("Ваш ответ: ")
        val answer = readLine()?.trim()?.lowercase()

        if (answer == "выход") break

        total++
        if (answer == russian) {
            println("✅ Правильно!")
            correct++
        } else {
            println("❌ Неверно. Правильный перевод: '$russian'")
        }
    }

    if (total > 0) {
        println("\n🏁 Викторина завершена!")
        println("Результат: $correct из $total (${correct * 100 / total}%)")
    }
}

// Добавить слово
fun addWord(dictionary: MutableMap<String, String>) {
    print("Введите английское слово: ")
    val english = readLine()?.trim()?.lowercase() ?: return

    if (english.isEmpty()) {
        println("❌ Слово не может быть пустым.")
        return
    }

    if (dictionary.containsKey(english)) {
        println("⚠️ Слово '$english' уже есть: ${dictionary[english]}")
        print("Заменить? (да/нет): ")
        if (readLine()?.lowercase() != "да") return
    }

    print("Введите перевод на русский: ")
    val russian = readLine()?.trim() ?: return

    if (russian.isEmpty()) {
        println("❌ Перевод не может быть пустым.")
        return
    }

    dictionary[english] = russian
    println("✅ Слово '$english — $russian' добавлено!")
}

// Показать все слова
fun showAllWords(dictionary: Map<String, String>) {
    if (dictionary.isEmpty()) {
        println("Словарь пуст.")
        return
    }

    println("\n📖 Все слова в словаре:")
    for ((eng, rus) in dictionary) {
        println("  $eng — $rus")
    }
    println("Всего: ${dictionary.size} слов(а)")
}