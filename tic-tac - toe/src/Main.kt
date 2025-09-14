
const val RESET = "\u001b[0m"
const val RED = "\u001b[31m"
const val BLUE = "\u001b[34m"
const val GREEN = "\u001b[32m"
const val YELLOW = "\u001b[33m"
const val CYAN = "\u001b[36m"

fun main(){
    var playersWins = 0
    var botWins = 0
    var draws = 0

    println("$CYAN === XO Крестики-Нолики с ботом ====$RESET")

    while (true) {
        println("\n$CYAN Выберите режим:$RESET")
        println("1.$RED Игрок vs Бот $RESET")
        println("2.$BLUE Игрок vs Бот $RESET")
        println("3.$YELLOW Просмотр счёта $RESET")
        println("4.$GREEN Выход $RESET")

        print("$CYAN Ваш выбор:$RESET")
        val choice = readLine()?.trim()

        when (choice){
            "1"->{
                val result = playVsBot(playersWins,botWins,draws)
                playersWins = result.first
                botWins =result.second
                draws = result.third
            }
            "2"->playPVP()
            "3"->println("$YELLOW Счёт:Игрок $playersWins: $botWins Бот| Ничья:$draws$RESET")
            "4","exit","выход"->{
                println("$GREEN Спасибо за игру!$RESET")
                return
            }
            else -> println("$RED  Выберите 1,2,3 или 4 $RESET")
        }
    }
}
// Режим: Игрок vs Бот
// Возвращает обновлённый счёт: (playerWins, botWins, draws)
fun playVsBot(playerWins:Int,botWins:Int,draws: Int): Triple<Int, Int, Int>{
    val board = Array(3){ CharArray(3){' '} }
    var currentPlayer = 'X'
    var moves =0
    var newPlayerWins = playerWins
    var newBotWins=botWins
    var newDraws = draws

    while (true){
        printBoard(board)

        if (currentPlayer == 'X'){
            //Ход игрока
            println("$RED Ваш ход(Х).Введите строку и столбец (1-3):$RESET")
            val input = readLine()?.trim()

            if (input?.lowercase() in listOf("exit","выход")){
                println("$YELLOW Выход из игры.$RESET")
                return Triple(newPlayerWins,newBotWins,newDraws)
            }
            val parts = input?.split("")?.filter { it.isNotBlank() }
            if (parts?.size !=2){
                println("$RED  Ошибка:введите два числа от 1 до $RESET")
                continue
            }
            val row =parts[0].toIntOrNull()?.minus(1)
            val col = parts[1].toIntOrNull()?.minus(1)

            if (row == null || col == null || row !in 0..2 || col !in 0..2){
                println("$RED Введите числа от 1 до 3 $RESET")
                continue
            }
            if (board[row][col] !=' '){
                println("$RED Клетка занята!$RESET ")
                continue
            }
            board[col][col] = currentPlayer
            moves++
        }else{
            //ход бота
            println("$CYAN Бот делает ход...$RESET")
            Thread.sleep(800)
            makeBotMove(board)
            moves++
        }
        //проверка победы
        if (checkWin(board,currentPlayer)){
            printBoard(board)
            if (currentPlayer == 'X'){
                println("$GREEN Вы победили! Поздравляем!$RESET")
                newPlayerWins++
            }else{
                println("$RED Бот победил! Удачи в следующий раз!$RESET")
                newBotWins++
            }
            break
        }
        if (moves == 9){
            printBoard(board)
            println("$YELLOW Ничья!$RESET")
            newDraws++
            break
        }
        currentPlayer = if (currentPlayer == 'X')'O' else 'X'
    }
    //Спросить, хочет ли играть ещё
    println("\n $CYAN Сыграем еще раз?(да/нет)$RESET")
    val again = readLine()?.lowercase()
    if (again in listOf("Да","yes","y","д")){
        return playVsBot(newPlayerWins,newBotWins,newDraws)
    }else{
        return Triple(newPlayerWins,newBotWins,newDraws)
    }
}
//Бот:Случайный ход
fun makeBotMove(board: Array<CharArray>){
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    for (i in 0..2){
        for (j in 0..2){
            if (board[i][j] == ' '){
                emptyCells.add(Pair(i,j))
            }
        }
    }
    if (emptyCells.isNotEmpty()){
        val (row,col)=emptyCells.random()
        board[row][col]='0'
    }
}
// Режим: Игрок vs Игрок
fun playPVP(){
    val board = Array(3){ CharArray(3){' '} }
    var currentPlayer ='X'
    var moves =0

    while (true){
        printBoard(board)
        println("${if (currentPlayer == 'X') RED else BLUE}Игрок $currentPlayer$RESET,введите строку и столбец(1-3)")
        val input=readLine()?.trim()

        if (input?.lowercase() in listOf("exit")){
            println("$YELLOW Игра заверщена.$RESET")
            return
        }
        val parts = input?.split(" ")?.filter { it.isNotBlank() }
        if (parts?.size !=2){
            println("$RED Введите два числа от 1 до 3 $RESET")
            continue
        }
        val row = parts[0].toIntOrNull()?.minus(1)
        val col = parts[1].toIntOrNull()?.minus(1)

        if (row == null || col ==null || row !in 0..2 || col !in 0..2 || board[row][col] !=' '){
            println("$RED Некорректный ход!$RESET")
            continue
        }
        board [row][col] = currentPlayer
        moves++

        if (checkWin(board,currentPlayer)){
            printBoard(board)
            println("${GREEN} Игрок $currentPlayer победил!$RESET")
            break
        }
        if (moves==9){
            printBoard(board)
            println("$YELLOW Ничья!$RESET")
            break
        }
        currentPlayer= if (currentPlayer == 'X')'O' else 'X'
    }
    println("\n $CYAN Сыграем в игру еще раз?(да,нет)$RESET")
    val again = readLine()?.lowercase()
    if (again in listOf("да","yes","y","д")){
        playPVP()
    }
}
// Вывод доски с цветами
fun printBoard(board: Array<CharArray>){
    println()
    for (i in board.indices) {
        val row = mutableListOf<String>()
        for (j in 0..2){
            val cell = when(board[i][j]){
                'X'->"$RED X $RESET"
                'O'->"$BLUE O $RESET"
                else -> " "
            }
            row.add(cell)
        }
        println("${row[0]} ${row[1]} ${row[2]}")
        if (i < 2) println("─────┼─────┼─────")
    }
    println()
}
// Проверка победы
fun checkWin(board: Array<CharArray>,player: Char): Boolean{
    // Строки
    for (i in 0..2){
        if (board[i][0] == player && board [i][1] == player && board [i][2] ==player)return true
    }
    // Столбцы
    for (j in 0..2){
        if (board[0][j] == player && board[1][j] == player && board [2][j] ==player)return true
    }
    // Диагонали
    if (board[0][0] == player && board [1][1]  == player && board[2][2] == player)return true
    if (board [0][2] == player && board[1][1] ==player && board[2][0]== player)return true
return false
}