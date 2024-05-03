package calculator

object VariableStorage {
    private var variables = mutableMapOf<String, Int>()

    fun setVariable(name: String, value: Int) {
        variables[name] = value
    }

    fun getVariable(name: String): Int? {
        if (variables[name] == null) { println("Unknown variable") }
        return variables[name]
    }
}

fun isCalculationExpressionValid(expression: String): Boolean {
    val pattern = "((-|[+])?\\s*\\d+\\s*)\\s*((-|[+])+\\s*\\d+\\s*)*".toRegex()
    return expression.matches(pattern)
}

fun calculator(userInput: String) {
    println(userInput
        .replace("+", "")
        .replace("--", "")
        .replace(Regex("- +"), "-")
        .split(Regex(" +"))
        .sumOf { it.toInt() })
}

//If an identifier or value of a variable is invalid during variable declaration, the program must print a message
fun validateAssignment(input: String) {
    val identifierRegex = Regex("""[a-zA-Z]+""")
    val assignmentRegex = Regex("""-*\d+|[a-zA-Z]+""")
    if (input.contains("=")) {
        val parts = input.split("=")
        // Validate the identifier on the left side
        val identifier = parts[0].trim()
        if (!identifierRegex.matches(identifier)) {
            println("Invalid identifier")
        } else {
            // Validate the assignment on the right side
            val assignment = parts[1].trim()
            if (!assignmentRegex.matches(assignment)) {
                println("Invalid assignment")
            } else processInput(input)
        }
    } else {
        processInput(input)
    }
}

fun processInput(userInput: String) {
    val variableStorage = VariableStorage
    val variableRegex = Regex("[a-zA-Z]+")

    if (!userInput.contains("=".toRegex())) { // Expression with + or -
        val processedInput = userInput.split(" ").map { it.trim() }.mapNotNull { term ->
            if (term.matches(variableRegex)) variableStorage.getVariable(term)?.toString() else term
        } // if item = [a-zA-Z] -> get from Storage und ADD to expression -> calc

        if (isCalculationExpressionValid(processedInput.joinToString(" "))) {
            calculator(processedInput.joinToString(" "))
        }
    } else { // Assignment
        val assignmentPattern = """^\s*[a-zA-Z]+\s*=\s*-?\d+\s*$""".toRegex() //> a = 5 / a =-4
        val setSavedValuePattern = """^\s*[a-zA-Z]+\s*=\s*[a-zA-Z]+\s*$""".toRegex() //if val = val
        val assignmentParts = userInput.split("=").map { it.trim() }

        when {
            assignmentParts.size == 1 -> {
                variableStorage.getVariable(userInput)?.let { value ->
                    println(value)
                }
            } //> a
            userInput.matches(assignmentPattern) -> variableStorage.setVariable(
                assignmentParts[0],
                assignmentParts[1].toInt()
            )

            userInput.matches(setSavedValuePattern) -> {
                val key = variableStorage.getVariable(assignmentParts[1])
                if (key != null) {
                    variableStorage.setVariable(assignmentParts[0], key)
                } else println("Unknown variable")
            }
        }
    }
}

fun usersInterface() {
    val commandPattern = Regex("""/.*""")
    while (true) {
        val userInput = readLine()
        when (userInput) {
            null, "" -> continue    // Ignore empty lines
            "/exit" -> break        // Exit the loop if /exit command is entered
            "/help" -> println("The program calculates the sum and subtract of numbers")
            else -> {
                when {
                    userInput.matches(commandPattern) -> println("Unknown command")
                    isCalculationExpressionValid(userInput) -> calculator(userInput)
                    else -> validateAssignment(userInput)
                }
            }
        }
    }
}

fun main() {
    usersInterface()
    println("Bye!")
}
