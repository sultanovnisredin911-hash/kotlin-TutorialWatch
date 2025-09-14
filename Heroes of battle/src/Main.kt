import kotlin.random.Random

// Цвета
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val BLUE = "\u001b[34m"
const val YELLOW = "\u001b[33m"
const val MAGENTA = "\u001b[35m"

// Базовый класс героя
abstract class Hero(
    val name: String,
    var health: Int,
    val maxHealth: Int,
    val attack: Int
) {
    abstract fun specialAbility(enemy: Hero): String

    fun takeDamage(damage: Int) {
        health = maxOf(0, health - damage)
    }

    fun isAlive() = health > 0

    fun showStatus() {
        val bar = "█".repeat((health * 10 / maxHealth))
        val empty = "░".repeat(10 - bar.length)
        println("$name: $health/$maxHealth HP [$bar$empty]")
    }
}

// Воин
class Warrior(name: String) : Hero(name, 120, 120, 15) {
    private var rage = 0

    override fun specialAbility(enemy: Hero): String {
        if (rage >= 30) {
            val damage = attack * 2
            enemy.takeDamage(damage)
            rage = 0
            return "$RED💥 Яростная атака! Нанесено $damage урона!$RESET"
        }
        rage += 10
        return "🔥 Накопил ярость ($rage/30). Следующая способность будет сильной!"
    }
}

// Маг
class Mage(name: String) : Hero(name, 80, 80, 20) {
    private var mana = 100

    override fun specialAbility(enemy: Hero): String {
        if (mana >= 50) {
            val damage = 40
            enemy.takeDamage(damage)
            mana -= 50
            return "$BLUE⚡ Огненный шар! Нанесено $damage урона!$RESET"
        }
        mana = minOf(100, mana + 30)
        return "🌀 Восстановил ману ($mana/100)"
    }
}

// Лучник
class Archer(name: String) : Hero(name, 100, 100, 18) {
    private var focus = 0

    override fun specialAbility(enemy: Hero): String {
        if (focus >= 2) {
            val damage = attack * 3
            enemy.takeDamage(damage)
            focus = 0
            return "$GREEN🎯 Точный выстрел! Нанесено $damage урона!$RESET"
        }
        focus++
        return "🎯 Готовится к точному выстрелу... ($focus/2)"
    }
}

fun main() {
    println("$MAGENTA=== ⚔️ БИТВА ГЕРОЕВ ===$RESET")
    println("Создайте своего героя!")

    // Выбор игрока
    print("Введите имя героя: ")
    val playerName = readLine()?.trim() ?: "Игрок"

    println("Выберите класс:")
    println("1. $RED Воин$RESET (120 HP, сильный, ярость)")
    println("2. $BLUE Маг$RESET (80 HP, мощные способности)")
    println("3. $GREEN Лучник$RESET (100 HP, точные выстрелы)")

    val player = when (readLine()) {
        "1" -> Warrior(playerName)
        "2" -> Mage(playerName)
        "3" -> Archer(playerName)
        else -> {
            println("❌ Неизвестный выбор. Создан Воин.")
            Warrior(playerName)
        }
    }

    // Бот
    val botClasses = listOf(::Warrior, ::Mage, ::Archer)
    val botClass = botClasses.random()
    val bot = botClass("Бот")

    println("\n$YELLOW Бой начинается!$RESET")
    println("${player.name} ($player.javaClass.simpleName) vs ${bot.name} ($bot.javaClass.simpleName)")
    Thread.sleep(1000)

    while (player.isAlive() && bot.isAlive()) {
        // Ход игрока
        player.showStatus()
        bot.showStatus()
        println("\n$GREEN=== Ваш ход ===$RESET")
        println("1. Обычная атака")
        println("2. Специальная способность")

        val choice = readLine()
        when (choice) {
            "1" -> {
                bot.takeDamage(player.attack)
                println("⚔️ ${player.name} атакует! Нанесено ${player.attack} урона.")
            }
            "2" -> {
                println(player.specialAbility(bot))
            }
            else -> {
                bot.takeDamage(player.attack)
                println("⚔️ Пропущен ход. Обычная атака: ${player.attack} урона.")
            }
        }

        if (!bot.isAlive()) break

        // Ход бота (случайный)
        Thread.sleep(800)
        val botChoice = if (Random.nextFloat() < 0.7) 1 else 2
        println("\n$RED=== Ход бота ===$RESET")
        when (botChoice) {
            1 -> {
                player.takeDamage(bot.attack)
                println("⚔️ ${bot.name} атакует! Нанесено ${bot.attack} урона.")
            }
            2 -> {
                println(bot.specialAbility(player))
            }
        }

        Thread.sleep(1200)
    }

    // Результат
    println("\n$MAGENTA=== РЕЗУЛЬТАТ ===$RESET")
    if (player.isAlive()) {
        println("$GREEN🎉 ${player.name} победил!$RESET")
    } else {
        println("$RED💀 ${player.name} погиб. ${bot.name} победил!$RESET")
    }
}