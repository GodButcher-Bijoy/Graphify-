import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationHandler {

    private static final Set<String> RESERVED_WORDS = Set.of("x", "y", "sin", "cos", "tan", "log", "sqrt", "abs", "pi", "e", "exp");

    // "2x" কে "2*x" এ কনভার্ট করার লজিক
    public static String formatEquation(String eqStr) {
        String eq = eqStr.toLowerCase().replace(" ", "");
        return eq.replaceAll("(\\d)([a-z])", "$1*$2");
    }

    // ইকুয়েশন থেকে স্লাইডারের জন্য ভেরিয়েবল (a, b, c ইত্যাদি) বের করার লজিক
    public static Set<String> extractVariables(String eq) {
        Set<String> foundVars = new HashSet<>();
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(eq.toLowerCase());

        while (m.find()) {
            String var = m.group();
            if (!RESERVED_WORDS.contains(var)) {
                foundVars.add(var);
            }
        }
        return foundVars;
    }

    // স্ট্যান্ডার্ড বা ইনভার্স ইকুয়েশন (শুধু x অথবা y) এর জন্য Expression তৈরি
    public static Expression buildExpression(String function, String independentVar, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable(independentVar);
        for (String var : globalVariables.keySet()) {
            builder.variable(var);
        }
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }

    // ইমপ্লিসিট ইকুয়েশন (x ও y দুটোই থাকলে) এর জন্য Expression তৈরি
    public static Expression buildImplicitExpression(String function, Map<String, Double> globalVariables) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variables("x", "y");
        for (String var : globalVariables.keySet()) {
            builder.variable(var);
        }
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) {
            expr.setVariable(entry.getKey(), entry.getValue());
        }
        return expr;
    }
}