package com.example.app.calculator;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import com.example.app.exception.CalculatorException;

public class ArithmeticEvaluatorTest {
    private ArithmeticEvaluator evaluator;

    @BeforeMethod
    public void setUp() {
        evaluator = new ArithmeticEvaluator();
    }

    @DataProvider(name = "basicExpressions")
    public Object[][] basicExpressionsProvider() {
        return new Object[][] {
            {"2 + 3 * 4", 14.0},
            {"(10 + 5) / 3", 5.0},
            {"2 * (3 + 4) - 1", 13.0},
            {"-5 + 3", -2.0},
            {"2.5 + 1.5", 4.0}
        };
    }

    @Test(dataProvider = "basicExpressions", description = "Test basic arithmetic expressions")
    public void testBasicExpressions(String expression, double expected) {
        Assert.assertEquals(evaluator.evaluate(expression), expected, 0.0001);
    }

    @Test(description = "Test nested parentheses")
    public void testNestedParentheses() {
        Assert.assertEquals(evaluator.evaluate("3 * (2 + (3 * 2))"), 24.0, 0.0001);
    }

    @Test(description = "Test division by zero", expectedExceptions = CalculatorException.class)
    public void testDivisionByZero() {
        evaluator.evaluate("5 / 0");
    }

    @Test(description = "Test empty expression", expectedExceptions = CalculatorException.class)
    public void testEmptyExpression() {
        evaluator.evaluate("");
    }

    @Test(description = "Test null expression", expectedExceptions = CalculatorException.class)
    public void testNullExpression() {
        evaluator.evaluate(null);
    }

    @Test(description = "Test invalid expression", expectedExceptions = CalculatorException.class)
    public void testInvalidExpression() {
        evaluator.evaluate("2 + * 3");
    }

    @Test(description = "Test missing parenthesis", expectedExceptions = CalculatorException.class)
    public void testMissingParenthesis() {
        evaluator.evaluate("(2 + 3 * 4");
    }

    @Test(description = "Test decimal numbers")
    public void testDecimalNumbers() {
        Assert.assertEquals(evaluator.evaluate("3.14159"), 3.14159, 0.0001);
    }

    @Test(description = "Test operator precedence")
    public void testOperatorPrecedence() {
        Assert.assertEquals(evaluator.evaluate("2 + 3 * 4"), 14.0, 0.0001);
        Assert.assertEquals(evaluator.evaluate("(2 + 3) * 4"), 20.0, 0.0001);
    }

    @Test(description = "Test complex expression")
    public void testComplexExpression() {
        Assert.assertEquals(evaluator.evaluate("(2.5 + 3.5) * 2 - 1.5"), 10.5, 0.0001);
    }
}
