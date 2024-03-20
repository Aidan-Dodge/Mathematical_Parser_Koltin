import kotlin.reflect.typeOf

/*
    returns true if operator A has equal or greater precedence over operator B.
 */
fun hasPrecedence(opA: String, opB: String): Boolean {
    val result = when(opA){
        "+"->  (opB == "+" || opB == "-")
        "-"->  (opB == "+" || opB == "-")
        "*"->  (opB != "^")
        "/"->  (opB != "^")
        "^"->  (opB != "^") //exponentials are evaluated right to left so we cannot allow ^ to have equal precedence to itself
        else -> false //if operator not found default to false
    }
    return result
}

/*
helper function which checks if a string is numeric
 */
fun isNumeric(st: String): Boolean {
    return st.toDoubleOrNull() != null
}

class Polynomials {
    /*here we set an array of chars representing our mathematical operators
    we can use their index to identify which operators have precedence over each other
    */
    val operators = arrayOf("+", "-", "*", "/", "^")
    fun parse(expression: String): Int {
        var x: Int
        var y: Int
        val RPN = shuntingYard(expression)
        val evaluation: ArrayDeque<Int> = ArrayDeque()
        var v: Int

        /*
        evaluation of Reverse Polish Notation:
        remove a value from the RPN queue, and if it is a digit place it on top
        of the evaluation stack. When an operator is found, pop off the top two numbers
        from the evaluation stack and execute that operation with those 2 numbers and place
        the result back on top of the evaluation stack. At the end there should be one value
        left in the stack, and it should be the result of the arithmetic expression.
         */
        while (RPN.isNotEmpty()){
            val token = RPN.removeFirst()
            if (isNumeric(token)){
                evaluation.add(token.toInt())
            }else when(token){
                "+"-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = x + y
                    evaluation.add(v)
                }
                "-"-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = y - x
                    evaluation.add(v)
                }
                "*"-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = x * y
                    evaluation.add(v)
                }
                "/"-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = y / x
                    evaluation.add(v)
                }
                "^"-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    //not using Kotlin.math package
                    v = 1
                    for (i in 0..<x) {
                        v *= y
                    }
                    evaluation.add(v)
                }

            }


        }

        return evaluation.removeLast()
    }

    //https://www.chris-j.co.uk/parsing.php
    //https://en.wikipedia.org/wiki/Shunting_yard_algorithm
    fun shuntingYard(expression: String): ArrayDeque<String>{

        val tokens = buildDeque(expression)
        var token: String

        val operatorStack = ArrayDeque<String>()
        val outputQueue = ArrayDeque<String>()

        /*
        While the operator at the top of operatorStack has => precedence than
        the current operator, pop it and add to end of outputQueue. Finally push current
        operator to top of operatorStack
        */
        fun parseOperator(operator: String){
            var temp = operatorStack.last()
            while (temp != "(" && (hasPrecedence(temp, operator))){
                outputQueue.add(operatorStack.removeLast())
                if (operatorStack.isEmpty()){break}
                else{temp = operatorStack.last()}
            }
             operatorStack.add(operator)
        }

        /*
        For a closing bracket, pop operators of the operatorStack
        and put onto outputQueue until the opening bracket is found.
        Then pop off the opening bracket.
         */
        fun parseClosingParenthesis(){
            var temp = operatorStack.last()
            while(temp != "("){
                outputQueue.add(operatorStack.removeLast())
                temp = operatorStack.last()
            }
            operatorStack.removeLast()
        }

        while (tokens.isNotEmpty()){
            token = tokens.removeFirst()
            //check for type of token (number, operator)
            if (isNumeric(token)){
                outputQueue.add(token)
            } else if (token in operators){
                if (operatorStack.isEmpty()){
                    operatorStack.add(token) //no operators to compare
                }else {
                    parseOperator(token)
                }
            }else when(token){
                "(" -> operatorStack.add(token)
                ")" -> parseClosingParenthesis()
            }

        }
        while(operatorStack.isNotEmpty()){
            outputQueue.add(operatorStack.removeLast())
        }
        return outputQueue
    }
    fun buildDeque(st: String): ArrayDeque<String>{
        val ad = ArrayDeque<String>()
        var currentNumber = ""
        st.forEach{
            //check for consecutive digits to combine for whole number
            if (it.isDigit()){
                currentNumber += it
            }
            else{
                if (currentNumber.isNotEmpty()) {ad.add(currentNumber)}
                currentNumber = ""
                ad.add(it.toString())
            }
        }
        if (currentNumber.isNotEmpty()) {ad.add(currentNumber)} //catches if the last object of expression was a number
        return ad
    }


    fun differentiate(expression: String): String {
        return expression
    }

    fun simplify(expression: String): String {
        return expression
    }

    fun print(expression: String): Unit {

    }
}