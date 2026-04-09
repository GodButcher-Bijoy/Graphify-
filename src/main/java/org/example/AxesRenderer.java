package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AxesRenderer {
    private final AppState appState;
    private final GraphicsContext gc;

    public AxesRenderer(AppState appState, GraphicsContext gc) {
        this.appState = appState;
        this.gc = gc;
    }

    // --- Grid Lines Only ---
    public void drawSmartGridLines(double width, double height) {
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

    public void drawAxes(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);
    }

    // --- Number Labels (Drawn on top) ---
    public void drawGridLabels(double width, double height) {
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

        if (centerX >= 0 && centerX <= width && centerY >= 0 && centerY <= height) {
            String label = "0";

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

    // --- Polar Grid (Concentric Circles + Radial Lines) ---
    public void drawPolarGrid(double width, double height) {
        double centerX = width / 2.0 + appState.getOffsetX();
        double centerY = height / 2.0 + appState.getOffsetY();
        double scale   = appState.getScale();

        // ── 1. Choose a smart radius step (same logic as Cartesian major step) ──
        double targetGridPixelWidth = 100;
        double minStep = (width / scale) * (targetGridPixelWidth / width);
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual  = minStep / magnitude;
        double majorStep;
        if      (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep =  5 * magnitude;
        else if (residual > 1) majorStep =  2 * magnitude;
        else                   majorStep =      magnitude;

        // Minor circles: 5 subdivisions (same feel as Cartesian minor lines)
        double minorStep = majorStep / 5.0;

        Color majorCircleColor = Color.web("#999999");
        Color minorCircleColor = Color.web("#E0E0E0");

        // Maximum radius we need to draw (diagonal of canvas is the worst case)
        double maxR = Math.hypot(
                Math.max(centerX, width  - centerX),
                Math.max(centerY, height - centerY)
        ) / scale;

        // ── 2. Draw concentric circles ─────────────────────────────────────────
        for (double r = minorStep; r <= maxR + majorStep; r += minorStep) {
            double remainder = Math.abs(r / majorStep - Math.round(r / majorStep));
            boolean isMajor  = remainder < 0.001;

            double screenR = r * scale;
            if (isMajor) { gc.setStroke(majorCircleColor); gc.setLineWidth(1.0); }
            else         { gc.setStroke(minorCircleColor); gc.setLineWidth(0.7); }

            gc.strokeOval(centerX - screenR, centerY - screenR,
                    screenR * 2,       screenR * 2);
        }

        // ── 3. Draw radial lines every 30° ────────────────────────────────────
        Color radialColor = Color.web("#BBBBBB");
        Color axisColor   = Color.BLACK;

        // The longest line we'd ever need to draw is maxR in screen pixels
        double lineLen = maxR * scale + majorStep * scale;

        for (int deg = 0; deg < 360; deg += 30) {
            double rad = Math.toRadians(deg);
            double dx  = Math.cos(rad);
            double dy  = Math.sin(rad);   // screen y is inverted below

            // Lines at 0° and 90° are the main axes — draw them thicker / darker
            boolean isMainAxis = (deg == 0 || deg == 90 || deg == 180 || deg == 270);
            if (isMainAxis) { gc.setStroke(axisColor);   gc.setLineWidth(1.5); }
            else            { gc.setStroke(radialColor); gc.setLineWidth(0.9); }

            gc.strokeLine(
                    centerX + dx * (-lineLen), centerY - dy * (-lineLen),
                    centerX + dx * lineLen,    centerY - dy * lineLen
            );
        }

        // ── 4. Radius labels along the positive x-axis ────────────────────────
        gc.setFont(new Font("Arial", 12));
        Color textColor = Color.web("#444444");
        Color bgWhite   = Color.web("#FFFFFF", 0.85);

        for (double r = majorStep; r <= maxR + majorStep; r += majorStep) {
            double screenX = centerX + r * scale;
            // Only draw the label if it's visible on screen
            if (screenX > width) break;

            String label = formatNumber(r);
            double labelY = centerY + 16;   // just below the x-axis

            gc.setFill(bgWhite);
            gc.fillRect(screenX - 4, labelY - 12, label.length() * 8, 14);
            gc.setFill(textColor);
            gc.fillText(label, screenX - 4, labelY);
        }

        // ── 5. Angle labels at the end of each radial spoke ───────────────────
        // Place them just outside the outermost visible circle
        double labelR = (Math.floor(maxR / majorStep) * majorStep) * scale;
        labelR = Math.min(labelR, Math.min(width, height) * 0.44); // clamp inside canvas

        String[] angleLabels = {
                "0°","30°","60°","90°","120°","150°",
                "180°","210°","240°","270°","300°","330°"
        };

        gc.setFont(new Font("Arial", 11));
        for (int i = 0; i < 12; i++) {
            double rad = Math.toRadians(i * 30);
            double lx  = centerX + Math.cos(rad) * (labelR + 18);
            double ly  = centerY - Math.sin(rad) * (labelR + 18);

            // Skip if outside canvas
            if (lx < 0 || lx > width || ly < 0 || ly > height) continue;

            String lbl = angleLabels[i];
            gc.setFill(bgWhite);
            gc.fillRect(lx - 4, ly - 12, lbl.length() * 7 + 4, 15);
            gc.setFill(textColor);
            gc.fillText(lbl, lx - 4, ly);
        }

        // ── 6. Origin label ───────────────────────────────────────────────────
        if (centerX >= 0 && centerX <= width && centerY >= 0 && centerY <= height) {
            gc.setFill(bgWhite);
            gc.fillRect(centerX - 14, centerY + 3, 10, 14);
            gc.setFill(textColor);
            gc.fillText("0", centerX - 12, centerY + 14);
        }
    }

    // [NEW] Smart Number Formatter to fix 0.0 bug
    private String formatNumber(double val) {

        double rounded = Math.round(val * 1e8) / 1e8;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) {
            return String.valueOf((long) Math.round(rounded)); // Integer hole purno sonkha dekhabe
        }

        return String.format("%.5f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}