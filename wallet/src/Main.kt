import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val RED = "\u001b[31m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val BLUE = "\u001b[34m"

// Файл для хранения транзакций
private const val TRANSACTIONS_FILE = "transactions.txt"

// Формат даты и времени
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

data class Transaction(
    val amount: Double,
    val type: String, // "Доход" или "Расход"
    val category: String,
    val date: LocalDateTime
)

fun main() {
    val transactions = loadTransactions()
    println("$CYAN=== 💰 КОШЕЛЁК ===$RESET")
    println("Следите за своими финансами!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addIncome(transactions)
            "2" -> addExpense(transactions)
            "3" -> viewAll(transactions)
            "4" -> showBalance(transactions)
            "5" -> showStats(transactions)
            "6" -> {
                println("До свидания! 👋")
                break
            }
            else -> println("$RED❌ Выберите 1–6$RESET")
        }
        saveTransactions(transactions)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить доход")
    println("2. Добавить расход")
    println("3. Все транзакции")
    println("4. Баланс")
    println("5. Статистика")
    println("6. Выход")
}

fun addIncome(transactions: MutableList<Transaction>) {
    print("Сумма дохода: ")
    val input = readLine()?.trim() ?: return
    val amount = input.toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("$RED❌ Введите положительное число.$RESET")
        return
    }

    print("Категория (например: Зарплата, Подарок): ")
    val category = readLine()?.trim() ?: "Разное"

    transactions.add(Transaction(amount, "Доход", category, LocalDateTime.now()))
    println("$GREEN✅ Доход добавлен: $amount ₽$RESET")
}

fun addExpense(transactions: MutableList<Transaction>) {
    print("Сумма расхода: ")
    val input = readLine()?.trim() ?: return
    val amount = input.toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("$RED❌ Введите положительное число.$RESET")
        return
    }

    print("Категория (например: Еда, Транспорт): ")
    val category = readLine()?.trim() ?: "Разное"

    transactions.add(Transaction(amount, "Расход", category, LocalDateTime.now()))
    println("$RED✅ Расход добавлен: $amount ₽$RESET")
}

fun viewAll(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        println("$YELLOW📌 Нет транзакций.$RESET")
        return
    }
    println("\n$CYAN=== ВСЕ ТРАНЗАКЦИИ ===$RESET")
    for (i in transactions.indices) {
        val t = transactions[i]
        val color = if (t.type == "Доход") GREEN else RED
        val sign = if (t.type == "Доход") "+" else "-"
        println("${i + 1}. $color$sign${t.amount} ₽$RESET | ${t.category} | ${t.date.format(formatter)}")
    }
}

fun showBalance(transactions: List<Transaction>) {
    val income = transactions.filter { it.type == "Доход" }.sumOf { it.amount }
    val expense = transactions.filter { it.type == "Расход" }.sumOf { it.amount }
    val balance = income - expense
    val color = if (balance >= 0) GREEN else RED

    println("\n$YELLOW=== БАЛАНС ===$RESET")
    println("$GREEN Доход: $income ₽$RESET")
    println("$RED Расход: $expense ₽$RESET")
    println("$color Баланс: $balance ₽$RESET")
}

fun showStats(transactions: List<Transaction>) {
    val expenses = transactions.filter { it.type == "Расход" }
    if (expenses.isEmpty()) {
        println("$YELLOW📌 Нет расходов.$RESET")
        return
    }

    val byCategory = expenses.groupingBy { it.category }.fold(0.0) { acc, t -> acc + t.amount }

    println("\n$BLUE=== СТАТИСТИКА РАСХОДОВ ===$RESET")
    byCategory.toList()
        .sortedByDescending { it.second }
        .forEach { (category, total) ->
            println("🔹 $category: $total ₽")
        }
}

// Загрузка транзакций из файла
fun loadTransactions(): MutableList<Transaction> {
    val file = File(TRANSACTIONS_FILE)
    if (!file.exists()) return mutableListOf()

    return file.useLines { lines ->
        lines.mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size == 4) {
                val amount = parts[0].toDoubleOrNull() ?: return@mapNotNull null
                val type = parts[1]
                val category = parts[2]
                val date = runCatching { LocalDateTime.parse(parts[3], formatter) }.getOrNull()
                    ?: return@mapNotNull null
                Transaction(amount, type, category, date)
            } else null
        }.toMutableList()
    }
}

// Сохранение транзакций в файл
fun saveTransactions(transactions: List<Transaction>) {
    try {
        File(TRANSACTIONS_FILE).bufferedWriter().use { writer ->
            for (t in transactions) {
                writer.write("${t.amount}|${t.type}|${t.category}|${t.date.format(formatter)}\n")
            }
        }
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}