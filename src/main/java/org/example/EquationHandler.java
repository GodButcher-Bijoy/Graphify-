package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationHandler {

    // ⚠️ FIXED: Added 't' for parametric, and missing exp4j math functions
    private static final Set<String> RESERVED_WORDS = Set.of(
            "x", "y", "t", "sin", "cos", "tan", "asin", "acos", "atan",
            "log", "log10", "log2", "sqrt", "cbrt", "abs", "pi", "e", "exp",
            "sinh", "cosh", "tanh", "signum", "ceil", "floor"
    );

    // "2x" কে "2*x" এ কনভার্ট করার লজিক
    public static String formatEquation(String eqStr) {
        String eq = eqStr.toLowerCase().replace(" ", "");

        // ⚠️ FIXED: Better implicit multiplication support
        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2"); // 2x -> 2*x
        eq = eq.replaceAll("(\\d)(\\()", "$1*$2");   // 2(x) -> 2*(x)
        eq = eq.replaceAll("(\\))(\\()", "$1*$2");   // (x)(y) -> (x)*(y)

        return eq;
    }

    // ইকুয়েশন থেকে স্লাইডারের জন্য ভেরিয়েবল (a, b, c ইত্যাদি) বের করার লজিক
    public static Set<String> extractVariables(String eq) {
        Set<String> foundVars = new HashSet<>();

        // Match whole alphabetic tokens.
        // If the token is a known math keyword → skip all its letters entirely.
        // Otherwise → extract each individual letter that is not itself a reserved word.
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(eq);

        while (m.find()) {
            String token = m.group();
            if (RESERVED_WORDS.contains(token)) {
                // e.g. "sin", "cos", "pi", "e", "x", "y" → skip
                continue;
            }
            // Not a reserved word: offer individual letters as slider candidates.
            for (char c : token.toCharArray()) {
                String letter = String.valueOf(c);
                if (!RESERVED_WORDS.contains(letter)) {
                    foundVars.add(letter);
                }
            }
        }
        return foundVars;
    }

    // স্ট্যান্ডার্ড বা ইনভার্স ইকুয়েশন (শুধু x অথবা y) এর জন্য Expression তৈরি
    public static Expression buildExpression(String function, String independentVar, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable(independentVar);

        for (String var : globalVariables.keySet()) {
            // ⚠️ FIX: Prevent exp4j error by not re-declaring the independent variable
            if (!var.equals(independentVar)) {
                builder.variable(var);
            }
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals(independentVar)) {
                expr.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return expr;
    }

    // ইমপ্লিসিট ইকুয়েশন (x ও y দুটোই থাকলে) এর জন্য Expression তৈরি
    public static Expression buildImplicitExpression(String function, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variables("x", "y");

        for (String var : globalVariables.keySet()) {
            if (!var.equals("x") && !var.equals("y")) {
                builder.variable(var);
            }
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals("x") && !entry.getKey().equals("y")) {
                expr.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return expr;
    }
}