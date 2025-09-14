import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"
const val STRIKE = "\u001b[9m" // зачёркнутый текст

// Файл для хранения
private const val BOOKS_FILE = "books.txt"

data class Book(
    val title: String,
    val author: String,
    val year: Int,
    val genre: String,
    var isRead: Boolean = false
)

fun main() {
    val books = loadBooks()
    println("$CYAN=== 📚 МОЯ БИБЛИОТЕКА ===$RESET")
    println("Управляйте своей коллекцией книг!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addBook(books)
            "2" -> viewAll(books)
            "3" -> searchBooks(books)
            "4" -> markAsRead(books)
            "5" -> filterByGenre(books)
            "6" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–6$RESET")
        }
        saveBooks(books)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить книгу")
    println("2. Все книги")
    println("3. Поиск")
    println("4. Отметить как прочитанную")
    println("5. Фильтр по жанру")
    println("6. Выход")
}

fun addBook(books: MutableList<Book>) {
    print("Название: ")
    val title = readLine()?.trim() ?: ""
    if (title.isEmpty()) {
        println("$RED❌ Название не может быть пустым.$RESET")
        return
    }

    print("Автор: ")
    val author = readLine()?.trim() ?: ""
    if (author.isEmpty()) {
        println("$RED❌ Автор обязателен.$RESET")
        return
    }

    print("Год (например, 2025): ")
    val year = readLine()?.toIntOrNull()
    if (year == null || year !in 1000..2100) {
        println("$RED❌ Введите корректный год.$RESET")
        return
    }

    print("Жанр (например: Фантастика): ")
    val genre = readLine()?.trim() ?: "Разное"

    books.add(Book(title, author, year, genre))
    println("$GREEN✅ Книга добавлена: '$title' — $author$RESET")
}

fun viewAll(books: List<Book>) {
    if (books.isEmpty()) {
        println("$YELLOW📌 Библиотека пуста.$RESET")
        return
    }
    println("\n$CYAN=== ВСЕ КНИГИ ===$RESET")
    for ((i, book) in books.withIndex()) {
        val status = if (book.isRead) "$GREEN✅ Прочитана$RESET" else "$RED❌ Не прочитана$RESET"
        val titleText = if (book.isRead) "$STRIKE${book.title}$RESET" else book.title
        println("${i + 1}. $titleText")
        println("   Автор: ${book.author}")
        println("   Год: ${book.year}")
        println("   Жанр: ${book.genre}")
        println("   Статус: $status")
        println()
    }
}

fun searchBooks(books: List<Book>) {
    if (books.isEmpty()) {
        println("$YELLOW📌 Библиотека пуста.$RESET")
        return
    }

    print("Поиск (по названию, автору или жанру): ")
    val query = readLine()?.trim()?.lowercase() ?: ""
    if (query.isEmpty()) return

    val results = books.filter {
        it.title.lowercase().contains(query) ||
                it.author.lowercase().contains(query) ||
                it.genre.lowercase().contains(query)
    }

    if (results.isEmpty()) {
        println("$RED❌ Ничего не найдено.$RESET")
    } else {
        println("\n$GREEN=== НАЙДЕНО (${results.size}) ===$RESET")
        for (book in results) {
            val status = if (book.isRead) "✅" else "❌"
            println("🔹 ${book.title} — ${book.author} ($status)")
        }
    }
}

fun markAsRead(books: List<Book>) {
    viewAll(books)
    if (books.isEmpty()) return

    print("Номер книги для отметки: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in books.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val book = books[index]
    book.isRead = true
    println("$GREEN🎉 Книга отмечена как прочитанная: '${book.title}'$RESET")
}

fun filterByGenre(books: List<Book>) {
    if (books.isEmpty()) {
        println("$YELLOW📌 Библиотека пуста.$RESET")
        return
    }

    val genres = books.map { it.genre }.toSet().sorted()
    println("\n$BLUE=== ДОСТУПНЫЕ ЖАНРЫ ===$RESET")
    genres.forEachIndexed { i, genre -> println("${i + 1}. $genre") }

    print("Выберите жанр (номер): ")
    val genreIndex = readLine()?.toIntOrNull()?.minus(1)
    if (genreIndex == null || genreIndex !in genres.indices) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }

    val selectedGenre = genres[genreIndex]
    val filtered = books.filter { it.genre == selectedGenre }

    println("\n$CYAN=== КНИГИ В ЖАНРЕ '$selectedGenre' ===$RESET")
    for (book in filtered) {
        val status = if (book.isRead) "✅" else "❌"
        println("🔹 ${book.title} — ${book.author} ($status)")
    }
}

// Загрузка книг из файла
fun loadBooks(): MutableList<Book> {
    val file = File(BOOKS_FILE)
    if (!file.exists()) return mutableListOf()

    return file.useLines { lines ->
        lines.mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size == 5) {
                val title = parts[0]
                val author = parts[1]
                val year = parts[2].toIntOrNull() ?: return@mapNotNull null
                val genre = parts[3]
                val isRead = parts[4].toBooleanStrictOrNull() ?: false
                Book(title, author, year, genre, isRead)
            } else null
        }.toMutableList()
    }
}

// Сохранение книг в файл
fun saveBooks(books: List<Book>) {
    try {
        File(BOOKS_FILE).bufferedWriter().use { writer ->
            for (book in books) {
                writer.write("${book.title}|${book.author}|${book.year}|${book.genre}|${book.isRead}\n")
            }
        }
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}