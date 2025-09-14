import java.awt.Color.GRAY
import kotlin.random.Random

// Цвета
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val BLUE = "\u001b[34m"
const val CYAN = "\u001b[36m"
const val MAGENTA = "\u001b[35m"

fun main() {
    println("$MAGENTA=== 🏰 ПРИКЛЮЧЕНИЕ В ПОДЗЕМЕЛЬЕ ===$RESET")
    println("Вы — храбрый герой, спускающийся в древнее подземелье в поисках сокровищ.")
    println("Выбирайте действия. Удачи!")

    val player = Player(health = 100, gold = 0)
    val inventory = mutableListOf<String>()

    entrance(player, inventory)
}

// Класс игрока
data class Player(
    var health: Int,
    var gold: Int,
    var hasSword: Boolean = false,
    var hasArmor: Boolean = false
)

fun entrance(player: Player, inventory: MutableList<String>) {
    println("\n$CYAN Вы находитесь у входа в тёмное подземелье.$RESET")
    println("Перед вами три пути:")
    println("1. $YELLOW Налево — слышны звуки капающей воды$RESET")
    println("2. $GREEN Прямо — тихо и темно$RESET")
    println("3. $RED Направо — пахнет гнилью$RESET")

    when (readChoice()) {
        1 -> leftPath(player, inventory)
        2 -> straightPath(player, inventory)
        3 -> rightPath(player, inventory)
        else -> {
            println("$GRAY Неизвестный выбор. Вы идёте прямо.$RESET")
            straightPath(player, inventory)
        }
    }
}

fun leftPath(player: Player, inventory: MutableList<String>) {
    println("\n$CYAN Вы идёте по влажному тоннелю. На стене — факел.$RESET")
    println("Вы видите сундук.")
    println("1. $YELLOW  Открыть сундук$RESET")
    println("2. $GREEN Пройти мимо$RESET")

    when (readChoice()) {
        1 -> {
            if (Random.nextFloat() < 0.7) {
                println("$GREEN🎉 Вы нашли 20 золотых!$RESET")
                player.gold += 20
            } else {
                println("$RED💀 Ловушка! Вам нанесено 20 урона.$RESET")
                player.health = maxOf(0, player.health - 20)
                if (player.health == 0) return endGame(false, player, inventory)
            }
        }
        else -> println("$GRAY Вы прошли мимо.$RESET")
    }
    dragonCave(player, inventory)
}

fun straightPath(player: Player, inventory: MutableList<String>) {
    println("\n$CYAN Тоннель привёл вас в комнату с мечом на алтаре.$RESET")
    println("1. $YELLOW Взять меч$RESET")
    println("2. $GREEN Оставить$RESET")

    when (readChoice()) {
        1 -> {
            println("$GREEN⚔️ Вы взяли меч! Атака усилена.$RESET")
            player.hasSword = true
            inventory.add("Меч")
        }
        else -> println("$GRAY Вы оставили меч.$RESET")
    }
    monsterRoom(player, inventory)
}

fun rightPath(player: Player, inventory: MutableList<String>) {
    println("\n$RED Вы попали в зал, полный пауков!$RESET")
    if (player.hasArmor) {
        println("$GREEN🛡️ К счастью, у вас есть доспехи! Вы прошли безопасно.$RESET")
    } else {
        val damage = 15
        println("$RED🕷️ Пауки атакуют! Урон: $damage$RESET")
        player.health = maxOf(0, player.health - damage)
        if (player.health == 0) return endGame(false, player, inventory)
    }
    treasureRoom(player, inventory)
}

fun dragonCave(player: Player, inventory: MutableList<String>) {
    println("\n$RED В глубине вы видите дракона!$RESET")
    println("1. $YELLOW Атаковать$RESET")
    println("2. $GREEN Попытаться украсть золото$RESET")
    println("3. $BLUE Убежать$RESET")

    when (readChoice()) {
        1 -> {
            val damage = if (player.hasSword) 50 else 20
            val dragonHealth = 60
            println("$RED⚔️ Вы атакуете дракона! Нанесено $damage урона.$RESET")
            if (damage >= dragonHealth) {
                println("$GREEN🐉 Дракон повержен!$RESET")
                println("$YELLOW Вы нашли сокровище: 100 золотых!$RESET")
                player.gold += 100
                endGame(true, player, inventory)
            } else {
                println("$RED Дракон сильнее! Он атакует в ответ!$RESET")
                player.health = maxOf(0, player.health - 40)
                if (player.health == 0) {
                    println("$RED💀 Вы погибли от когтей дракона.$RESET")
                } else {
                    println("$CYAN Вы сбежали, раненые.$RESET")
                }
                endGame(false, player, inventory)
            }
        }
        2 -> {
            if (Random.nextBoolean()) {
                println("$GREEN🎉 Вам удалось украсть 50 золотых!$RESET")
                player.gold += 50
            } else {
                println("$RED💀 Дракон заметил вас! Вы погибли.$RESET")
                player.health = 0
                endGame(false, player, inventory)
            }
        }
        else -> {
            println("$BLUE Вы сбежали.$RESET")
            endGame(false, player, inventory)
        }
    }
}

fun monsterRoom(player: Player, inventory: MutableList<String>) {
    println("\n$RED В комнате — огромный тролль!$RESET")
    println("1. Атаковать")
    println("2. Бежать")

    when (readChoice()) {
        1 -> {
            val damage = if (player.hasSword) 30 else 10
            println("$RED⚔️ Урон троллю: $damage$RESET")
            if (damage >= 30) {
                println("$GREEN🎉 Вы победили! Нашли 30 золотых.$RESET")
                player.gold += 30
            } else {
                println("$RED💥 Тролль ударил вас! Урон: 25$RESET")
                player.health = maxOf(0, player.health - 25)
            }
        }
        else -> println("$BLUE Вы сбежали.$RESET")
    }
    treasureRoom(player, inventory)
}

fun treasureRoom(player: Player, inventory: MutableList<String>) {
    println("\n$YELLOW Вы нашли комнату с сокровищами!$RESET")
    println("Золото: 50, доспехи на статуе.")
    println("1. Взять всё")
    println("2. Взять только золото")
    println("3. Взять доспехи")

    when (readChoice()) {
        1 -> {
            player.gold += 50
            player.hasArmor = true
            inventory.add("Доспехи")
            println("$GREEN Вы взяли всё!$RESET")
        }
        2 -> {
            player.gold += 50
            println("$GREEN Только золото.$RESET")
        }
        3 -> {
            player.hasArmor = true
            inventory.add("Доспехи")
            println("$GREEN Только доспехи.$RESET")
        }
    }
    dragonCave(player, inventory)
}

fun endGame(victory: Boolean, player: Player, inventory: List<String>) {
    println("\n" + if (victory) "$GREEN🎉 ПОБЕДА!$RESET" else "$RED💀 КОНЕЦ$RESET")
    println("Финальные данные:")
    println("❤️ Здоровье: ${player.health}")
    println("💰 Золото: ${player.gold}")
    println("🎒 Инвентарь: ${inventory.joinToString(", ").ifEmpty { "пусто" }}")
    println("Спасибо за игру!")
    return
}

// Универсальная функция ввода
fun readChoice(): Int {
    print("$CYAN Ваш выбор: $RESET")
    return readLine()?.trim()?.toIntOrNull() ?: 0
}