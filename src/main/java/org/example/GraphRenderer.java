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

import java.util.Map;

public class GraphRenderer {
    private final AppState appState;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final VBox functionContainer;

    // For panning (dragging) the graph
    private double lastMouseX;
    private double lastMouseY;

    public GraphRenderer(AppState appState, Canvas canvas, VBox functionContainer) {
        this.appState = appState;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.functionContainer = functionContainer;

        // Resize listeners
        this.canvas.widthProperty().addListener(evt -> drawGraph());
        this.canvas.heightProperty().addListener(evt -> drawGraph());

        // --- 1. Zoom Listener ---
        this.canvas.setOnScroll(this::handleZoom);

        // --- 2. Panning (Drag) Listeners ---
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

        this.canvas.setOnMouseReleased(e -> {
            canvas.setCursor(javafx.scene.Cursor.DEFAULT);
        });
    }

    public void drawGraph() {
        if (canvas == null || gc == null) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width == 0 || height == 0) return;

        gc.clearRect(0, 0, width, height);

        // ১. আগে গ্রিড লাইন আর অক্ষ আঁকবো
        drawSmartGridLines(width, height);
        drawAxes(width, height);

        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();

        // ২. এরপর গ্রাফের লাইনগুলো আঁকবো (যাতে লাইনের নিচে গ্রিড থাকে)
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                Color rowColor = (Color) mainRow.getUserData();
                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);

                String equation = inputBox.getText();
                String eq = equation.trim().replace(" ", "");

                if (eq.startsWith("(") && eq.endsWith(")")) {
                    // Point input: (x,y)
                    try {
                        String[] parts = eq.substring(1, eq.length() - 1).split(",");
                        if (parts.length == 2) {
                            // Expression hishebe evaluate korchi jate (a, 2*b) emon input o kaj kore
                            double pX = EquationHandler.buildExpression(parts[0], "x", appState.getGlobalVariables()).evaluate();
                            double pY = EquationHandler.buildExpression(parts[1], "x", appState.getGlobalVariables()).evaluate();

                            // Math co-ordinate ke Screen co-ordinate e convert kora
                            double screenX = centerX + (pX * appState.getScale());
                            double screenY = centerY - (pY * appState.getScale());

                            // Dot (Point) draw kora
                            gc.setFill(rowColor); // Input box er same color
                            gc.fillOval(screenX - 5, screenY - 5, 10, 10); // 10px size er dot

                            // Point er pashe text lekha (optional)
                            gc.setFill(Color.BLACK);
                            gc.fillText("(" + formatNumber(pX) + ", " + formatNumber(pY) + ")", screenX + 10, screenY - 10);
                        }
                    } catch (Exception ex) {
                        // Invalid point input hole ignore korbe
                    }
                } else {
                    // Normal Equation (Ager code)
                    plotEquation(equation, rowColor, centerX, centerY, width, height);
                }
            }
        }

        // ৩. একদম শেষে নাম্বারগুলো আঁকবো (যাতে নাম্বার সবসময় গ্রাফ লাইনের উপরে থাকে)
        drawGridLabels(width, height);
    }

    // --- Grid Lines Only ---
    private void drawSmartGridLines(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        double scale = appState.getScale();

        Color majorColor = Color.web("#999999");
        Color minorColor = Color.web("#E0E0E0");

        double targetGridPixelWidth = 100;
        double minStep = (width / scale) * (targetGridPixelWidth / width);
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual = minStep / magnitude;

        double majorStep;
        if (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep = 5 * magnitude;
        else if (residual > 1) majorStep = 2 * magnitude;
        else majorStep = magnitude;

        int subdivisions = (Math.abs(majorStep / magnitude - 2) < 0.001) ? 4 : 5;
        double minorStep = majorStep / subdivisions;

        // Vertical Lines
        double startX = Math.floor(-centerX / scale / minorStep) * minorStep;
        for (double i = startX; i * scale + centerX < width; i += minorStep) {
            double screenX = centerX + (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }
            gc.strokeLine(screenX, 0, screenX, height);
        }

        // Horizontal Lines
        double startY = Math.floor((centerY - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(centerY / scale / minorStep) * minorStep;
        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = centerY - (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }
            gc.strokeLine(0, screenY, width, screenY);
        }
    }

    // [NEW] Smart Number Formatter to fix 0.0 bug
    private String formatNumber(double val) {
        // Precision theek korar jonno round kora
        double rounded = Math.round(val * 1e8) / 1e8;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) {
            return String.valueOf((long) Math.round(rounded)); // Integer hole purno sonkha dekhabe
        }
        // Doshomik er por extra 0 gula kete dibe
        return String.format("%.5f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    // --- Number Labels (Drawn on top) ---
    private void drawGridLabels(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        double scale = appState.getScale();

        gc.setFont(new Font("Arial", 12));
        Color textColor = Color.web("#444444");
        Color bgWhite = Color.web("#FFFFFF", 0.85);

        double targetGridPixelWidth = 100;
        double minStep = (width / scale) * (targetGridPixelWidth / width);
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual = minStep / magnitude;

        double majorStep;
        if (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep = 5 * magnitude;
        else if (residual > 1) majorStep = 2 * magnitude;
        else majorStep = magnitude;

        int subdivisions = (Math.abs(majorStep / magnitude - 2) < 0.001) ? 4 : 5;
        double minorStep = majorStep / subdivisions;

        // X-Axis Numbers
        double startX = Math.floor(-centerX / scale / minorStep) * minorStep;
        double labelY = Math.max(20, Math.min(height - 10, centerY + 20));

        for (double i = startX; i * scale + centerX < width; i += minorStep) {
            double screenX = centerX + (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor && Math.abs(i) > 0.0000001) { // precision adjust kora holo
                String label = formatNumber(i); // [FIX] Notun formatter use kora holo

                gc.setFill(bgWhite);
                gc.fillRect(screenX - 8, labelY - 11, label.length() * 8, 14);
                gc.setFill(textColor);
                gc.fillText(label, screenX - 6, labelY);
            }
        }

        // Y-Axis Numbers
        double startY = Math.floor((centerY - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(centerY / scale / minorStep) * minorStep;
        double labelX = Math.max(10, Math.min(width - 35, centerX + 8));

        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = centerY - (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor && Math.abs(i) > 0.0000001) { // precision adjust kora holo
                String label = formatNumber(i); // [FIX] Notun formatter use kora holo

                gc.setFill(bgWhite);
                gc.fillRect(labelX - 2, screenY - 6, label.length() * 8, 14);
                gc.setFill(textColor);
                gc.fillText(label, labelX, screenY + 5);
            }
        }
        // --- Origin (0,0) Numbering (Desmos Style) ---
        // Center (intersection) jodi screen er vitore thake, tobei 0 draw korbe
        if (centerX >= 0 && centerX <= width && centerY >= 0 && centerY <= height) {
            String label = "0";

            // Ektu bame ar niche soriye draw korchi jate axis line er sathe overlap na hoy
            double zeroX = centerX - 12;
            double zeroY = centerY + 14;

            // Background shadow
            gc.setFill(bgWhite);
            gc.fillRect(zeroX - 2, zeroY - 11, 10, 14);

            // Draw '0'
            gc.setFill(textColor);
            gc.fillText(label, zeroX, zeroY);
        }
    }

    private void drawAxes(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);
    }

    // --- Smooth Mouse-Centered Zoom ---
    private void handleZoom(ScrollEvent event) {
        event.consume();
        double zoomFactor = 1.1;
        double oldScale = appState.getScale();
        double newScale = oldScale;

        if (event.getDeltaY() > 0) newScale *= zoomFactor;
        else newScale /= zoomFactor;

        // [FIX] Zoom out & in limit extended (Desmos er moto)
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

    private void plotEquation(String eqStr, Color color, double cx, double cy, double width, double height) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);

        String eq = EquationHandler.formatEquation(eqStr);

        try {
            if (eq.startsWith("y=") && !eq.contains("x=")) plotStandard(cx, cy, width, height, eq.substring(2));
            else if (eq.startsWith("x=") && !eq.contains("y=")) plotInverse(cx, cy, width, height, eq.substring(2));
            else if (eq.contains("=")) plotImplicit(cx, cy, width, height, eq);
            else plotStandard(cx, cy, width, height, eq);
        } catch (Exception e) {}
    }

    private void plotStandard(double cx, double cy, double width, double height, String function) {
        Expression expr = EquationHandler.buildExpression(function, "x", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;
        for (double screenX = 0; screenX < width; screenX++) {
            double mathX = (screenX - cx) / appState.getScale();
            expr.setVariable("x", mathX);
            double mathY;
            try { mathY = expr.evaluate(); } catch (Exception e) { continue; }

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) { first = true; continue; }
            double screenY = cy - (mathY * appState.getScale());

            if (screenY < -500 || screenY > height + 500) { first = true; continue; }
            if (first) { gc.moveTo(screenX, screenY); first = false; }
            else { gc.lineTo(screenX, screenY); }
        }
        gc.stroke();
    }

    private void plotInverse(double cx, double cy, double width, double height, String function) {
        Expression expr = EquationHandler.buildExpression(function, "y", appState.getGlobalVariables());
        gc.beginPath();
        boolean first = true;
        for (double screenY = 0; screenY < height; screenY++) {
            double mathY = (cy - screenY) / appState.getScale();
            expr.setVariable("y", mathY);
            double mathX;
            try { mathX = expr.evaluate(); } catch (Exception e) { continue; }

            if (Double.isNaN(mathX) || Double.isInfinite(mathX)) { first = true; continue; }
            double screenX = cx + (mathX * appState.getScale());

            if (screenX < -500 || screenX > width + 500) { first = true; continue; }
            if (first) { gc.moveTo(screenX, screenY); first = false; }
            else { gc.lineTo(screenX, screenY); }
        }
        gc.stroke();
    }

    private void plotImplicit(double cx, double cy, double width, double height, String eq) {
        gc.setLineWidth(2.0);
        String[] parts = eq.split("=");
        String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq;

        Expression expr = EquationHandler.buildImplicitExpression(expressionStr, appState.getGlobalVariables());

        int res = 4;
        for (double x = 0; x < width; x += res) {
            for (double y = 0; y < height; y += res) {
                double vBL = evaluate(expr, x, y + res, cx, cy, appState.getScale());
                double vBR = evaluate(expr, x + res, y + res, cx, cy, appState.getScale());
                double vTR = evaluate(expr, x + res, y, cx, cy, appState.getScale());
                double vTL = evaluate(expr, x, y, cx, cy, appState.getScale());

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

    private double evaluate(Expression expr, double screenX, double screenY, double cx, double cy, double scale) {
        double epsilon = 0.0001; // Avoid divide by zero error for implicit equations
        expr.setVariable("x", (screenX - cx + epsilon) / scale);
        expr.setVariable("y", (cy - screenY + epsilon) / scale);
        try { return expr.evaluate(); } catch (Exception e) { return Double.NaN; }
    }

    private boolean isSignDifferent(double v1, double v2) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return (v1 > 0 && v2 <= 0) || (v1 <= 0 && v2 > 0);
    }
}