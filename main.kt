/*
 * CS 141 Extra Credit: Symbolic Expressions in Kotlin
 * Aidan Dodge, adodge
 */
import kotlin.math.pow

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

/*
The Expression class is a parent class where its derived classes are the different types of expressions.
Each of the types of expressions are data classes that hold values for the details of the expressions.
 */
open class Expression(open val negative: Boolean = false) {

    override fun toString(): String {
        return when (this){
            is Number -> value.toInt().toString()
            is Variable ->name
            is Add -> {
                //account for adding negatives
                if (left.negative){"$right-$left"}
                else if (right.negative) {"$left-$right"}
                else {"$left+$right"}
            }
            is Subtract -> {
                //acount for substracting negatives
                if (right.negative) {"$left+$right"}
                else {"$left-$right"}
            }
            is Multiply -> "$left*$right"
            is Divide -> "$left/$right"
            is Power -> "$left^$right"
            else ->"NOT PROPER TYPE" //shouldn't ever be reached
        }
    }

    fun copy(neg: Boolean): Expression {
        return when (this) {
            is Number -> this.copy(negative = neg)
            is Variable -> this.copy(negative = neg)
            is Add -> this.copy(negative = neg)
            is Subtract -> this.copy(negative = neg)
            is Multiply -> this.copy(negative = neg)
            is Divide -> this.copy(negative = neg)
            is Power -> this.copy(negative = neg)
            else -> this
        }

    }
}
data class Number(val value: Double, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}
data class Variable(val name: String, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}
data class Add(val left: Expression, val right: Expression, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}
data class Subtract(val left: Expression, val right: Expression, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}
data class Multiply(val left: Expression, val right: Expression, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }

}
data class Divide(val left: Expression, val right: Expression, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}
data class Power(val left: Expression, val right: Expression, override val negative: Boolean = false): Expression(){
    override fun toString(): String {
        return super.toString()
    }
}

//builds a dequeue representing the string expression
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

class Polynomials {
    /*here we set an array of chars representing our mathematical operators
    we can use their index to identify which operators have precedence over each other
    */
    val operators = arrayOf("+", "-", "*", "/", "^")

    //https://en.wikipedia.org/wiki/Reverse_Polish_notation
    fun parse(expression: String): Int {
        var x: Double
        var y: Double
        val RPN = shuntingYard(expression)
        val evaluation: ArrayDeque<Int> = ArrayDeque()
        var v: Double

        //based on the operation take 2 numbers at top of eval stack and do math
        fun eval(operation: String){
            x = evaluation.removeLast().toDouble()
            y = evaluation.removeLast().toDouble()
            v = when(operation){
                "+"-> x + y
                "-"-> y - x
                "*"-> x * y
                "/"-> y/x
                "^"-> y.pow(x)
                else-> 999999.99999 //should never reach this
            }

            //add result back to top of the stack
            evaluation.add(v.toInt())
        }
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
                    eval("+")
                }
                "-"-> {
                    eval("-")
                }
                "*"-> {
                    eval("*")
                }
                "/"-> {
                    eval("/")
                }
                "^"-> {
                    eval("^")
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
                else ->outputQueue.add(token) //add to account for variables
            }

        }
        while(operatorStack.isNotEmpty()){
            outputQueue.add(operatorStack.removeLast())
        }
        return outputQueue
    }

    /*
    The derivative function also uses RPN. This method simplifies the expression
    before deriving, then derives based on the operation.
     */
    fun differentiate(expression: String): Expression {
        val simple = simplify(expression) //simplify first
        val derived: Expression = derive(simple) //will recursively be called for different parts of equation

        //simplify again after deriving
        val simpdiff = simplify(derived.toString())
        return simpdiff
    }

    fun derive(simple: Expression): Expression {
        return when (simple) {
            // d/dt (N) = 0, for N is all real numbers
            is Number -> Number(0.0)
            //d/dt (X) = 1
            is Variable -> Number(1.0)
            // d/dt (X + Y) = X' + Y'
            is Add -> Add(derive(simple.left), derive(simple.right))
            // d/dt (X - Y) = X' - Y'
            is Subtract -> Subtract(derive(simple.left), derive(simple.right))
            is Multiply -> {
                val E: Expression = checkSimpleMultDiff(simple) // check for simple multiplications like 2*X
                if (E == simple) {
                    // d/dt (A*B) = A'B + AB'
                    Add(Multiply(derive(simple.left), simple.right), Multiply(simple.left, derive(simple.right)))
                }
                else {E}
            }
            is Divide ->{
                val E: Expression = checkDiffDiv(simple)
                if (E == simple) {
                    // d/dt (A/B) = (BA' - AB') / B^2
                    val top = Subtract(
                        Multiply(simple.right, derive(simple.left)),
                        Multiply(simple.left, derive(simple.right))
                    )
                    val bottom = Power(simple.right, Number(2.0))
                    Divide(top, bottom)
                }
                else {
                    E // for 1/x
                }
            }
            //power rule only applies for real numbers in the exponent
            //do not forget chain rule
            is Power -> {
                if (simple.right is Number) {
                    val exponent = Number(simple.right.value - 1)
                    val coefficient = Number(simple.right.value)
                    Multiply(coefficient, Multiply(Power(simple.left, exponent), derive(simple.left)))
                } else {simple} // not accounting for X^Y yet
            }
            else -> simple // leave as is
        }
    }
    //Checks for the case 1/(x^N) = -N/(x^(N+1))
    fun checkDiffDiv(E: Divide): Expression{
        return when(E.left){
            is Number -> {
                when (E.right){
                    is Variable -> {
                        // 1/x
                        Divide(simpMult(E.left, Number(1.0)), Power(E.right, Number(2.0)), negative=true)
                    }
                    is Power -> {
                        if (E.right.right is Number) {
                            // 1/(x^N)
                            Divide(
                                simpMult(E.left, Number(E.right.right.value)),
                                Power(E.right.left, Number(E.right.right.value + 1)),
                                negative = true
                            )
                        }
                        else {E}
                    }
                    else -> E
                }
            }
            else -> E
        }
    }
    /*
    This function checks for the different simple multiplication functions, to
    avoid unnecessary extra differentiation calls
     */
    fun checkSimpleMultDiff(E: Multiply): Expression {
        if (E.left is Number && E.right is Variable) {return E.left}
        if (E.left is Variable && E.right is Number) {return E.right}
        if (E.left is Number && E.right is Power) {
            return Multiply(simpMult(E.right.right, E.left), Power(E.right.left, simpSub(Number(1.0), E.right.right)))
        }
        else {return E}
    }
    /*
    My simplify function will use the PRN notation to simplify the expression
    and will reverse the result back to infix and return
     */
    fun simplify(expression: String): Expression {
        val rpn = shuntingYard(expression)
        val evaluation: ArrayDeque<Expression> = ArrayDeque()

        fun simplifyHelp(xp: String): Expression {
            val x = evaluation.removeLast()
            val y = evaluation.removeLast()
            return when(xp){
                    "+" -> simpAdd(x,y)
                    "-" -> simpSub(x,y)
                    "*" -> simpMult(x,y)
                    "/" -> simpDiv(x,y)
                    "^" -> simpPow(x,y)
                    else -> Expression() //should never reach this
            }
        }

        while (rpn.isNotEmpty()) {
            val token = rpn.removeFirst()
            if (isNumeric(token)) { // token is number
                evaluation.add(Number(token.toDouble()))
            } else if (token !in operators) { //token is variable
                evaluation.add(Variable(token))
            } else when (token) { // token is operator
                "+" -> {
                    evaluation.add(simplifyHelp("+"))
                }

                "-" -> {
                    evaluation.add(simplifyHelp("-"))
                }

                "*" -> {
                    evaluation.add(simplifyHelp("*"))

                }

                "/" -> {
                    evaluation.add(simplifyHelp("/"))
                }

                "^" -> {
                    evaluation.add(simplifyHelp("^"))
                }
            }
        }
        //val equation: String = (evaluation.removeLast()).toString()
        return evaluation.removeLast()
    }

    /*
    accounts for adding symbolic expressions that are not just numbers or variables,
    Like adding coefficients or if the same variable is in alike statements.
     */
    fun nestedAdd(x: Expression, y: Expression): Expression{
        return when (x) {
            // nX + mX = (n+M)X
            //goal: make number coefficients always on the left side
            is Multiply -> {
                when (y) {
                    is Multiply -> {
                        if (x.right == y.right) {
                           Multiply(simpAdd(x.left, y.left), x.right)
                        } else {Add(x,y)}
                    }
                    else -> Add(x,y) // nothing to simplify
                }
            }
            else ->Add(x,y) //nothing to simplify
        }
    }
    fun simpAdd(x: Expression, y: Expression): Expression{
        if (x == y) {return simpMult(Number(2.0), x)} // X + X = 2X
        if (x.negative) {
            val sub = x.copy(neg=false)
            return simpSub(sub,y) //y + (-x) = Y - X
        }
        if (y.negative) {
            val sub = y.copy(neg=false) //make a copy that isn't negative
            return simpSub(sub, x) //(-x) + y = y - x
        }
         return when(x) {
             is Number -> {
                 if (x.value == 0.0) {y} //0+x = x
                 else{
                     when (y) {
                         is Number -> {Number(x.value + y.value)}
                         is Variable -> {Add(y,x)}
                         else -> {Add(y,x)}
                     }
                 }
             }
             is Variable -> {
                 when (y) {
                     is Number -> {
                         if (y.value == 0.0) {x} // 0 * x = 0
                         else{Add(y,x)}
                     }
                     is Variable -> {
                         if (y == x) { Multiply(Number(2.0),x) } // x + x = 2*x
                         else {Add(y,x)}
                     }
                     else -> {Add(y,x)}
                 }
             }
             else-> {
                 when (y) {
                     is Number -> {if (y.value == 0.0){x} else{Add(y,x)}} // 0 * x = 0
                     is Variable -> Add(y,x)
                     else -> { nestedAdd(y,x) }
                 }
             }
             }
    }

    /*
    TO IMPLEMENT:
    0 - x = -x
    y - (-x) = y + x
    y + (-x) = y-x
    other unary stuff.
     */
    fun simpSub(x: Expression, y: Expression): Expression{
        //add account for 0-x = -x
        if (x == y){return Number(0.0)} // x - x = 0
        if (x.negative) {
            val adder = x.copy(neg=false)
            return simpAdd(adder, y) //x - (-y) = x + y
        }
        return when(x) {
            is Number -> {
                if (x.value == 0.0){y} //x - 0 = x
                else {
                    when (y) {
                        is Number -> { Number(y.value - x.value) }
                        is Variable -> {Subtract(y,x)}
                        else -> { Subtract(y, x) }
                    }
                }
            }
            is Variable ->{
                when (y) {
                    is Number -> {Subtract(y,x)}
                    is Variable -> {Subtract(y,x)}
                    else -> Subtract(y,x)
                }
            }
            else-> {
                when (y) {
                    is Number -> {Subtract(y,x)}
                    is Variable -> {Subtract(y,x)}
                    else -> Subtract(y,x)
                }
            }
        }
    }


    /*
    Created for when you are multiplying into another symbolic expression that isn't a
    single variable or number. These occurrences happen during the simplification.
    X is the expression and y is the multiplier.
     */
    fun nestedMult(x: Expression, y: Expression): Expression {
        return when (x){
            //y * (A + B) = yA + yB
            is Add -> {
                simpAdd(simpMult(x.right, y), simpMult(x.left, y))
            }
            // y * (A - B) == yA- yB
            is Subtract -> {
                simpSub(simpMult(x.right, y), simpMult(x.left, y))
            }
            // y* (A*B) = y*A*B. <- depending on if A or B match the type of y, further simplification can be done
            is Multiply -> {
                when (y){
                    // N*(X*Y) where N is a number
                    is Number -> {
                        if (x.left is Number) {
                            Multiply(Number(x.left.value * y.value), x.right)
                        } else if (x.right is Number) {
                            Multiply(x.left, Number(x.right.value * y.value))
                        } else {
                            Multiply (x, y)
                        }
                    }
                    //X*(X*Y)
                    is Variable -> {
                        if (x.left is Variable && (x.left == y)) {
                            Multiply(Power(x.left, Number(2.0)), x.right)
                        } else if (x.right is Variable && (x.right == y)) {
                            Multiply(x.left, Power(x.right, Number(2.0)))
                        } else {Multiply(x, y)}
                    }
                    else -> {Multiply(x,y)}// so many symbolic combinations
                }
            }
            // y* (A/B) = yA/B. <- check if A or B match type of y
            is Divide -> {
                when (y){
                    is Number -> {
                        if (x.left is Number){
                            Divide(Number(x.left.value * y.value), x.right)
                        } else if (x.right is Number) {
                            Multiply(Number(y.value / x.right.value), x.left)
                        } else {Multiply(x, y)}
                    }
                    is Variable -> {
                        if (x.left is Variable && (y == x.left)) {
                            Divide(Power(y, Number(2.0)), x.right)
                        } else if (x.right is Variable && (y == x.right)){
                            x.left
                        }else {Multiply(x, y)}
                    }
                    else -> Multiply(x,y)
                }
            }
            // y * y^N = x^(N+1)
            is Power -> {
                if (x.left == y && x.right is Number) {Power (x.left, Number(x.right.value + 1)) }
                else {Multiply(y, x)}
            }
            else -> {Multiply(x,y)} //shouldn't reach this
        }
    }
    fun simpMult(x: Expression, y: Expression): Expression{
        return when(x) {
            is Number -> {
                when (x.value) {
                    0.0 -> Number(0.0) // x*0 = 0
                    1.0 -> y// x*1 = x
                    else -> when (y) {
                        is Number -> {
                            Number(x.value * y.value)
                        }
                        is Variable -> Multiply(y,x)
                        else -> { nestedMult(y, x) }
                    }
                }
            }
            is Variable -> {
                when (y){
                    is Number -> {
                        if (y.value == 0.0) {Number(0.0)} // 0*x = 0
                        else if (y.value == 1.0) {x} // 1*x = x
                        else {Multiply(y,x)}
                    }
                    is Variable -> {
                        if (x == y) {Power(y,Number(2.0))} // x*x = x^2
                        else{Multiply(y,x)}
                    }
                    else -> {nestedMult(y,x)}
                }
            }
            else-> {
                when (y) {
                    is Number -> {if (y.value == 0.0){Number(0.0)} //0*x = 0
                    else if (y.value == 1.0) {x} // 1*x = x
                    else {nestedMult(x,y)}}
                    else -> {nestedMult(x,y)}
                }
            }
        }
    }
    /*
    For when dividing a symbolic expression by a variable. X is the expression and
    Y is the variable.
     */
    fun nestDiv(x: Expression, y: Expression): Expression{
        return when (x) {
            is Multiply -> {
                if (x.left == y) { x.right } // X*Y/X = Y
                else if (x.right == y) { x.left } // Y*X/X = Y
                // (x^N)*A = X^(N-1)*A
                if (x.left is Power && (x.left.left == y)){
                    Multiply(simpPow(simpSub(Number(1.0), x.left.right), x.left.left), x.right)
                } else if (x.right is Power && x.right.left == y) {  //A*X^N/X = A*X^(N-1)
                    Multiply(x.left, simpPow(simpSub(Number(1.0),  x.right.right), x.right.left))
                }
                else Divide(x, y) //nothing to simplify
            }
            is Divide -> {Divide(x.left, simpMult(x.right, y))}
            is Power -> {
                // X^N/X = X^(N-1) ; simpSub should solve if N is number or expression
                if (x.left == y) {
                    Power(x.left, simpSub(Number(1.0), x.right)) //
                } else {Divide(x,y)} // nothing to simplify
            }
            else -> Divide(x,y) //nothing to simplify

        }
    }

    fun simpDiv(x: Expression, y: Expression): Expression{
        /*
        this eq check should work for all expression types due to the built-in equals
        method for data classes in Kotlin
         */
        if (x == y) {return Number(1.0)} // x/x = 1
        return when(x) {
            is Number -> {
                when (y) {
                    is Number -> {
                        if (y.value == 0.0) {Number(0.0)} // 0/x = 0
                        else {Number(y.value / x.value)}
                    }
                    is Variable -> {Divide(y,x)}
                    else -> {
                        if (x.value == 1.0){y} // x/1 = x
                        else{Divide(y,x)}
                    }
                }
            }
            is Variable -> {
                when (y){
                    is Number -> {
                        if (y.value == 0.0) {Number(0.0)}
                        else {Divide(y,x)}
                    }
                    is Variable -> {Divide(y,x)}
                    else -> {nestDiv(y,x)}
                }
            }
            else-> {
                when (y) {
                    is Number -> {
                        if (y.value == 0.0) {Number(0.0)}
                        else{Divide(y,x)}
                    } // 0/x = 0
                    else -> {Divide(y,x)}
                }
            }
        }
    }
    fun simpPow(x: Expression, y: Expression): Expression{
        return when(x) {
            is Number -> {
                if (x.value == 0.0) {Number(1.0)} // x^0 = 1
                else if (x.value==1.0) {y} // x^1 = x
                else {
                    when(y){
                        is Number -> {Number(y.value.pow(x.value))}
                        is Variable -> {Power(y,x)}
                        is Power -> {Power(y.left, Multiply(y.right, x))}
                        else -> Power(y,x)
                    }
                }
            }
            is Variable -> {
                when (y) {
                    is Number -> {
                        if (y.value == 0.0) {Number(0.0)} // 0^N = 0
                        else if (y.value == 1.0) {Number(1.0)} // 1^N = 1
                        else {Power(y,x)}
                    }
                    is Variable -> {Power(y,x)}
                    is Power -> {Power(y.left, Multiply(y.right,x))}
                    else -> {Power(y,x)}
                }
            }
            else-> {
                //same as variable could change later to simplify expressions
                when (y) {
                    is Number -> {
                        if (y.value == 0.0){Number(0.0)} // 0^N = 0
                        else if(y.value == 1.0) {Number(1.0)} //1^N = 1
                        else{Power(y,x)}
                    }
                    is Variable -> {Power(y,x)}
                    is Power -> {Power(y.left, Multiply(y.right, x))}
                    else -> {Power(y,x)}
                }
            }
        }
    }

}