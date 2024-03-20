import kotlin.reflect.typeOf

/*
    returns true if operator A has equal or greater precedence.
 */
fun hasPrecedence(opA: Char, opB: Char): Boolean {
    val result = when(opA){
        '+'->  (opB == '+' || opB == '-')
        '-'->  (opB == '+' || opB == '-')
        '*'->  (opB != '^')
        '/'->  (opB != '^')
        '^'->  (opB != '^') //exponentials are evaluated right to left so we cannot allow ^ to have equal precedence to itself
        else -> false //if operator not found default to false
    }
    return result
}
class Polynomials {
    /*here we set an array of chars representing our mathematical operators
    we can use their index to identify which operators have precedence over each other
    */
    val operators = arrayOf('+', '-', '*', '/', '^')
    fun parse(expression: String): Int {
        var x: Int
        var y: Int
        val RPN = shuntingYard(expression)
        val evaluation: ArrayDeque<Int> = ArrayDeque()
        var v: Int
        while (RPN.isNotEmpty()){
            val token = RPN.removeFirst()
            if (token.isDigit()){
                evaluation.add(token.digitToInt())
            }else when(token){
                '+'-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = x + y
                    evaluation.add(v)
                }
                '-'-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = y - x
                    evaluation.add(v)
                }
                '*'-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = x * y
                    evaluation.add(v)
                }
                '/'-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    v = y / x
                    evaluation.add(v)
                }
                '^'-> {
                    x = evaluation.removeLast()
                    y = evaluation.removeLast()
                    //not using Kotlin.math package
                    v = 1
                    for (i in 0..<y) {
                        v *= x
                    }
                    evaluation.add(v)
                }

            }


        }

        return evaluation.removeLast()
    }

    //https://www.chris-j.co.uk/parsing.php
    //https://en.wikipedia.org/wiki/Shunting_yard_algorithm
    fun shuntingYard(expression: String): ArrayDeque<Char>{

        val tokens = buildDeque(expression)
        var token: Char

        val operatorStack = ArrayDeque<Char>()
        val outputQueue = ArrayDeque<Char>()

        /*
        While the operator at the top of operatorStack has => precedence than
        the current operator, pop it and add to end of outputQueue. Finally push current
        operator to top of operatorStack
        */
        fun parseOperator(operator: Char){
            var temp = operatorStack.last()
            while (temp != '(' && (hasPrecedence(temp, operator))){
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
            while(temp != '('){
                outputQueue.add(operatorStack.removeLast())
                temp = operatorStack.last()
            }
            operatorStack.removeLast()
        }

        while (tokens.isNotEmpty()){
            token = tokens.removeFirst()
            //check for type of token (number, operator)
            if (token.isDigit()){
                outputQueue.add(token)
            } else if (token in operators){
                if (operatorStack.isEmpty()){
                    operatorStack.add(token)
                }else {
                    parseOperator(token)
                }
            }else when(token){
                '(' -> operatorStack.add(token)
                ')' -> parseClosingParenthesis()
            }

        }
        while(operatorStack.isNotEmpty()){
            outputQueue.add(operatorStack.removeLast())
        }
        return outputQueue
    }
    fun buildDeque(st: String): ArrayDeque<Char>{
        val ad = ArrayDeque<Char>()
        for (char in st){
            ad.add(char)
        }
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