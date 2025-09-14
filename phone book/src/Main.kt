import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"

// Файл для хранения
val CONTACTS_FILE = "contacts.txt"

data class Contact(
    val name: String,
    val phone: String,
    val email: String = ""
)

fun main() {
    val contacts = loadContacts()
    println("$CYAN=== 📞 ТЕЛЕФОННАЯ КНИГА ===$RESET")
    println("Управляйте своими контактами!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addContact(contacts)
            "2" -> viewAll(contacts)
            "3" -> searchContact(contacts)
            "4" -> deleteContact(contacts)
            "5" -> {
                println("До свидания! 👋")
                break
            }
            else -> println("$RED❌ Выберите 1–5$RESET")
        }
        saveContacts(contacts)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить контакт")
    println("2. Все контакты")
    println("3. Поиск")
    println("4. Удалить контакт")
    println("5. Выход")
}

fun addContact(contacts: MutableList<Contact>) {
    print("Имя: ")
    val name = readLine()?.trim() ?: ""
    if (name.isEmpty()) {
        println("$RED❌ Имя не может быть пустым.$RESET")
        return
    }

    print("Телефон: ")
    val phone = readLine()?.trim() ?: ""
    if (phone.isEmpty()) {
        println("$RED❌ Телефон обязателен.$RESET")
        return
    }

    print("Email (опционально): ")
    val email = readLine()?.trim() ?: ""

    contacts.add(Contact(name, phone, email))
    println("$GREEN✅ Контакт добавлен: $name$RESET")
}

fun viewAll(contacts: List<Contact>) {
    if (contacts.isEmpty()) {
        println("$YELLOW📌 Нет контактов.$RESET")
        return
    }
    println("\n$CYAN=== ВСЕ КОНТАКТЫ ===$RESET")
    for (i in contacts.indices) {
        val c = contacts[i]
        println("${i + 1}. $c.name")
        println("   📱 $c.phone")
        if (c.email.isNotBlank()) println("   ✉️  $c.email")
    }
}

fun searchContact(contacts: List<Contact>) {
    if (contacts.isEmpty()) {
        println("$YELLOW📌 Нет контактов для поиска.$RESET")
        return
    }

    print("Поиск по имени или телефону: ")
    val query = readLine()?.trim()?.lowercase() ?: ""
    if (query.isEmpty()) {
        println("$YELLOW🔍 Введите запрос.$RESET")
        return
    }

    val results = contacts.filter {
        it.name.lowercase().contains(query) ||
                it.phone.contains(query)
    }

    if (results.isEmpty()) {
        println("$RED❌ Ничего не найдено.$RESET")
    } else {
        println("\n$GREEN=== НАЙДЕНО (${results.size}) ===$RESET")
        for (c in results) {
            println("🔹 ${c.name}")
            println("   📱 ${c.phone}")
            if (c.email.isNotBlank()) println("   ✉️  ${c.email}")
        }
    }
}

fun deleteContact(contacts: MutableList<Contact>) {
    viewAll(contacts)
    if (contacts.isEmpty()) return

    print("Номер для удаления: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in contacts.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }
    val removed = contacts.removeAt(index)
    println("$YELLOW🗑 Удалён: ${removed.name}$RESET")
}

// Загрузка из файла
fun loadContacts(): MutableList<Contact> {
    val file = File(CONTACTS_FILE)
    if (!file.exists()) return mutableListOf()

    return file.readLines().mapNotNull { line ->
        val parts = line.split("|")
        if (parts.size >= 2) {
            Contact(parts[0], parts[1], parts.getOrNull(2) ?: "")
        } else null
    }.toMutableList()
}

// Сохранение в файл
fun saveContacts(contacts: List<Contact>) {
    try {
        File(CONTACTS_FILE).writeText(
            contacts.joinToString("\n") { "${it.name}|${it.phone}|${it.email}" }
        )
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}