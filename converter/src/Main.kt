import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"

// Файл для истории
private const val HISTORY_FILE = "conversion_history.txt"
private const val MAX_HISTORY = 10

fun main() {
    println("$CYAN=== 🔄 КОНВЕРТЕР ЕДИНИЦ ===$RESET")
    println("Конвертируйте длину, температуру, вес и валюту!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> convertLength()
            "2" -> convertTemperature()
            "3" -> convertWeight()
            "4" -> convertCurrency()
            "5" -> showHistory()
            "6" -> {
                println("До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–6$RESET")
        }
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Длина (м, км, см, дюймы, футы)")
    println("2. Температура (°C, °F, K)")
    println("3. Вес (кг, г, фунты, унции)")
    println("4. Валюта (RUB, USD, EUR)")
    println("5. История конвертаций")
    println("6. Выход")
}

// === ДЛИНА ===
fun convertLength() {
    val units = mapOf(
        "1" to "метры",
        "2" to "километры",
        "3" to "сантиметры",
        "4" to "дюймы",
        "5" to "футы"
    )

    val factors = mapOf<String, Double>(
        "метры" to 1.0,
        "километры" to 1000.0,
        "сантиметры" to 0.01,
        "дюймы" to 0.0254,
        "футы" to 0.3048
    )

    printValueAndUnit("длину", units) { value, from, to ->
        val meters = value * factors[from]!!
        val result = meters / factors[to]!!
        "✅ $value $from = ${"%.4f".format(result)} $to"
    }
}

// === ТЕМПЕРАТУРА ===
fun convertTemperature() {
    val units = mapOf(
        "1" to "Цельсий (°C)",
        "2" to "Фаренгейт (°F)",
        "3" to "Кельвин (K)"
    )

    print("Значение: ")
    val value = readLine()?.toDoubleOrNull()
    if (value == null) {
        println("$RED❌ Неверное число.$RESET")
        return
    }

    println("Из:")
    units.forEach { (k, v) -> println("$k. $v") }
    val fromKey = readLine()?.trim()
    val from = units[fromKey]
    if (from == null) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }

    println("В:")
    units.forEach { (k, v) -> println("$k. $v") }
    val toKey = readLine()?.trim()
    val to = units[toKey]
    if (to == null) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }

    val result = when {
        from == to -> value
        from == "Цельсий (°C)" && to == "Фаренгейт (°F)" -> value * 9/5 + 32
        from == "Цельсий (°C)" && to == "Кельвин (K)" -> value + 273.15
        from == "Фаренгейт (°F)" && to == "Цельсий (°C)" -> (value - 32) * 5/9
        from == "Фаренгейт (°F)" && to == "Кельвин (K)" -> (value - 32) * 5/9 + 273.15
        from == "Кельвин (K)" && to == "Цельсий (°C)" -> value - 273.15
        from == "Кельвин (K)" && to == "Фаренгейт (°F)" -> (value - 273.15) * 9/5 + 32
        else -> value
    }

    val message = "✅ $value $from = ${"%.4f".format(result)} $to"
    println("$GREEN$message$RESET")
    addToHistory(message)
}

// === ВЕС ===
fun convertWeight() {
    val units = mapOf(
        "1" to "килограммы",
        "2" to "граммы",
        "3" to "фунты",
        "4" to "унции"
    )

    val factors = mapOf<String, Double>(
        "килограммы" to 1.0,
        "граммы" to 0.001,
        "фунты" to 0.453592,
        "унции" to 0.0283495
    )

    printValueAndUnit("вес", units) { value, from, to ->
        val kg = value * factors[from]!!
        val result = kg / factors[to]!!
        "✅ $value $from = ${"%.4f".format(result)} $to"
    }
}

// === ВАЛЮТА ===
fun convertCurrency() {
    val units = mapOf(
        "1" to "RUB",
        "2" to "USD",
        "3" to "EUR"
    )

    // Фиксированные курсы (можно обновить)
    val rates = mapOf(
        "RUB" to 1.0,
        "USD" to 90.0,
        "EUR" to 100.0
    )

    printValueAndUnit("сумму", units) { value, from, to ->
        val rub = value * rates[from]!!
        val result = rub / rates[to]!!
        "✅ $value $from = ${"%.2f".format(result)} $to"
    }
}

// Универсальная функция для ввода значения и единиц
inline fun printValueAndUnit(
    type: String,
    units: Map<String, String>,
    crossinline convert: (Double, String, String) -> String
) {
    print("Введите $type: ")
    val value = readLine()?.toDoubleOrNull()
    if (value == null) {
        println("$RED❌ Неверное число.$RESET")
        return
    }

    println("Из:")
    units.forEach { (k, v) -> println("$k. $v") }
    val fromKey = readLine()?.trim()
    val from = units[fromKey]
    if (from == null) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }

    println("В:")
    units.forEach { (k, v) -> println("$k. $v") }
    val toKey = readLine()?.trim()
    val to = units[toKey]
    if (to == null) {
        println("$RED❌ Неверный выбор.$RESET")
        return
    }

    val message = convert(value, from, to)
    println("$GREEN$message$RESET")
    addToHistory(message)
}

// Добавить в историю
fun addToHistory(entry: String) {
    val lines = if (File(HISTORY_FILE).exists()) {
        File(HISTORY_FILE).readLines().toMutableList()
    } else mutableListOf()

    lines.add(0, entry) // в начало
    // Ограничиваем до 10 записей
    val toSave = lines.take(MAX_HISTORY)

    try {
        File(HISTORY_FILE).writeText(toSave.joinToString("\n"))
    } catch (e: Exception) {
        println("$RED❌ Не удалось сохранить историю: ${e.message}$RESET")
    }
}

// Просмотр истории
fun showHistory() {
    val file = File(HISTORY_FILE)
    if (!file.exists()) {
        println("$YELLOW📌 История пуста.$RESET")
        return
    }

    val lines = file.readLines()
    if (lines.isEmpty()) {
        println("$YELLOW📌 История пуста.$RESET")
        return
    }

    println("\n$BLUE=== ИСТОРИЯ КОНВЕРТАЦИЙ ===$RESET")
    lines.forEachIndexed { i, line ->
        println("${i + 1}. $line")
    }
}