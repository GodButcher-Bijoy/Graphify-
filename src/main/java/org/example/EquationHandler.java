package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationHandler {

    // ─── Custom trig functions exp4j does not provide natively ───────────────
    private static final Function SEC = new Function("sec", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.cos(args[0]); }
    };
    private static final Function CSC = new Function("csc", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.sin(args[0]); }
    };
    // "cosec" is the alternate spelling used in many textbooks
    private static final Function COSEC = new Function("cosec", 1) {
        @Override public double apply(double... args) { return 1.0 / Math.sin(args[0]); }
    };
    private static final Function COT = new Function("cot", 1) {
        @Override public double apply(double... args) { return Math.cos(args[0]) / Math.sin(args[0]); }
    };

    // ─── Real-valued power operator — fixes x^(2/3) being NaN for x < 0 ─────
    //
    // ROOT CAUSE: Java's Math.pow(-2, 0.6667) returns NaN because the JVM does
    // not attempt rational-root interpretation for negative bases.
    // Mathematically (-2)^(2/3) = ((-2)²)^(1/3) = 4^(1/3) ≈ 1.587 — fully real.
    //
    // FIX: Override exp4j's built-in ^ with this operator.
    // For a negative base we find the best rational p/q (q ≤ 100) for the exponent:
    //   • Odd q  → real root exists: (-1)^p · |base|^(p/q)
    //   • Even q → complex only (e.g. (-1)^0.5 = i) → NaN, graph lifts pen
    // Positive bases use Math.pow unchanged.
    private static final Operator REAL_POW = new Operator(
            "^", 2, false, Operator.PRECEDENCE_POWER) {
        @Override
        public double apply(double... args) {
            double base = args[0];
            double exp  = args[1];
            if (Double.isNaN(base) || Double.isNaN(exp)) return Double.NaN;
            if (base >= 0) return Math.pow(base, exp);   // normal path unchanged

            // base < 0: search for rational approximation of exp = p/q
            for (int q = 1; q <= 100; q++) {
                long p = Math.round(exp * q);
                if (Math.abs(exp - (double) p / q) < 1e-9) {
                    if (q % 2 == 1) {                    // odd denominator → real
                        double sign = (p % 2 == 0) ? 1.0 : -1.0;
                        return sign * Math.pow(-base, exp);
                    } else {
                        return Double.NaN;               // even denominator → complex
                    }
                }
            }
            return Double.NaN; // irrational exponent, no real result for negative base
        }
    };

    // All names that must NOT be treated as slider variables
    private static final Set<String> RESERVED_WORDS = Set.of(
            "x", "y", "t", "r",
            "sin",  "cos",  "tan",
            "asin", "acos", "atan",
            "sec",  "csc",  "cosec", "cot",
            "log",  "log10","log2",
            "sqrt", "cbrt", "abs",
            "pi",   "e",    "exp",
            "sinh", "cosh", "tanh",
            "signum", "ceil", "floor"
    );

    // ─── Helpers to attach custom functions to every builder ─────────────────
    /** Registers sec/csc/cosec/cot AND the real-power ^ operator on any builder.
     *  Public so BoundaryCondition inside FunctionPlotter can reuse the same set. */
    public static ExpressionBuilder withCustomFunctions(ExpressionBuilder builder) {
        return builder.functions(SEC, CSC, COSEC, COT).operator(REAL_POW);
    }

    // ─── Reverse unicode pretty-print → raw ASCII (used by parser) ──────────
    /**
     * Converts every Unicode pretty-print character produced by autoFormatEquation()
     * back to the ASCII form that exp4j expects.  Safe to call on already-ASCII text.
     */
    public static String reverseAutoFormat(String text) {
        if (text == null || text.isEmpty()) return text;
        // Superscript digits → ^n  (must come before other replacements)
        text = text.replace("⁰","^0").replace("¹","^1").replace("²","^2")
                .replace("³","^3").replace("⁴","^4").replace("⁵","^5")
                .replace("⁶","^6").replace("⁷","^7").replace("⁸","^8")
                .replace("⁹","^9");
        // Unicode symbols → ASCII sequences
        text = text.replace("√(", "sqrt(");
        text = text.replace("π",  "pi");
        text = text.replace("≤",  "<=");
        text = text.replace("≥",  ">=");
        text = text.replace("·",  "*");
        return text;
    }

    // ─── "2x" → "2*x" implicit multiplication ────────────────────────────────
    public static String formatEquation(String eqStr) {
        // Reverse any unicode pretty-print chars first so the parser sees plain ASCII
        String eq = reverseAutoFormat(eqStr).toLowerCase().replace(" ", "");

        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2"); // 2x   -> 2*x
        eq = eq.replaceAll("(\\d)(\\()",   "$1*$2"); // 2(x) -> 2*(x)
        eq = eq.replaceAll("(\\))(\\()",   "$1*$2"); // (x)(y)-> (x)*(y)

        return eq;
    }

    // ─── Extract free variables for slider generation ─────────────────────────
    public static Set<String> extractVariables(String eq) {
        Set<String> foundVars = new HashSet<>();

        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(eq);

        while (m.find()) {
            String token = m.group();
            if (RESERVED_WORDS.contains(token)) continue;

            for (char c : token.toCharArray()) {
                String letter = String.valueOf(c);
                if (!RESERVED_WORDS.contains(letter)) {
                    foundVars.add(letter);
                }
            }
        }
        return foundVars;
    }

    // ─── Standard / inverse (one independent variable) ───────────────────────
    public static Expression buildExpression(String function, String independentVar,
                                             Map<String, Double> globalVariables) {
        ExpressionBuilder builder = withCustomFunctions(
                new ExpressionBuilder(function).variable(independentVar)
        );

        for (String var : globalVariables.keySet()) {
            if (!var.equals(independentVar)) builder.variable(var);
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals(independentVar))
                expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }

    // ─── Implicit (both x and y present) ─────────────────────────────────────
    public static Expression buildImplicitExpression(String function,
                                                     Map<String, Double> globalVariables) {
        ExpressionBuilder builder = withCustomFunctions(
                new ExpressionBuilder(function).variables("x", "y")
        );

        for (String var : globalVariables.keySet()) {
            if (!var.equals("x") && !var.equals("y")) builder.variable(var);
        }

        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            if (!entry.getKey().equals("x") && !entry.getKey().equals("y"))
                expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }
}