import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SampleTest {
    private val testPolynomials :Polynomials  = Polynomials()

    @Test
    fun testParse(){
        val expected = ArrayDeque(listOf("3","4","2","*","1","5","-","2","3","^","^","/","+"))
        val expected2 = ArrayDeque(listOf("3","4","2","1","-","*","+"))
        //testing shunting-Yard Algorithm
        assertEquals(expected, testPolynomials.shuntingYard("3+4*2/(1-5)^2^3"))
        assertEquals(expected2, testPolynomials.shuntingYard("3+4*(2-1)"))
        //testing parse
        assertEquals(7, testPolynomials.parse("3+4*(2-1)"))
        assertEquals(-29, testPolynomials.parse("5-6*18/3+2"))
        assertEquals(217, testPolynomials.parse("10*20-9/3+20"))
        assertEquals(8900, testPolynomials.parse("10^3*9-100"))

        //testing simplify
        assertEquals("7-x", testPolynomials.simplify("5-x*(3/3)+2"))
        assertEquals("x+2", testPolynomials.simplify("1*x-0/3+2"))
        assertEquals("17+x", testPolynomials.simplify("5+2*6+x"))

        //testing derivation
        assertEquals("2*x", testPolynomials.differentiate("x^2"))
        assertEquals("1+2*x+3*x^2", testPolynomials.differentiate("x+x^2+x^3"))
        assertEquals("2",testPolynomials.differentiate("(x*2*x)/x"))
        assertEquals("4*x^3+6*x^2-2*x+5+1/x^2", testPolynomials.differentiate("x^4+2*x^3-x^2+5*x-1/x"))
        assertEquals("12*x^2+12*x-2-2/x^3",testPolynomials.differentiate("4*x^3+6*x^2-2*x+5+1/x^2"))

        //testing print
    }
}