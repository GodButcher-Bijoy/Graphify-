package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.Map;

public class FunctionPlotter {
    private final AppState appState;
    private final GraphicsContext gc;

    public FunctionPlotter(AppState appState, GraphicsContext gc) {
        this.appState = appState;
        this.gc = gc;
    }


    public void plotEquation(String eqStr, Color color, double cx, double cy, double width, double height, BoundaryCondition boundary) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);

        String eq = EquationHandler.formatEquation(eqStr);

        try {
            if (eq.startsWith("y=") && !eq.contains("x=")) {
                // Canonical explicit: y = f(x)
                plotStandard(cx, cy, width, height, eq.substring(2), boundary);
            } else if (eq.startsWith("x=") && !eq.contains("y=")) {
                // Canonical inverse: x = f(y)
                plotInverse(cx, cy, width, height, eq.substring(2), boundary);
            } else if (eq.contains("=")) {
                // Try to rewrite as explicit y = f(x) before falling back to slow implicit
                String rerouted = tryRewriteAsExplicit(eq);
                if (rerouted != null) {
                    plotStandard(cx, cy, width, height, rerouted, boundary);
                } else {
                    plotImplicit(cx, cy, width, height, eq, boundary);
                }
            } else {
                plotStandard(cx, cy, width, height, eq, boundary);
            }
        } catch (Exception e) {}
    }

    /**
     * Attempts to rewrite an implicit equation as an explicit y = f(x).
     * Handles two cases:
     *   1. "expr = y"        → returns expr  (y is isolated on the RHS)
     *   2. "expr - y = 0"    → returns expr  (y appears as -y on LHS, RHS is 0)
     *      "expr + (-1)*y = 0" — same idea
     *
     * Returns null if the equation cannot be trivially solved for y.
     */
    private String tryRewriteAsExplicit(String eq) {
        // Split on the first '='
        int eqIdx = eq.indexOf('=');
        if (eqIdx < 0) return null;
        String lhs = eq.substring(0, eqIdx).trim();
        String rhs = eq.substring(eqIdx + 1).trim();

        // ── Case 1: expr = y  (RHS is exactly "y", no other y on LHS) ─────────
        if (rhs.equals("y") && !lhs.contains("y")) {
            return lhs;   // plot lhs as f(x)
        }

        // ── Case 2: y = expr  already handled above, but catch rhs forms ──────
        if (lhs.equals("y") && !rhs.contains("y")) {
            return rhs;
        }

        // ── Case 3: "expr - y = 0"  →  "expr" (LHS ends with "-y", RHS = "0") ─
        if (rhs.equals("0")) {
            // Check if lhs ends with "-y" (possibly with surrounding whitespace)
            if (lhs.endsWith("-y") && !lhs.substring(0, lhs.length() - 2).contains("y")) {
                return lhs.substring(0, lhs.length() - 2).trim();
            }
            // Check "+(-y)" or "-(y)" patterns
            if (lhs.endsWith("+(-y)") && !lhs.substring(0, lhs.length() - 5).contains("y")) {
                return lhs.substring(0, lhs.length() - 5).trim();
            }
        }

        // ── Case 4: "expr - y = c"  →  rewrite as "expr - c" ──────────────────
        if (lhs.endsWith("-y") && !lhs.substring(0, lhs.length() - 2).contains("y")) {
            // expr - y = rhs  →  y = expr - rhs
            String exprPart = lhs.substring(0, lhs.length() - 2).trim();
            // Only safe if rhs contains no y
            if (!rhs.contains("y")) {
                return "(" + exprPart + ")-(" + rhs + ")";
            }
        }

        return null;  // cannot simplify — fall back to implicit
    }

    private void plotStandard(double cx, double cy, double width, double height, String function, BoundaryCondition boundary) {
        Expression expr = EquationHandler.buildExpression(function, "x", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;
        double step = .1;
        double prevMathY = Double.NaN;

        for (double screenX = 0; screenX < width; screenX += step) {
            double mathX = (screenX - cx) / appState.getScale();
            expr.setVariable("x", mathX);

            double mathY;
            try {
                mathY = expr.evaluate();
            } catch (Exception e) {
                first = true;
                prevMathY = Double.NaN;
                continue;
            }

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                first = true;
                prevMathY = Double.NaN;
                continue;
            }


            if (boundary != null && !boundary.test(mathX, mathY, 0)) {
                first = true;
                prevMathY = Double.NaN;
                continue;
            }

            double screenY = cy - (mathY * appState.getScale());
            double clampedY = screenY;
            if (screenY < -100000) clampedY = -100000;
            if (screenY > height + 100000) clampedY = height + 100000;

            if (first) {
                gc.moveTo(screenX, clampedY);
                first = false;
            } else {
                if (!Double.isNaN(prevMathY) && Math.abs(mathY - prevMathY) > (height / appState.getScale()) * 2) {
                    gc.stroke();
                    gc.beginPath();
                    gc.moveTo(screenX, clampedY);
                } else {
                    gc.lineTo(screenX, clampedY);
                }
            }
            prevMathY = mathY;
        }
        gc.stroke();
    }

    private void plotInverse(double cx, double cy, double width, double height, String function, BoundaryCondition boundary) {
        Expression expr = EquationHandler.buildExpression(function, "y", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;
        double step = .1;
        double prevMathX = Double.NaN;

        for (double screenY = 0; screenY < height; screenY += step) {
            double mathY = (cy - screenY) / appState.getScale();
            expr.setVariable("y", mathY);

            double mathX;
            try {
                mathX = expr.evaluate();
            } catch (Exception e) {
                first = true;
                prevMathX = Double.NaN;
                continue;
            }

            if (Double.isNaN(mathX) || Double.isInfinite(mathX)) {
                first = true;
                prevMathX = Double.NaN;
                continue;
            }


            if (boundary != null && !boundary.test(mathX, mathY, 0)) {
                first = true;
                prevMathX = Double.NaN;
                continue;
            }

            double screenX = cx + (mathX * appState.getScale());
            double clampedX = Math.max(-100000, Math.min(width + 100000, screenX));

            if (first) {
                gc.moveTo(clampedX, screenY);
                first = false;
            } else {
                if (!Double.isNaN(prevMathX) && Math.abs(mathX - prevMathX) > (width / appState.getScale()) * 2) {
                    gc.stroke();
                    gc.beginPath();
                    gc.moveTo(clampedX, screenY);
                } else {
                    gc.lineTo(clampedX, screenY);
                }
            }
            prevMathX = mathX;
        }
        gc.stroke();
    }

    private void plotImplicit(double cx, double cy, double width, double height, String eq, BoundaryCondition boundary) {
        gc.setLineWidth(2.0);
        String[] parts = eq.split("=");
        String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq;

        Expression expr = EquationHandler.buildImplicitExpression(expressionStr, appState.getGlobalVariables());

        int res = 4;
        for (double x = 0; x < width; x += res) {
            for (double y = 0; y < height; y += res) {

                if (boundary != null) {
                    double mathX = (x + res / 2.0 - cx) / appState.getScale();
                    double mathY = (cy - (y + res / 2.0)) / appState.getScale();
                    if (!boundary.test(mathX, mathY, 0)) continue;
                }

                double vBL = evaluate(expr, x, y + res, cx, cy, appState.getScale());
                double vBR = evaluate(expr, x + res, y + res, cx, cy, appState.getScale());
                double vTR = evaluate(expr, x + res, y, cx, cy, appState.getScale());
                double vTL = evaluate(expr, x, y, cx, cy, appState.getScale());

                double limit = 1e10;
                if (Math.abs(vBL) > limit || Math.abs(vBR) > limit || Math.abs(vTR) > limit || Math.abs(vTL) > limit) continue;

                double[][][] hits = new double[4][2][];
                int hitCount = 0;

                if (isSignDifferent(vBL, vTL)) hits[hitCount++] = new double[][]{{x, y + res - (-vBL / (vTL - vBL) * res)}};
                if (isSignDifferent(vBL, vBR)) hits[hitCount++] = new double[][]{{x + (-vBL / (vBR - vBL) * res), y + res}};
                if (isSignDifferent(vBR, vTR)) hits[hitCount++] = new double[][]{{x + res, y + res - (-vBR / (vTR - vBR) * res)}};
                if (isSignDifferent(vTR, vTL)) hits[hitCount++] = new double[][]{{x + res - (-vTR / (vTL - vTR) * res), y}};

                if (hitCount == 2) gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                else if (hitCount == 4) {
                    gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                    gc.strokeLine(hits[2][0][0], hits[2][0][1], hits[3][0][0], hits[3][0][1]);
                }
            }
        }
    }


    // Overload for polar equations — caller specifies t range (e.g. 0 to 8π)
    public void plotParametric(String xEq, String yEq, Color color,
                               double cx, double cy, double width, double height,
                               BoundaryCondition boundary, double tStart, double tEnd) {
        plotParametricCore(xEq, yEq, color, cx, cy, width, height, boundary, tStart, tEnd);
    }

    // Original signature — regular parametric curves use t = -100..100
    public void plotParametric(String xEq, String yEq, Color color,
                               double cx, double cy, double width, double height,
                               BoundaryCondition boundary) {
        plotParametricCore(xEq, yEq, color, cx, cy, width, height, boundary, -100, 100);
    }

    private void plotParametricCore(String xEq, String yEq, Color color,
                                    double cx, double cy, double width, double height,
                                    BoundaryCondition boundary, double tStart, double tEnd) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);
        gc.beginPath();

        try {
            Expression exprX = EquationHandler.buildExpression(xEq, "t", appState.getGlobalVariables());
            Expression exprY = EquationHandler.buildExpression(yEq, "t", appState.getGlobalVariables());

            double tStep  = 0.02; // finer step → smoother curves

            // How far outside the canvas we still allow a point to be drawn.
            // Beyond this the value is effectively tending to ±infinity → lift the pen.
            double margin    = Math.max(width, height) * 3.0;

            // A jump larger than this in SCREEN PIXELS between two consecutive points
            // means there is a real discontinuity / asymptote → lift the pen.
            // Using screen-space (not math-space) so the check is zoom-independent.
            double jumpLimit = Math.hypot(width, height) * 1.5;

            boolean pathStarted = false;
            double prevSX = Double.NaN;
            double prevSY = Double.NaN;

            for (double t = tStart; t <= tEnd; t += tStep) {
                double mathX, mathY;
                try {
                    exprX.setVariable("t", t);
                    exprY.setVariable("t", t);
                    mathX = exprX.evaluate();
                    mathY = exprY.evaluate();
                } catch (Exception e) {
                    // evaluation error → treat as discontinuity, lift the pen
                    pathStarted = false;
                    prevSX = prevSY = Double.NaN;
                    continue;
                }

                // NaN or ±Infinity from the expression itself → discontinuity
                if (!Double.isFinite(mathX) || !Double.isFinite(mathY)) {
                    pathStarted = false;
                    prevSX = prevSY = Double.NaN;
                    continue;
                }

                // Boundary condition check
                if (boundary != null && !boundary.test(mathX, mathY, t)) {
                    pathStarted = false;
                    prevSX = prevSY = Double.NaN;
                    continue;
                }

                double sx = cx + mathX * appState.getScale();
                double sy = cy - mathY * appState.getScale();

                // If the screen point is beyond the allowed margin the math value is
                // tending toward ±infinity → lift the pen instead of clamping & drawing.
                boolean atInfinity = sx < -margin || sx > width  + margin
                        || sy < -margin || sy > height + margin;
                if (atInfinity) {
                    pathStarted = false;
                    prevSX = prevSY = Double.NaN;
                    continue;
                }

                if (!pathStarted) {
                    gc.moveTo(sx, sy);
                    pathStarted = true;
                } else {
                    // Screen-space jump: zoom-independent discontinuity / asymptote detection
                    double screenJump = Math.hypot(sx - prevSX, sy - prevSY);
                    if (screenJump > jumpLimit) {
                        // Real discontinuity → close current sub-path and start a new one
                        gc.stroke();
                        gc.beginPath();
                        gc.moveTo(sx, sy);
                    } else {
                        gc.lineTo(sx, sy);
                    }
                }

                prevSX = sx;
                prevSY = sy;
            }
            gc.stroke();
        } catch (Exception e) {
            // silently ignore malformed equations
        }
    }

    public void plotInequality(String lhs, String op, String rhs, Color color, double cx, double cy, double width, double height, BoundaryCondition boundary) {
        // Build diff expression: lhs - (rhs)
        String diffStr = EquationHandler.formatEquation(lhs + "-(" + rhs + ")");
        try {
            Expression expr = EquationHandler.buildImplicitExpression(diffStr, appState.getGlobalVariables());

            // Shade satisfied region with semi-transparent color
            Color shadeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.50);
            gc.setFill(shadeColor);

            int res = 3;
            for (double px = 0; px < width; px += res) {
                for (double py = 0; py < height; py += res) {
                    double mathX = (px + res / 2.0 - cx) / appState.getScale();
                    double mathY = (cy - (py + res / 2.0)) / appState.getScale();

                    if (boundary != null && !boundary.test(mathX, mathY, 0)) continue;

                    try {
                        expr.setVariable("x", mathX);
                        expr.setVariable("y", mathY);
                        double val = expr.evaluate();
                        if (Double.isNaN(val) || Double.isInfinite(val)) continue;

                        boolean satisfied;
                        switch (op) {
                            case "<":  satisfied = val < 0;  break;
                            case "<=": satisfied = val <= 0; break;
                            case ">":  satisfied = val > 0;  break;
                            case ">=": satisfied = val >= 0; break;
                            default:   satisfied = false;
                        }

                        if (satisfied) gc.fillRect(px, py, res, res);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        // Draw the boundary curve (equality line) in solid color on top
        String boundaryEq = lhs + "=" + rhs;
        plotEquation(boundaryEq, color, cx, cy, width, height, boundary);
    }

    public void plotCompoundInequality(String left, String op1, String mid, String op2, String right, Color color, double cx, double cy, double width, double height, BoundaryCondition boundary) {
        String diffStr1 = EquationHandler.formatEquation(left + "-(" + mid + ")");
        String diffStr2 = EquationHandler.formatEquation(mid + "-(" + right + ")");
        try {
            Expression expr1 = EquationHandler.buildImplicitExpression(diffStr1, appState.getGlobalVariables());
            Expression expr2 = EquationHandler.buildImplicitExpression(diffStr2, appState.getGlobalVariables());

            Color shadeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.50);
            gc.setFill(shadeColor);

            int res = 3;
            for (double px = 0; px < width; px += res) {
                for (double py = 0; py < height; py += res) {
                    double mathX = (px + res / 2.0 - cx) / appState.getScale();
                    double mathY = (cy - (py + res / 2.0)) / appState.getScale();

                    if (boundary != null && !boundary.test(mathX, mathY, 0)) continue;

                    try {
                        expr1.setVariable("x", mathX); expr1.setVariable("y", mathY);
                        double val1 = expr1.evaluate();
                        if (Double.isNaN(val1) || Double.isInfinite(val1)) continue;

                        expr2.setVariable("x", mathX); expr2.setVariable("y", mathY);
                        double val2 = expr2.evaluate();
                        if (Double.isNaN(val2) || Double.isInfinite(val2)) continue;

                        boolean sat1, sat2;
                        switch (op1) {
                            case "<":  sat1 = val1 < 0;  break;
                            case "<=": sat1 = val1 <= 0; break;
                            case ">":  sat1 = val1 > 0;  break;
                            case ">=": sat1 = val1 >= 0; break;
                            default:   sat1 = false;
                        }
                        switch (op2) {
                            case "<":  sat2 = val2 < 0;  break;
                            case "<=": sat2 = val2 <= 0; break;
                            case ">":  sat2 = val2 > 0;  break;
                            case ">=": sat2 = val2 >= 0; break;
                            default:   sat2 = false;
                        }

                        if (sat1 && sat2) gc.fillRect(px, py, res, res);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        plotEquation(left + "=" + mid, color, cx, cy, width, height, boundary);
        plotEquation(mid + "=" + right, color, cx, cy, width, height, boundary);
    }

    private double evaluate(Expression expr, double screenX, double screenY, double cx, double cy, double scale) {
        double epsilon = 0.0001;
        expr.setVariable("x", (screenX - cx + epsilon) / scale);
        expr.setVariable("y", (cy - screenY + epsilon) / scale);
        try { return expr.evaluate(); } catch (Exception e) { return Double.NaN; }
    }

    private boolean isSignDifferent(double v1, double v2) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return (v1 > 0 && v2 <= 0) || (v1 <= 0 && v2 > 0);
    }


    public static class BoundaryCondition {
        private class SingleCondition {
            Expression leftExpr, rightExpr;
            String op;

            SingleCondition(String leftStr, String rightStr, String operator, Map<String, Double> globals) {
                this.op = operator;
                leftExpr = buildExpr(EquationHandler.formatEquation(leftStr), globals);
                rightExpr = buildExpr(EquationHandler.formatEquation(rightStr), globals);
            }

            boolean test(double x, double y, double t) {
                if (op == null || leftExpr == null || rightExpr == null) return true;
                try {
                    leftExpr.setVariable("x", x).setVariable("y", y).setVariable("t", t);
                    rightExpr.setVariable("x", x).setVariable("y", y).setVariable("t", t);
                    double l = leftExpr.evaluate();
                    double r = rightExpr.evaluate();
                    switch (op) {
                        case "<": return l < r;
                        case "<=": return l <= r;
                        case ">": return l > r;
                        case ">=": return l >= r;
                        default: return true;
                    }
                } catch (Exception e) { return true; }
            }
        }

        private java.util.List<SingleCondition> conditions = new java.util.ArrayList<>();

        public BoundaryCondition(String condStr, Map<String, Double> globals) {
            String[] parts = condStr.split(",");
            for (String p : parts) {
                p = p.trim();
                if (!p.isEmpty()) {
                    parseAndAddCondition(p, globals);
                }
            }
        }

        private void parseAndAddCondition(String p, Map<String, Double> globals) {
            java.util.List<Integer> opIndices = new java.util.ArrayList<>();
            java.util.List<String> ops = new java.util.ArrayList<>();

            for (int i = 0; i < p.length(); i++) {
                if (p.startsWith("<=", i) || p.startsWith(">=", i)) {
                    opIndices.add(i);
                    ops.add(p.substring(i, i + 2));
                    i++;
                } else if (p.charAt(i) == '<' || p.charAt(i) == '>') {
                    opIndices.add(i);
                    ops.add(String.valueOf(p.charAt(i)));
                }
            }

            if (ops.size() == 1) {
                String left = p.substring(0, opIndices.get(0)).trim();
                String right = p.substring(opIndices.get(0) + ops.get(0).length()).trim();
                conditions.add(new SingleCondition(left, right, ops.get(0), globals));

            } else if (ops.size() == 2) {
                String left = p.substring(0, opIndices.get(0)).trim();
                String mid = p.substring(opIndices.get(0) + ops.get(0).length(), opIndices.get(1)).trim();
                String right = p.substring(opIndices.get(1) + ops.get(1).length()).trim();

                conditions.add(new SingleCondition(left, mid, ops.get(0), globals));
                conditions.add(new SingleCondition(mid, right, ops.get(1), globals));
            }
        }

        private Expression buildExpr(String e, Map<String, Double> globals) {
            try {
                ExpressionBuilder builder = EquationHandler.withCustomFunctions(
                        new ExpressionBuilder(e).variables("x", "y", "t")
                );
                for (String k : globals.keySet()) builder.variable(k);
                Expression expr = builder.build();
                for (Map.Entry<String, Double> entry : globals.entrySet()) {
                    expr.setVariable(entry.getKey(), entry.getValue());
                }
                return expr;
            } catch (Exception ex) { return null; }
        }

        public boolean test(double x, double y, double t) {
            for (SingleCondition c : conditions) {
                if (!c.test(x, y, t)) {
                    return false;
                }
            }
            return true;
        }
    }
}