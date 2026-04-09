package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.objecthunter.exp4j.Expression;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphRenderer {
    private final AppState appState;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final VBox functionContainer;

    // Helper classes
    private final AxesRenderer axesRenderer;
    private final FunctionPlotter functionPlotter;

    // For panning (dragging) the graph
    private double lastMouseX;
    private double lastMouseY;

    public GraphRenderer(AppState appState, Canvas canvas, VBox functionContainer) {
        this.appState = appState;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.functionContainer = functionContainer;

        this.axesRenderer = new AxesRenderer(appState, gc);
        this.functionPlotter = new FunctionPlotter(appState, gc);

        this.canvas.widthProperty().addListener(evt -> drawGraph());
        this.canvas.heightProperty().addListener(evt -> drawGraph());

        this.canvas.setOnScroll(this::handleZoom);

        this.canvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        this.canvas.setOnMouseDragged(e -> {
            double deltaX = e.getX() - lastMouseX;
            double deltaY = e.getY() - lastMouseY;
            appState.setOffsetX(appState.getOffsetX() + deltaX);
            appState.setOffsetY(appState.getOffsetY() + deltaY);
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            drawGraph();
        });

        this.canvas.setOnMouseReleased(e -> canvas.setCursor(javafx.scene.Cursor.DEFAULT));

        this.canvas.setOnMouseClicked(e -> {
            double cx = canvas.getWidth() / 2.0 + appState.getOffsetX();
            double cy = canvas.getHeight() / 2.0 + appState.getOffsetY();
            double scale = appState.getScale();

            for (Point2D pinnedPoint : new java.util.ArrayList<>(appState.getPinnedPoints())) {
                double screenX = cx + pinnedPoint.getX() * scale;
                double screenY = cy - pinnedPoint.getY() * scale;
                if (Math.hypot(screenX - e.getX(), screenY - e.getY()) < 10) {
                    appState.togglePinnedPoint(pinnedPoint);
                    drawGraph();
                    return;
                }
            }

            for (Point2D tempPoint : appState.getTemporaryPoints()) {
                double screenX = cx + tempPoint.getX() * scale;
                double screenY = cy - tempPoint.getY() * scale;
                if (Math.hypot(screenX - e.getX(), screenY - e.getY()) < 10) {
                    appState.togglePinnedPoint(tempPoint);
                    drawGraph();
                    return;
                }
            }
        });
    }

    public void drawGraph() {
        if (canvas == null || gc == null) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width == 0 || height == 0) return;

        gc.clearRect(0, 0, width, height);

        if (appState.getGraphMode() == AppState.GraphMode.POLAR) {
            axesRenderer.drawPolarGrid(width, height);
        } else {
            axesRenderer.drawSmartGridLines(width, height);
            axesRenderer.drawAxes(width, height);
        }

        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();

        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                boolean isHidden = (boolean) mainRow.getProperties().getOrDefault("isHidden", false);
                if (isHidden) continue;
                Color rowColor = (Color) mainRow.getUserData();
                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                // MathRenderer wrapped the TextField inside a StackPane (inputInner)
                StackPane inputInner = (StackPane) fieldAndPrompt.getChildren().get(0);
                TextField inputBox = (TextField) inputInner.getChildren().get(0);

                String rawInput = inputBox.getText().trim();
                if (rawInput.isEmpty()) continue;

                // Reverse any unicode pretty-print chars (², √, π, ≤, ≥ …) back to
                // ASCII before the boundary splitter and all plotters see the string.
                String eqStr = EquationHandler.reverseAutoFormat(rawInput.replace(" ", ""));
                FunctionPlotter.BoundaryCondition boundary = null;

                if (eqStr.contains("{") && eqStr.contains("}")) {
                    int start = eqStr.lastIndexOf("{");
                    int end = eqStr.lastIndexOf("}");
                    if (start < end) {
                        String condStr = eqStr.substring(start + 1, end);
                        eqStr = eqStr.substring(0, start); // কন্ডিশন ছাড়া শুধু সমীকরণ
                        boundary = new FunctionPlotter.BoundaryCondition(condStr, appState.getGlobalVariables());
                    }
                }

                // ─── POLAR equation  r = f(t)  ──────────────────────────────────
                // Convert  r=expr  to parametric (expr*cos(t), expr*sin(t))
                // Lowercase check handles both "r=" and "R="
                // FIX: t must start at 0, not negative. Negative t makes r negative,
                // which mirrors the curve 180° and draws a second unwanted ghost copy.
                // 0 → 8π gives 4 full rotations — sufficient for spirals and roses.
                String eqLower = eqStr.toLowerCase();
                if (eqLower.startsWith("r=")) {
                    String rExpr = eqStr.substring(2); // expression after "r="
                    String xEq = "(" + rExpr + ")*cos(t)";
                    String yEq = "(" + rExpr + ")*sin(t)";
                    functionPlotter.plotParametric(xEq, yEq, rowColor, centerX, centerY, width, height, boundary,
                            0, 8 * Math.PI);
                    continue;
                }

                if (eqStr.startsWith("(") && eqStr.endsWith(")")) {
                    try {
                        String[] parts = eqStr.substring(1, eqStr.length() - 1).split(",");
                        if (parts.length == 2) {
                            String xPart = parts[0].trim();
                            String yPart = parts[1].trim();


                            if (xPart.contains("t") || yPart.contains("t")) {
                                functionPlotter.plotParametric(xPart, yPart, rowColor, centerX, centerY, width, height, boundary);
                            } else {

                                double pX = EquationHandler.buildExpression(xPart, "x", appState.getGlobalVariables()).evaluate();
                                double pY = EquationHandler.buildExpression(yPart, "x", appState.getGlobalVariables()).evaluate();


                                if (boundary != null && !boundary.test(pX, pY, 0)) continue;

                                double screenX = centerX + (pX * appState.getScale());
                                double screenY = centerY - (pY * appState.getScale());

                                gc.setFill(rowColor);
                                gc.fillOval(screenX - 5, screenY - 5, 10, 10);
                                gc.setFill(Color.BLACK);
                                gc.fillText("(" + formatNumber(pX) + ", " + formatNumber(pY) + ")", screenX + 10, screenY - 10);
                            }
                        }
                    } catch (Exception ex) {}
                } else {
                    String[] ineq = detectInequality(eqStr);
                    if (ineq != null && ineq.length == 5) {
                        functionPlotter.plotCompoundInequality(ineq[0], ineq[1], ineq[2], ineq[3], ineq[4], rowColor, centerX, centerY, width, height, boundary);
                    } else if (ineq != null) {
                        functionPlotter.plotInequality(ineq[0], ineq[1], ineq[2], rowColor, centerX, centerY, width, height, boundary);
                    } else {
                        functionPlotter.plotEquation(eqStr, rowColor, centerX, centerY, width, height, boundary);
                    }
                }
            }
        }

        axesRenderer.drawGridLabels(width, height);
        calculateTemporaryPoints(width, height, centerX, centerY);
        validatePinnedPoints();
        drawPoints(centerX, centerY);
    }

    private void handleZoom(ScrollEvent event) {
        event.consume();
        double zoomFactor = 1.1;
        double oldScale = appState.getScale();
        double newScale = oldScale;

        if (event.getDeltaY() > 0) newScale *= zoomFactor;
        else newScale /= zoomFactor;

        if (newScale < 0.000001) newScale = 0.000001;
        if (newScale > 10000000) newScale = 10000000;

        double mx = event.getX();
        double my = event.getY();
        double cx = canvas.getWidth() / 2.0;
        double cy = canvas.getHeight() / 2.0;

        double oldOffsetX = appState.getOffsetX();
        double oldOffsetY = appState.getOffsetY();

        double newOffsetX = mx - cx - (mx - cx - oldOffsetX) * (newScale / oldScale);
        double newOffsetY = my - cy - (my - cy - oldOffsetY) * (newScale / oldScale);

        appState.setScale(newScale);
        appState.setOffsetX(newOffsetX);
        appState.setOffsetY(newOffsetY);

        drawGraph();
    }

    private String formatNumber(double val) {
        double rounded = Math.round(val * 1e8) / 1e8;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) return String.valueOf((long) Math.round(rounded));
        return String.format("%.5f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private String[] detectInequality(String eq) {
        java.util.List<Integer> starts = new java.util.ArrayList<>();
        java.util.List<Integer> lengths = new java.util.ArrayList<>();
        java.util.List<String> ops = new java.util.ArrayList<>();
        for (int i = 0; i < eq.length(); i++) {
            if (i < eq.length() - 1 && eq.startsWith("<=", i)) {
                starts.add(i); lengths.add(2); ops.add("<="); i++;
            } else if (i < eq.length() - 1 && eq.startsWith(">=", i)) {
                starts.add(i); lengths.add(2); ops.add(">="); i++;
            } else if (eq.charAt(i) == '<') {
                starts.add(i); lengths.add(1); ops.add("<");
            } else if (eq.charAt(i) == '>') {
                starts.add(i); lengths.add(1); ops.add(">");
            }
        }
        if (ops.size() == 0) return null;
        if (ops.size() == 1) {
            int p = starts.get(0), l = lengths.get(0);
            return new String[]{eq.substring(0, p).trim(), ops.get(0), eq.substring(p + l).trim()};
        }
        if (ops.size() == 2) {
            int p1 = starts.get(0), l1 = lengths.get(0);
            int p2 = starts.get(1), l2 = lengths.get(1);
            return new String[]{eq.substring(0, p1).trim(), ops.get(0),
                    eq.substring(p1 + l1, p2).trim(), ops.get(1),
                    eq.substring(p2 + l2).trim()};
        }
        return null;
    }

    // --- INTERSECTION LOGIC ---
    private class EqWrapper {
        int type;
        Expression expr;

        EqWrapper(int axisType) { this.type = axisType; }

        EqWrapper(String eqStr, Map<String, Double> globals) {
            String eq = EquationHandler.formatEquation(eqStr);
            if (eq.startsWith("y=") && !eq.contains("x=")) { type = 0; expr = EquationHandler.buildExpression(eq.substring(2), "x", globals); }
            else if (eq.startsWith("x=") && !eq.contains("y=")) { type = 1; expr = EquationHandler.buildExpression(eq.substring(2), "y", globals); }
            else if (eq.contains("=")) { type = 2; String[] parts = eq.split("="); String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq; expr = EquationHandler.buildImplicitExpression(expressionStr, globals); }
            else { type = 0; expr = EquationHandler.buildExpression(eq, "x", globals); }
        }

        double evaluateDifference(double x, double y) {
            if (type == 3) return y;
            if (type == 4) return x;
            try {
                if (type == 0) { expr.setVariable("x", x); return y - expr.evaluate(); }
                else if (type == 1) { expr.setVariable("y", y); return x - expr.evaluate(); }
                else { expr.setVariable("x", x); expr.setVariable("y", y); return expr.evaluate(); }
            } catch (Exception e) { return Double.NaN; }
        }
    }

    private void addTempPoint(double x, double y) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) return;
        Point2D p = new Point2D(x, y);
        double pixelThreshold = 10.0 / appState.getScale();
        for (Point2D existing : appState.getTemporaryPoints()) {
            if (existing.distance(p) < pixelThreshold) return;
        }
        appState.getTemporaryPoints().add(p);
    }

    private boolean crossesBox(EqWrapper eq, double xMin, double xMax, double yMin, double yMax) {
        if (eq == null) return false;
        double midX = (xMin + xMax) / 2.0; double midY = (yMin + yMax) / 2.0;
        double vBL = eq.evaluateDifference(xMin, yMin); double vBR = eq.evaluateDifference(xMax, yMin);
        double vTL = eq.evaluateDifference(xMin, yMax); double vTR = eq.evaluateDifference(xMax, yMax);
        double vC  = eq.evaluateDifference(midX, midY); double vT = eq.evaluateDifference(midX, yMax);
        double vB = eq.evaluateDifference(midX, yMin); double vL = eq.evaluateDifference(xMin, midY);
        double vR = eq.evaluateDifference(xMax, midY);

        boolean hasPos = vBL > 0 || vBR > 0 || vTL > 0 || vTR > 0 || vC > 0 || vT > 0 || vB > 0 || vL > 0 || vR > 0;
        boolean hasNeg = vBL < 0 || vBR < 0 || vTL < 0 || vTR < 0 || vC < 0 || vT < 0 || vB < 0 || vL < 0 || vR < 0;

        double epsilon = 1e-7;
        if (Math.abs(vBL) < epsilon || Math.abs(vBR) < epsilon || Math.abs(vTL) < epsilon || Math.abs(vTR) < epsilon || Math.abs(vC) < epsilon || Math.abs(vT) < epsilon || Math.abs(vB) < epsilon || Math.abs(vL) < epsilon || Math.abs(vR) < epsilon) return true;
        return hasPos && hasNeg;
    }

    private void refineBox(EqWrapper eq1, EqWrapper eq2, double x1, double x2, double y1, double y2, int depth) {
        if (depth > 12) {
            double midX = (x1 + x2) / 2.0; double midY = (y1 + y2) / 2.0;
            double val1 = eq1.evaluateDifference(midX, midY); double val2 = eq2.evaluateDifference(midX, midY);
            if (Math.abs(val1) < 1000.0 && Math.abs(val2) < 1000.0) addTempPoint(midX, midY);
            return;
        }
        double midX = (x1 + x2) / 2.0; double midY = (y1 + y2) / 2.0;
        if (crossesBox(eq1, x1, midX, y1, midY) && crossesBox(eq2, x1, midX, y1, midY)) refineBox(eq1, eq2, x1, midX, y1, midY, depth + 1);
        if (crossesBox(eq1, midX, x2, y1, midY) && crossesBox(eq2, midX, x2, y1, midY)) refineBox(eq1, eq2, midX, x2, y1, midY, depth + 1);
        if (crossesBox(eq1, x1, midX, midY, y2) && crossesBox(eq2, x1, midX, midY, y2)) refineBox(eq1, eq2, x1, midX, midY, y2, depth + 1);
        if (crossesBox(eq1, midX, x2, midY, y2) && crossesBox(eq2, midX, x2, midY, y2)) refineBox(eq1, eq2, midX, x2, midY, y2, depth + 1);
    }

    private void calculateTemporaryPoints(double width, double height, double cx, double cy) {
        appState.getTemporaryPoints().clear();
        int focusedIdx = appState.getFocusedEquationIndex();
        if (focusedIdx == -1) return;

        List<EqWrapper> otherExprs = new ArrayList<>();
        EqWrapper focusedEq = null;

        int index = 0;
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                boolean isHidden = (boolean) mainRow.getProperties().getOrDefault("isHidden", false);
                if (isHidden) {
                    index++;
                    continue;
                }

                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                StackPane inputInner2 = (StackPane) fieldAndPrompt.getChildren().get(0);
                TextField inputBox = (TextField) inputInner2.getChildren().get(0);
                String eq = inputBox.getText();
                if (!eq.trim().isEmpty() && !(eq.startsWith("(") && eq.endsWith(")"))) {
                    try {
                        EqWrapper wrapper = new EqWrapper(eq, appState.getGlobalVariables());
                        if (index == focusedIdx) focusedEq = wrapper; else otherExprs.add(wrapper);
                    } catch (Exception ignored) {}
                }
            }
            index++;
        }
        if (focusedEq == null) return;
        otherExprs.add(new EqWrapper(3)); otherExprs.add(new EqWrapper(4));

        double scale = appState.getScale(); double startX = -cx / scale; double endX = (width - cx) / scale;
        double startY = (cy - height) / scale; double endY = cy / scale;
        double step = 10.0 / scale;

        for (double x = startX; x < endX; x += step) {
            for (double y = startY; y < endY; y += step) {
                if (crossesBox(focusedEq, x, x + step, y, y + step)) {
                    for (EqWrapper other : otherExprs) {
                        if (crossesBox(other, x, x + step, y, y + step)) refineBox(focusedEq, other, x, x + step, y, y + step, 0);
                    }
                }
            }
        }
        addExtrema(focusedEq, startX, endX, startY, endY);
    }

    private void addExtrema(EqWrapper eq, double startX, double endX, double startY, double endY) {
        if (eq == null || eq.expr == null || eq.type == 2) return;
        final int STEPS = 500;

        if (eq.type == 0) {
            double h = (endX - startX) / STEPS;
            double eps = h * 1e-4;
            double prevDeriv = Double.NaN;
            double prevX = startX;
            for (int i = 1; i <= STEPS; i++) {
                double x = startX + i * h;
                try {
                    eq.expr.setVariable("x", x - eps);
                    double ym = eq.expr.evaluate();
                    eq.expr.setVariable("x", x + eps);
                    double yp = eq.expr.evaluate();
                    double deriv = yp - ym;
                    if (!Double.isNaN(prevDeriv) && !Double.isInfinite(deriv) && !Double.isNaN(deriv)
                            && Math.abs(deriv) < 1e10 && Math.abs(prevDeriv) < 1e10
                            && prevDeriv * deriv < 0) {
                        double lo = prevX, hi = x;
                        for (int iter = 0; iter < 50; iter++) {
                            double mid = (lo + hi) / 2.0;
                            eq.expr.setVariable("x", mid - eps);
                            double ym2 = eq.expr.evaluate();
                            eq.expr.setVariable("x", mid + eps);
                            double yp2 = eq.expr.evaluate();
                            double dMid = yp2 - ym2;
                            if (dMid * prevDeriv > 0) lo = mid; else hi = mid;
                        }
                        double extremX = (lo + hi) / 2.0;
                        eq.expr.setVariable("x", extremX);
                        double extremY = eq.expr.evaluate();
                        if (!Double.isNaN(extremY) && !Double.isInfinite(extremY)) addTempPoint(extremX, extremY);
                    }
                    prevDeriv = deriv;
                    prevX = x;
                } catch (Exception ignored) { prevDeriv = Double.NaN; prevX = x; }
            }
        } else if (eq.type == 1) {
            double h = (endY - startY) / STEPS;
            double eps = h * 1e-4;
            double prevDeriv = Double.NaN;
            double prevY = startY;
            for (int i = 1; i <= STEPS; i++) {
                double y = startY + i * h;
                try {
                    eq.expr.setVariable("y", y - eps);
                    double xm = eq.expr.evaluate();
                    eq.expr.setVariable("y", y + eps);
                    double xp = eq.expr.evaluate();
                    double deriv = xp - xm;
                    if (!Double.isNaN(prevDeriv) && !Double.isInfinite(deriv) && !Double.isNaN(deriv)
                            && Math.abs(deriv) < 1e10 && Math.abs(prevDeriv) < 1e10
                            && prevDeriv * deriv < 0) {
                        double lo = prevY, hi = y;
                        for (int iter = 0; iter < 50; iter++) {
                            double mid = (lo + hi) / 2.0;
                            eq.expr.setVariable("y", mid - eps);
                            double xm2 = eq.expr.evaluate();
                            eq.expr.setVariable("y", mid + eps);
                            double xp2 = eq.expr.evaluate();
                            double dMid = xp2 - xm2;
                            if (dMid * prevDeriv > 0) lo = mid; else hi = mid;
                        }
                        double extremY2 = (lo + hi) / 2.0;
                        eq.expr.setVariable("y", extremY2);
                        double extremX2 = eq.expr.evaluate();
                        if (!Double.isNaN(extremX2) && !Double.isInfinite(extremX2)) addTempPoint(extremX2, extremY2);
                    }
                    prevDeriv = deriv;
                    prevY = y;
                } catch (Exception ignored) { prevDeriv = Double.NaN; prevY = y; }
            }
        }
    }

    private void validatePinnedPoints() {
        if (appState.getPinnedPoints().isEmpty()) return;
        List<EqWrapper> allExprs = new ArrayList<>();
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;

                // ⚠️ NEW: Eye Button Check
                boolean isHidden = (boolean) mainRow.getProperties().getOrDefault("isHidden", false);
                if (isHidden) continue;

                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                StackPane inputInner3 = (StackPane) fieldAndPrompt.getChildren().get(0);
                TextField inputBox = (TextField) inputInner3.getChildren().get(0);
                String eq = inputBox.getText();
                if (!eq.trim().isEmpty() && !(eq.startsWith("(") && eq.endsWith(")"))) {
                    try { allExprs.add(new EqWrapper(eq, appState.getGlobalVariables())); } catch (Exception ignored) {}
                }
            }
        }
        allExprs.add(new EqWrapper(3)); allExprs.add(new EqWrapper(4));
        double threshold = 5.0 / appState.getScale();
        appState.getPinnedPoints().removeIf(p -> {
            int crossingCount = 0;
            for (EqWrapper eq : allExprs) {
                if (crossesBox(eq, p.getX() - threshold, p.getX() + threshold, p.getY() - threshold, p.getY() + threshold)) crossingCount++;
            }
            return crossingCount < 1;
        });
    }

    private void drawPoints(double cx, double cy) {
        double scale = appState.getScale();
        gc.setFill(Color.web("#3b3535", 1.0));
        for (Point2D p : appState.getTemporaryPoints()) {
            double screenX = cx + p.getX() * scale;
            double screenY = cy - p.getY() * scale;
            gc.fillOval(screenX - 4, screenY - 4, 8, 8);
            gc.setStroke(Color.web("#3b3535", 0.0));
            gc.strokeOval(screenX - 4, screenY - 4, 8, 8);
        }

        gc.setFont(Font.font("Arial", 14));
        for (Point2D p : appState.getPinnedPoints()) {
            double screenX = cx + p.getX() * scale;
            double screenY = cy - p.getY() * scale;


            gc.setFill(Color.web("#007AFF"));
            gc.fillOval(screenX - 5, screenY - 5, 10, 10);
            gc.setStroke(Color.web("#888888", 0.0));
            gc.strokeOval(screenX - 5, screenY - 5, 10, 10);


            double xVal = Math.abs(p.getX()) < 0.0001 ? 0.0 : p.getX();
            double yVal = Math.abs(p.getY()) < 0.0001 ? 0.0 : p.getY();
            String label = String.format("(%.2f, %.2f)", xVal, yVal);


            gc.setFill(Color.web("#2c3e50", 0.9)); // সুন্দর ডার্ক থিম
            gc.fillRoundRect(screenX + 12, screenY - 22, 90, 25, 6, 6);


            gc.setFill(Color.WHITE);
            gc.fillText(label, screenX + 18, screenY - 4);
        }
    }
}