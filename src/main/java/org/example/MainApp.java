package org.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainApp extends Application {

    // --- GLOBAL STATE (From first.java logic) ---
    private static final double DEFAULT_SCALE = 40;
    private double scale = DEFAULT_SCALE; // Pixels per unit
    private double offsetX = 0; // Panning X (Future proofing)
    private double offsetY = 0; // Panning Y

    // Global Variable Store (Name -> Value)
    private final Map<String, Double> variables = new HashMap<>();

    // UI Components
    private Canvas canvas;
    private GraphicsContext gc;
    private VBox functionContainer; // Holds all input rows

    // Styling Constants (From MainApp.java)
    private final Color[] graphColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
    private int globalColorIndex = 0;

    @Override
    public void start(Stage stage) {
        // --- 1. INTRO ANIMATION (From MainApp.java) ---
        Pane root = new Pane();
        double width = 900;
        double height = 600;
        root.setStyle("-fx-background-color: #002366;");

        Line hLine = new Line(0, height - 100, width, height - 100);
        hLine.setStroke(Color.WHITE);
        hLine.setStrokeWidth(5);
        hLine.setScaleX(0);

        Line vLine = new Line(width / 2, 0, width / 2, height);
        vLine.setStroke(Color.WHITE);
        vLine.setStrokeWidth(5);
        vLine.setScaleY(0);

        Text title = new Text("Graphify Pro");
        title.setFont(Font.font("Pristina", FontWeight.BOLD, 85));
        title.setStroke(Color.WHITE);
        title.setStrokeWidth(0.3);
        title.setFill(Color.TRANSPARENT);
        title.setOpacity(0);
        title.setX((width / 2) - 160);
        title.setY(height / 2);

        root.getChildren().addAll(hLine, vLine, title);

        // Animations
        ScaleTransition hAnim = new ScaleTransition(Duration.seconds(1.5), hLine);
        hAnim.setFromX(0); hAnim.setToX(1);

        ScaleTransition vAnim = new ScaleTransition(Duration.seconds(1), vLine);
        vAnim.setFromY(0); vAnim.setToY(1);

        TranslateTransition slideLine = new TranslateTransition(Duration.seconds(1), vLine);
        slideLine.setToX(-(width / 2) + 50);

        TranslateTransition moveTitleUp = new TranslateTransition(Duration.seconds(1), title);
        moveTitleUp.setByY(-100);

        FadeTransition textFade = new FadeTransition(Duration.seconds(1.5), title);
        textFade.setFromValue(0); textFade.setToValue(1);

        FillTransition textFill = new FillTransition(Duration.seconds(2), title);
        textFill.setFromValue(Color.TRANSPARENT);
        textFill.setToValue(Color.WHITE);

        ParallelTransition sequence2 = new ParallelTransition(slideLine, textFade, textFill);
        SequentialTransition sequence = new SequentialTransition(hAnim, vAnim, sequence2);

        sequence.play();

        root.setOnMouseClicked(event -> {
            Scene mainScene = createMainScene(stage);
            stage.setScene(mainScene);
            stage.centerOnScreen();
        });

        Scene scene = new Scene(root, width, height);
        stage.setTitle("Graphify Pro - Ultimate Edition");
        stage.setScene(scene);
        stage.show();
    }

    // --- 2. MAIN LAYOUT (MainApp Design) ---
    private Scene createMainScene(Stage stage) {
        BorderPane root = new BorderPane();

        // A. SIDEBAR
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(100, 30, 30, 30));
        sidebar.setPrefWidth(420);
        sidebar.setAlignment(Pos.TOP_CENTER);
        // MainApp Dark Theme
        sidebar.setStyle("-fx-background-color: #121212; -fx-border-color: Purple; -fx-border-width: 0 4px 0 0;");

        Label inputLabel = new Label("Functions:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        functionContainer = new VBox(25);
        functionContainer.setStyle("-fx-background-color: transparent;");

        // Add initial input
        addFunctionInputBox();

        ScrollPane scrollPane = new ScrollPane(functionContainer);
        VBox.setMargin(scrollPane, new Insets(20, 0, 0, 0));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Control info
        Label helpLabel = new Label("Scroll to Zoom\nDrag sliders to update");
        helpLabel.setTextFill(Color.GRAY);
        helpLabel.setStyle("-fx-font-style: italic;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        sidebar.getChildren().addAll(inputLabel, scrollPane, helpLabel);

        // B. GRAPH AREA
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #ECF0F1;"); // Light background for graph

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());

        // Listeners for Redraw
        canvas.widthProperty().addListener(evt -> drawGraph());
        canvas.heightProperty().addListener(evt -> drawGraph());

        // Zoom Logic (From first.java)
        canvas.setOnScroll(this::handleZoom);

        graphPane.getChildren().add(canvas);

        root.setLeft(sidebar);
        root.setCenter(graphPane);

        return new Scene(root, 1100, 750);
    }

    // --- 3. INPUT SYSTEM (Combined Logic) ---
    private void addFunctionInputBox() {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");

        // Color Assignment
        Color assignedColor = graphColors[globalColorIndex % graphColors.length];
        globalColorIndex++;
        mainRow.setUserData(assignedColor); // Store color in the row

        StackPane inputWrapper = new StackPane();
        inputWrapper.setAlignment(Pos.CENTER_RIGHT);

        // Color Dot
        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor);
        StackPane.setAlignment(colorDot, Pos.CENTER_LEFT);
        StackPane.setMargin(colorDot, new Insets(0, 0, 0, 15));

        TextField inputBox = new TextField();
        inputBox.setPromptText("ex: y = sin(x) or x^2 + y^2 = 25");
        inputBox.setPrefHeight(50);
        inputBox.setPadding(new Insets(5, 80, 5, 35)); // Padding for dot and buttons
        inputBox.setStyle(
                "-fx-background-color: White; -fx-background-radius: 10; " +
                        "-fx-border-color: #9D00FF; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;"
        );

        // Icons
        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(40);
        StackPane.setMargin(buttonBox, new Insets(0, 10, 0, 0));

        Button closeBtn = createIconButton("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z", "gray", 18);

        // Hover effects
        closeBtn.setOnMouseEntered(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().add(closeBtn);

        // Container for Sliders and Prompts
        VBox metaContainer = new VBox(5);
        metaContainer.setPadding(new Insets(0, 0, 0, 20));

        HBox promptContainer = new HBox(10); // "Add slider for: a, b"
        promptContainer.setAlignment(Pos.CENTER_LEFT);

        VBox sliderContainer = new VBox(5); // Actual sliders

        metaContainer.getChildren().addAll(promptContainer, sliderContainer);

        // Delete Action
        Runnable deleteAction = () -> {
            functionContainer.getChildren().remove(mainRow);
            if (functionContainer.getChildren().isEmpty()) {
                addFunctionInputBox(); // Keep at least one
            }
            drawGraph();
        };
        closeBtn.setOnAction(e -> deleteAction.run());

        // Text Change Logic (Analyze for variables)
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            analyzeInputForVariables(newVal, promptContainer, sliderContainer);
            drawGraph();
        });

        // Enter Key -> New Input
        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addFunctionInputBox();
            }
        });

        inputWrapper.getChildren().addAll(inputBox, colorDot, buttonBox);
        mainRow.getChildren().addAll(inputWrapper, metaContainer);
        functionContainer.getChildren().add(mainRow);

        // Focus if it's not the very first load
        if (functionContainer.getChildren().size() > 1) {
            inputBox.requestFocus();
        }
    }

    // --- 4. VARIABLE & SLIDER LOGIC (From first.java) ---

    private void analyzeInputForVariables(String eq, HBox promptBox, VBox sliderBox) {
        promptBox.getChildren().clear();

        if (eq == null || eq.trim().isEmpty()) return;

        // 1. Find potential variables
        Set<String> foundVars = new HashSet<>();
        Pattern p = Pattern.compile("[A-Za-z]+"); // Match words
        Matcher m = p.matcher(eq);

        // Reserved words that are NOT variables
        Set<String> reserved = new HashSet<>(Arrays.asList(
                "x", "y", "sin", "cos", "tan", "asin", "acos", "atan",
                "sqrt", "cbrt", "log", "exp", "abs", "pi", "e"
        ));

        while (m.find()) {
            String var = m.group();
            if (!reserved.contains(var.toLowerCase())) {
                foundVars.add(var);
            }
        }

        // 2. Check which ones already have sliders IN THIS ROW or Globally?
        // Logic: Variables are global in value, but we need to see if we need to show an "Add Slider" button.
        // Simple approach: If variable is in 'foundVars' but not in 'variables' map, ask to create it.
        // If it IS in 'variables' map, check if we need to show the slider UI in this row?
        // Desmos style: Slider appears once. For simplicity here: Variables are global.
        // We show "Add slider" if the variable exists in text but not in our global map yet.

        for (String var : foundVars) {
            if (!variables.containsKey(var)) {
                Button addBtn = new Button("Add slider: " + var);
                addBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand;");
                addBtn.setOnAction(e -> {
                    createSlider(var, sliderBox);
                    promptBox.getChildren().remove(addBtn);
                });
                promptBox.getChildren().add(addBtn);
            }
        }
    }

    private void createSlider(String varName, VBox container) {
        if (variables.containsKey(varName)) return; // Already exists

        variables.put(varName, 1.0); // Default value

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #222; -fx-background-radius: 5; -fx-padding: 5;");

        Label nameLbl = new Label(varName);
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Consolas", 14));

        Slider slider = new Slider(-10, 10, 1);
        slider.setPrefWidth(150);

        TextField valInput = new TextField("1.00");
        valInput.setPrefWidth(60);
        valInput.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: gray;");

        // Listeners
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            variables.put(varName, newVal.doubleValue());
            valInput.setText(String.format("%.2f", newVal));
            drawGraph();
        });

        valInput.setOnAction(e -> {
            try {
                double val = Double.parseDouble(valInput.getText());
                slider.setValue(val);
                // Dynamic slider expansion
                if(val > slider.getMax()) slider.setMax(val + 10);
                if(val < slider.getMin()) slider.setMin(val - 10);
            } catch (Exception ex) {}
        });

        row.getChildren().addAll(nameLbl, slider, valInput);
        container.getChildren().add(row);
        drawGraph();
    }

    // --- 5. GRAPHING ENGINE (From first.java) ---

    private void handleZoom(ScrollEvent event) {
        event.consume();
        double zoomFactor = 1.1;
        // Zoom towards mouse pointer logic could be added here,
        // but sticking to center zoom from first.java for stability
        if (event.getDeltaY() > 0) scale *= zoomFactor;
        else scale /= zoomFactor;
        drawGraph();
    }

    private void drawGraph() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        // Center of the screen
        double centerX = width / 2.0 + offsetX;
        double centerY = height / 2.0 + offsetY;

        // A. Draw Smart Grid (from first.java)
        drawSmartGrid(centerX, centerY, width, height);

        // B. Draw Axes
        drawAxes(centerX, centerY, width, height);

        // C. Plot All Functions
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox row = (VBox) node;
                StackPane inputWrapper = (StackPane) row.getChildren().get(0);
                TextField inputBox = (TextField) inputWrapper.getChildren().get(0);
                Color color = (Color) row.getUserData();

                String eq = inputBox.getText();
                if (eq != null && !eq.trim().isEmpty()) {
                    plotEquation(eq, color, centerX, centerY, width, height);
                }
            }
        }
    }

    private void drawSmartGrid(double cx, double cy, double width, double height) {
        // Desmos-style Grid Logic
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

        // Vertical Lines (X-axis)
        double startX = Math.floor(-cx / scale / minorStep) * minorStep;
        for (double i = startX; i * scale + cx < width; i += minorStep) {
            double screenX = cx + (i * scale);
            boolean isMajor = Math.abs(i / majorStep - Math.round(i / majorStep)) < 0.001;

            gc.setStroke(isMajor ? majorColor : minorColor);
            gc.setLineWidth(isMajor ? 1.0 : 0.5);
            gc.strokeLine(screenX, 0, screenX, height);

            if (isMajor && Math.abs(i) > 0.0001) {
                gc.setFill(textColor);
                String label = (Math.abs(i - Math.round(i)) < 0.001) ?
                        String.format("%d", (long)Math.round(i)) : String.format("%.1f", i);
                gc.fillText(label, screenX - 5, cy + 20);
            }
        }

        // Horizontal Lines (Y-axis)
        double startY = Math.floor((cy - height) / scale / minorStep) * minorStep;
        double endY = Math.ceil(cy / scale / minorStep) * minorStep;

        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = cy - (i * scale);
            boolean isMajor = Math.abs(i / majorStep - Math.round(i / majorStep)) < 0.001;

            gc.setStroke(isMajor ? majorColor : minorColor);
            gc.setLineWidth(isMajor ? 1.0 : 0.5);
            gc.strokeLine(0, screenY, width, screenY);

            if (isMajor && Math.abs(i) > 0.0001) {
                gc.setFill(textColor);
                String label = (Math.abs(i - Math.round(i)) < 0.001) ?
                        String.format("%d", (long)Math.round(i)) : String.format("%.1f", i);
                gc.fillText(label, cx + 5, screenY + 5);
            }
        }
    }

    private void drawAxes(double cx, double cy, double width, double height) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, cy, width, cy);
        gc.strokeLine(cx, 0, cx, height);
    }

    private void plotEquation(String rawEq, Color color, double cx, double cy, double width, double height) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);

        // Pre-processing
        String eq = rawEq.toLowerCase().replace(" ", "");
        // Implicit Multiplication (2x -> 2*x, ax -> a*x)
        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2");
        // Variable-Variable mult (ax -> a*x) - Simple heuristics
        // This is tricky without a full parser, but let's try a safe subset
        // Avoid replacing 'sin', 'cos' etc.
        // For this demo, we rely on user typing * mostly, or simple 2x.

        try {
            if (eq.contains("=") && !eq.startsWith("y=") && !eq.startsWith("x=")) {
                // Implicit Plotting (Marching Squares)
                plotImplicit(eq, cx, cy, width, height);
            } else {
                // Explicit Plotting
                if (eq.startsWith("y=")) eq = eq.substring(2);

                // Build Expression
                ExpressionBuilder builder = new ExpressionBuilder(eq).variable("x");
                for (String var : variables.keySet()) builder.variable(var);
                Expression expr = builder.build();
                for (Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

                gc.beginPath();
                boolean first = true;

                // Plot pixel by pixel (Optimization: step size could be higher)
                for (double screenX = 0; screenX <= width; screenX++) {
                    double mathX = (screenX - cx) / scale;
                    try {
                        expr.setVariable("x", mathX);
                        double mathY = expr.evaluate();

                        if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                            first = true; continue;
                        }

                        double screenY = cy - (mathY * scale);

                        // Clipping check to avoid drawing lines across infinity
                        if (screenY < -height || screenY > height * 2) {
                            first = true; continue;
                        }

                        if (first) { gc.moveTo(screenX, screenY); first = false; }
                        else { gc.lineTo(screenX, screenY); }
                    } catch (Exception ex) { first = true; }
                }
                gc.stroke();
            }
        } catch (Exception e) {
            // SIlent fail on invalid equations
        }
    }

    // Implicit Plotting (Marching Squares from first.java)
    private void plotImplicit(String eq, double cx, double cy, double width, double height) {
        String[] parts = eq.split("=");
        if(parts.length != 2) return;

        String expressionStr = parts[0] + "-(" + parts[1] + ")";

        ExpressionBuilder builder = new ExpressionBuilder(expressionStr).variables("x", "y");
        for (String var : variables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        int resolution = 5; // Resolution for marching squares (lower = nicer but slower)

        for (double x = 0; x < width; x += resolution) {
            for (double y = 0; y < height; y += resolution) {
                // Evaluate corners
                double vBL = evaluate(expr, x, y + resolution, cx, cy);
                double vBR = evaluate(expr, x + resolution, y + resolution, cx, cy);
                double vTR = evaluate(expr, x + resolution, y, cx, cy);
                double vTL = evaluate(expr, x, y, cx, cy);

                // Marching Squares Logic
                // If signs differ, there is a line crossing

                // Simple version: interpolate linearly
                // Check edges
                if (isSignDifferent(vBL, vTL)) {
                    // Left edge
                    double t = -vBL / (vTL - vBL);
                    double ly = (y + resolution) - t * resolution;
                    // We need a partner point. Simplified: Draw points or short segments
                    // For full connected lines, we need the lookup table.
                    // first.java used a simplified line drawer:

                    // Let's copy the logic from first.java strictly
                    // ... logic copied below ...
                }

                // Re-implementing the exact block from first.java for reliability
                double[][][] hits = new double[4][2][];
                int hitCount = 0;

                if (isSignDifferent(vBL, vTL)) {
                    double t = -vBL / (vTL - vBL);
                    hits[hitCount++] = new double[][]{{x, y + resolution - (t * resolution)}};
                }
                if (isSignDifferent(vBL, vBR)) {
                    double t = -vBL / (vBR - vBL);
                    hits[hitCount++] = new double[][]{{x + (t * resolution), y + resolution}};
                }
                if (isSignDifferent(vBR, vTR)) {
                    double t = -vBR / (vTR - vBR);
                    hits[hitCount++] = new double[][]{{x + resolution, y + resolution - (t * resolution)}};
                }
                if (isSignDifferent(vTR, vTL)) {
                    double t = -vTR / (vTL - vTR);
                    hits[hitCount++] = new double[][]{{x + resolution - (t * resolution), y}};
                }

                if (hitCount == 2) {
                    gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                }
                else if (hitCount == 4) {
                    gc.strokeLine(hits[0][0][0], hits[0][0][1], hits[1][0][0], hits[1][0][1]);
                    gc.strokeLine(hits[2][0][0], hits[2][0][1], hits[3][0][0], hits[3][0][1]);
                }
            }
        }
    }

    private double evaluate(Expression expr, double screenX, double screenY, double cx, double cy) {
        double mathX = (screenX - cx) / scale;
        double mathY = (cy - screenY) / scale;
        expr.setVariable("x", mathX);
        expr.setVariable("y", mathY);
        try { return expr.evaluate(); } catch (Exception e) { return Double.NaN; }
    }

    private boolean isSignDifferent(double v1, double v2) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return (v1 > 0 && v2 < 0) || (v1 < 0 && v2 > 0) || v1 == 0 || v2 == 0;
    }

    // --- UTILS ---
    private Button createIconButton(String svgData, String colorHex, double size) {
        SVGPath path = new SVGPath();
        path.setContent(svgData);
        path.setFill(Color.web(colorHex));

        // Normalize size
        double originalWidth = path.getBoundsInLocal().getWidth();
        double scaleFactor = size / originalWidth;
        path.setScaleX(scaleFactor);
        path.setScaleY(scaleFactor);

        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}