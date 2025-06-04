package com.example.app.calculator;

import com.example.app.exception.CalculatorException;
import java.util.*;

public class ArithmeticEvaluator {
    public double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new CalculatorException("Expression cannot be empty");
        }
        try {
            return new ExpressionParser(expression.trim()).parse();
        } catch (CalculatorException e) {
            throw e;
        } catch (Exception e) {
            throw new CalculatorException("Invalid expression: " + e.getMessage());
        }
    }

    private static class ExpressionParser {
        private final String expression;
        private int position;

        public ExpressionParser(String expression) {
            this.expression = expression;
            this.position = 0;
        }

        public double parse() {
            // recursively parses the expression. Stops upon encountering an unfamiliar char
            double result = parseExpression();

            // this catches unacceptable chars after the check for '(', '+', '-', '*', '/')
            if (position < expression.length()) {
                throw new CalculatorException("Unexpected character: " + expression.charAt(position));
            }
            return result;
        }

        private double parseExpression() {
            // expression always starts with a number.
            // If it does not, break down the brackets and modifiers (+,-) with recursion,
            // until it does
            double result = parseTerm();

            // the idea is to break the expression into bracketed segments, which are
            // then broken into numbers with accompanying modifiers (+,-) and operators (*,/,+,-)
            while (position < expression.length()) {
                skipWhitespace();
                char operator = peekChar();
                if (operator != '+' && operator != '-') {
                    break;
                }
                position++;
                double term = parseTerm();
                if (operator == '+') {
                    result += term;
                } else {
                    result -= term;
                }
            }

            return result;
        }

        private double parseTerm() {
            // expression always starts with a number.
            // If it does not, break down the brackets and modifiers (+,-) with recursion,
            // until it does
            double result = parseFactor();

            while (position < expression.length()) {
                skipWhitespace();
                char operator = peekChar();
                if (operator != '*' && operator != '/') {
                    break;
                }
                position++;
                double factor = parseFactor();
                if (operator == '*') {
                    result *= factor;
                } else {
                    if (factor == 0) {
                        throw new CalculatorException("Division by zero");
                    }
                    result /= factor;
                }
            }

            return result;
        }

        private double parseFactor() {
            skipWhitespace();
            char ch = peekChar();

            // this breaks the expression into brackets, recourses to the beginning parseExpression
            if (ch == '(') {
                position++;
                double result = parseExpression();
                skipWhitespace();
                if (position >= expression.length() || expression.charAt(position) != ')') {
                    throw new CalculatorException("Missing closing parenthesis");
                }
                position++;
                return result;
            }

            // the following two ifs consume modifiers (+,-) using recursive number search
            if (ch == '-') {
                position++;
                return -parseFactor();
            }

            if (ch == '+') {
                position++;
                return parseFactor();
            }

            return parseNumber();
        }

        private double parseNumber() {
            skipWhitespace();
            int startPos = position;
            boolean hasDecimal = false;

            // Handle digits before decimal
            while (position < expression.length() && 
                   (Character.isDigit(expression.charAt(position)) || 
                    expression.charAt(position) == '.')) {
                if (expression.charAt(position) == '.') {
                    if (hasDecimal) {
                        throw new CalculatorException("Invalid number format: multiple decimal points");
                    }
                    hasDecimal = true;
                }
                position++;
            }

            if (startPos == position) {
                throw new CalculatorException("Expected number at position " + position);
            }

            try {
                return Double.parseDouble(expression.substring(startPos, position));
            } catch (NumberFormatException e) {
                throw new CalculatorException("Invalid number format");
            }
        }

        private void skipWhitespace() {
            while (position < expression.length() &&
                   Character.isWhitespace(expression.charAt(position))) {
                position++;
            }
        }

        private char peekChar() {
            if (position >= expression.length()) {
                throw new CalculatorException("Unexpected end of expression");
            }
            return expression.charAt(position);
        }
    }
}
