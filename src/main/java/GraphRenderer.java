import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.objecthunter.exp4j.Expression;

public class GraphRenderer {
    private final AppState appState;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final VBox functionContainer;

    public GraphRenderer(AppState appState, Canvas canvas, VBox functionContainer) {
        this.appState = appState;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.functionContainer = functionContainer;

        // ক্যানভাস সাইজ পরিবর্তন হলে বা স্ক্রল করলে আবার গ্রাফ আঁকবে
        this.canvas.widthProperty().addListener(evt -> drawGraph());
        this.canvas.heightProperty().addListener(evt -> drawGraph());
        this.canvas.setOnScroll(this::handleZoom);
    }

    public void drawGraph() {
        if (canvas == null || gc == null) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width == 0 || height == 0) return;

        gc.clearRect(0, 0, width, height);
        drawSmartGrid(width, height);
        drawAxes(width, height);

        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();

        // তোমার UI স্ট্রাকচার অনুযায়ী সব ফাংশন বক্স থেকে ইকুয়েশন নিচ্ছে
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                Color rowColor = (Color) mainRow.getUserData();

                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);

                String equation = inputBox.getText();

                if (!equation.trim().isEmpty()) {
                    plotEquation(equation, rowColor, centerX, centerY, width, height);
                }
            }
        }
    }

    // --- Smart Grid (Your 1-2-5 Logic) ---
    private void drawSmartGrid(double width, double height) {
        double scale = appState.getScale();
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();

        Color majorColor = Color.web("#bfbfbf");
        Color minorColor = Color.web("#e6e6e6");
        Color textColor = Color.web("#666666");

        gc.setFont(new Font("Arial", 12));
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

        double startX = Math.floor(-centerX / scale / minorStep) * minorStep;
        for (double i = startX; i * scale + centerX < width; i += minorStep) {
            double screenX = centerX + (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }

            gc.strokeLine(screenX, 0, screenX, height);
            if (isMajor && Math.abs(i) > 0.001) {
                gc.setFill(textColor);
                String label = (Math.abs(i - Math.round(i)) < 0.001) ? String.format("%d", (long) Math.round(i)) : String.format("%.1f", i);
                gc.fillText(label, screenX - 4, centerY + 20);
            }
        }

        double startY = Math.floor((centerY - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(centerY / scale / minorStep) * minorStep;

        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = centerY - (i * scale);
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) { gc.setStroke(majorColor); gc.setLineWidth(1.0); }
            else { gc.setStroke(minorColor); gc.setLineWidth(0.7); }

            gc.strokeLine(0, screenY, width, screenY);
            if (isMajor && Math.abs(i) > 0.001) {
                gc.setFill(textColor);
                String label = (Math.abs(i - Math.round(i)) < 0.001) ? String.format("%d", (long) Math.round(i)) : String.format("%.1f", i);
                gc.fillText(label, centerX + 8, screenY + 5);
            }
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
        double scale = appState.getScale();

        gc.beginPath();
        boolean first = true;
        for (double screenX = 0; screenX < width; screenX++) {
            double mathX = (screenX - cx) / scale;
            expr.setVariable("x", mathX);
            double mathY = expr.evaluate();

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) continue;
            double screenY = cy - (mathY * scale);

            if (screenY < -500 || screenY > height + 500) { first = true; continue; }
            if (first) { gc.moveTo(screenX, screenY); first = false; }
            else { gc.lineTo(screenX, screenY); }
        }
        gc.stroke();
    }

    private void plotInverse(double cx, double cy, double width, double height, String function) {
        Expression expr = EquationHandler.buildExpression(function, "y", appState.getGlobalVariables());
        double scale = appState.getScale();

        gc.beginPath();
        boolean first = true;
        for (double screenY = 0; screenY < height; screenY++) {
            double mathY = (cy - screenY) / scale;
            expr.setVariable("y", mathY);
            double mathX = expr.evaluate();

            if (Double.isNaN(mathX) || Double.isInfinite(mathX)) continue;
            double screenX = cx + (mathX * scale);

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
        double scale = appState.getScale();

        int res = 4;
        for (double x = 0; x < width; x += res) {
            for (double y = 0; y < height; y += res) {
                double vBL = evaluate(expr, x, y + res, cx, cy, scale);
                double vBR = evaluate(expr, x + res, y + res, cx, cy, scale);
                double vTR = evaluate(expr, x + res, y, cx, cy, scale);
                double vTL = evaluate(expr, x, y, cx, cy, scale);

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
        expr.setVariable("x", (screenX - cx) / scale);
        expr.setVariable("y", (cy - screenY) / scale);
        try { return expr.evaluate(); } catch (Exception e) { return Double.NaN; }
    }

    private boolean isSignDifferent(double v1, double v2) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return (v1 > 0 && v2 < 0) || (v1 < 0 && v2 > 0) || v1 == 0 || v2 == 0;
    }

    private void handleZoom(ScrollEvent event) {
        event.consume();
        double zoomFactor = 1.1;
        double currentScale = appState.getScale();
        if (event.getDeltaY() > 0) {
            appState.setScale(currentScale * zoomFactor);
        } else {
            appState.setScale(currentScale / zoomFactor);
        }
        drawGraph();
    }
}