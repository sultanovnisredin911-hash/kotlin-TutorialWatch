import java.io.File

// Цвета
const val RESET = "\u001b[0m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"
const val MAGENTA = "\u001b[35m"

// Файл для хранения
private const val STUDENTS_FILE = "students.txt"

data class Student(
    var name: String,
    var age: Int,
    var group: String
)

fun main() {
    val students = loadStudents()
    println("$MAGENTA=== 🎓 СИСТЕМА УПРАВЛЕНИЯ СТУДЕНТАМИ ===$RESET")
    println("Управляйте данными студентов быстро и удобно!")

    while (true) {
        printMenu()
        when (readLine()?.trim()) {
            "1" -> addStudent(students)
            "2" -> viewAll(students)
            "3" -> editStudent(students)
            "4" -> deleteStudent(students)
            "5" -> searchStudent(students)
            "6" -> showStats(students)
            "7" -> {
                println("Завершение работы. До свидания! 👋")
                return
            }
            else -> println("$RED❌ Выберите 1–7$RESET")
        }
        saveStudents(students)
    }
}

fun printMenu() {
    println("\n$YELLOW--- МЕНЮ ---$RESET")
    println("1. Добавить студента")
    println("2. Все студенты")
    println("3. Редактировать")
    println("4. Удалить")
    println("5. Поиск по имени")
    println("6. Статистика")
    println("7. Выход")
}

fun addStudent(students: MutableList<Student>) {
    print("Имя: ")
    val name = readLine()?.trim() ?: ""
    if (name.isEmpty()) {
        println("$RED❌ Имя не может быть пустым.$RESET")
        return
    }

    print("Возраст: ")
    val age = readLine()?.toIntOrNull()
    if (age == null || age !in 14..100) {
        println("$RED❌ Введите возраст от 14 до 100.$RESET")
        return
    }

    print("Группа (например: ИТ-11): ")
    val group = readLine()?.trim() ?: ""
    if (group.isEmpty()) {
        println("$RED❌ Группа обязательна.$RESET")
        return
    }

    students.add(Student(name, age, group))
    println("$GREEN✅ Студент '$name' добавлен в группу $group!$RESET")
}

fun viewAll(students: List<Student>) {
    if (students.isEmpty()) {
        println("$YELLOW📌 Нет студентов.$RESET")
        return
    }
    println("\n$CYAN=== СПИСОК СТУДЕНТОВ ===$RESET")
    for ((i, s) in students.withIndex()) {
        println("${i + 1}. $s.name, $s.age лет, группа: $s.group")
    }
}

fun editStudent(students: MutableList<Student>) {
    viewAll(students)
    if (students.isEmpty()) return

    print("Номер студента для редактирования: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in students.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val student = students[index]
    println("Редактирование: ${student.name}")

    print("Новое имя (оставьте пустым, чтобы не менять): ")
    val name = readLine()?.trim()
    if (!name.isNullOrBlank()) student.name = name

    print("Новый возраст (оставьте пустым): ")
    val ageInput = readLine()?.trim()
    if (ageInput?.isNotBlank() == true) {
        val age = ageInput.toIntOrNull()
        if (age in 14..100) {
            if (age != null) {
                student.age = age
            }
        } else {
            println("$YELLOW Возраст оставлен без изменений.$RESET")
        }
    }

    print("Новая группа (оставьте пустым): ")
    val group = readLine()?.trim()
    if (!group.isNullOrBlank()) student.group = group

    println("$GREEN✅ Данные обновлены!$RESET")
}

fun deleteStudent(students: MutableList<Student>) {
    viewAll(students)
    if (students.isEmpty()) return

    print("Номер для удаления: ")
    val index = readLine()?.toIntOrNull()?.minus(1)
    if (index == null || index !in students.indices) {
        println("$RED❌ Неверный номер.$RESET")
        return
    }

    val removed = students.removeAt(index)
    println("$YELLOW🗑 Студент '${removed.name}' удалён.$RESET")
}

fun searchStudent(students: List<Student>) {
    if (students.isEmpty()) {
        println("$YELLOW📌 Список пуст.$RESET")
        return
    }

    print("Поиск по имени: ")
    val query = readLine()?.trim()?.lowercase() ?: ""
    if (query.isEmpty()) return

    val results = students.filter { it.name.lowercase().contains(query) }

    if (results.isEmpty()) {
        println("$RED❌ Ничего не найдено.$RESET")
    } else {
        println("\n$GREEN=== НАЙДЕНО (${results.size}) ===$RESET")
        for (s in results) {
            println("🔹 ${s.name}, ${s.age} лет, группа: ${s.group}")
        }
    }
}

fun showStats(students: List<Student>) {
    if (students.isEmpty()) {
        println("$YELLOW📌 Нет данных.$RESET")
        return
    }

    val byGroup = students.groupingBy { it.group }.eachCount()

    println("\n$BLUE=== СТАТИСТИКА ПО ГРУППАМ ===$RESET")
    var total = 0
    for ((group, count) in byGroup) {
        println("🎓 $group: $count студент(ов)")
        total += count
    }
    println("$MAGENTA Всего студентов: $total$RESET")
}

// Загрузка из файла
fun loadStudents(): MutableList<Student> {
    val file = File(STUDENTS_FILE)
    if (!file.exists()) return mutableListOf()

    return file.useLines { lines ->
        lines.mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size == 3) {
                val name = parts[0]
                val age = parts[1].toIntOrNull() ?: return@mapNotNull null
                val group = parts[2]
                Student(name, age, group)
            } else null
        }.toMutableList()
    }
}

// Сохранение в файл
fun saveStudents(students: List<Student>) {
    try {
        File(STUDENTS_FILE).bufferedWriter().use { writer ->
            for (s in students) {
                writer.write("${s.name}|${s.age}|${s.group}\n")
            }
        }
    } catch (e: Exception) {
        println("$RED❌ Ошибка сохранения: ${e.message}$RESET")
    }
}