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

public class first extends Application {

    // --- Graphing State Variables ---
    private double scale = 40; // Pixels per unit
    private double offsetX = 0;
    private double offsetY = 0;

    // --- Global Variables & Tracking ---
    private final Map<String, Double> globalVariables = new HashMap<>();
    private final Set<String> activeSliderVars = new HashSet<>();

    // --- UI Components ---
    private Canvas canvas;
    private GraphicsContext gc;
    private VBox functionContainer; // Holds all input boxes

    // Colors
    private final Color[] graphColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
    private int globalColorIndex = 0;

    @Override
    public void start(Stage stage) {
        // --- INTRO ANIMATION SETUP ---
        Pane root = new Pane();
        double width = 1000;
        double height = 700;
        root.setStyle("-fx-background-color: #002366;");

        Line hLine = new Line(0, height - 100, width, height - 100);
        hLine.setStroke(Color.WHITE);
        hLine.setStrokeWidth(5);
        hLine.setScaleX(0);

        Line vLine = new Line(width / 2, 0, width / 2, height);
        vLine.setStroke(Color.WHITE);
        vLine.setStrokeWidth(5);
        vLine.setScaleY(0);

        Text title = new Text("Graphify");
        title.setFont(Font.font("Pristina", FontWeight.BOLD, 85));
        title.setStroke(Color.WHITE);
        title.setStrokeWidth(0.3);
        title.setFill(Color.TRANSPARENT);
        title.setOpacity(0);
        title.setX((width / 2) - 120);
        title.setY(height / 2);

        root.getChildren().addAll(hLine, vLine, title);

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
        stage.setTitle("Desmos Clone (Merged)");
        stage.setScene(scene);
        stage.show();
    }

    // --- MAIN APP SCENE ---
    private Scene createMainScene(Stage stage) {
        BorderPane root = new BorderPane();

        // 1. SIDEBAR
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(400);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #121212; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        Label inputLabel = new Label("Enter Functions:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        functionContainer = new VBox(20);
        functionContainer.setStyle("-fx-background-color: transparent;");

        addFunctionInputBox(functionContainer, 0);

        ScrollPane scrollPane = new ScrollPane(functionContainer);
        VBox.setMargin(scrollPane, new Insets(20, 0, 0, 0));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        sidebar.getChildren().addAll(inputLabel, scrollPane);

        // 2. GRAPH AREA
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());

        canvas.widthProperty().addListener(evt -> drawGraph());
        canvas.heightProperty().addListener(evt -> drawGraph());
        canvas.setOnScroll(this::handleZoom);

        graphPane.getChildren().add(canvas);

        root.setLeft(sidebar);
        root.setCenter(graphPane);

        return new Scene(root, 1100, 750);
    }

    // --- INPUT BOX & SLIDER UI LOGIC ---
    private void addFunctionInputBox(VBox container, int insertIndex) {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");

        Color assignedColor = graphColors[globalColorIndex % graphColors.length];
        globalColorIndex++;
        mainRow.setUserData(assignedColor);

        VBox fieldAndPrompt = new VBox(0);
        fieldAndPrompt.setStyle("-fx-background-color: White; -fx-background-radius: 10; -fx-border-color: #9D00FF; -fx-border-width: 2; -fx-border-radius: 10;");

        TextField inputBox = new TextField();
        inputBox.setPromptText("Ex: ax + b");
        inputBox.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 15px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        inputBox.setPadding(new Insets(15, 80, 15, 35));

        HBox promptBox = new HBox(8);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.setPadding(new Insets(0, 10, 10, 35));
        promptBox.setVisible(false);
        promptBox.setManaged(false);

        fieldAndPrompt.getChildren().addAll(inputBox, promptBox);

        StackPane inputWrapper = new StackPane();

        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor);
        StackPane.setAlignment(colorDot, Pos.TOP_LEFT);
        StackPane.setMargin(colorDot, new Insets(20, 0, 0, 15));

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setMaxWidth(70);
        StackPane.setMargin(buttonBox, new Insets(10, 10, 0, 0));

        Button closeBtn = createIconButton(
                "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                "gray", 18
        );
        closeBtn.setOnMouseEntered(e -> ((javafx.scene.shape.SVGPath) closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((javafx.scene.shape.SVGPath) closeBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().add(closeBtn);
        inputWrapper.getChildren().addAll(fieldAndPrompt, colorDot, buttonBox);

        VBox sliderContainer = new VBox(5);
        sliderContainer.setPadding(new Insets(5, 0, 0, 20));

        Runnable deleteAction = () -> {
            if (container.getChildren().size() > 1) {
                container.getChildren().remove(mainRow);
                drawGraph();
            } else {
                inputBox.clear();
                sliderContainer.getChildren().clear();
                drawGraph();
            }
        };
        closeBtn.setOnAction(e -> deleteAction.run());

        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            updateSliderPrompt(newVal, promptBox, sliderContainer, inputBox);
            drawGraph();
        });

        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int currentIndex = container.getChildren().indexOf(mainRow);
                addFunctionInputBox(container, currentIndex + 1);
            }
            if (event.getCode() == KeyCode.BACK_SPACE && inputBox.getText().isEmpty()) {
                int index = container.getChildren().indexOf(mainRow);
                if (index > 0) {
                    VBox prevRow = (VBox) container.getChildren().get(index - 1);
                    StackPane prevWrapper = (StackPane) prevRow.getChildren().get(0);
                    VBox prevFieldWrapper = (VBox) prevWrapper.getChildren().get(0);
                    ((TextField) prevFieldWrapper.getChildren().get(0)).requestFocus();
                    deleteAction.run();
                }
            }
        });

        mainRow.getChildren().addAll(inputWrapper, sliderContainer);

        if (insertIndex >= 0 && insertIndex <= container.getChildren().size()) container.getChildren().add(insertIndex, mainRow);
        else container.getChildren().add(mainRow);

        inputBox.requestFocus();
    }

    private Button createIconButton(String svgData, String colorHex, double size) {
        javafx.scene.shape.SVGPath path = new javafx.scene.shape.SVGPath();
        path.setContent(svgData);
        path.setFill(Color.web(colorHex));
        double scaleFactor = size / path.getBoundsInLocal().getWidth();
        path.setScaleX(scaleFactor);
        path.setScaleY(scaleFactor);
        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        return btn;
    }

    // --- SLIDER PROMPT ---
    private void updateSliderPrompt(String eq, HBox promptBox, VBox sliderContainer, TextField inputBox) {
        Set<String> foundVars = new HashSet<>();
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(eq.toLowerCase());
        Set<String> reserved = Set.of("x", "y", "sin", "cos", "tan", "log", "sqrt", "abs", "pi", "e", "exp");

        while (m.find()) {
            String var = m.group();
            if (!reserved.contains(var)) foundVars.add(var);
        }

        List<String> missingSliders = new ArrayList<>();
        for (String var : foundVars) {
            if (!activeSliderVars.contains(var)) missingSliders.add(var);
        }

        promptBox.getChildren().clear();

        if (missingSliders.isEmpty()) {
            promptBox.setVisible(false);
            promptBox.setManaged(false);
            return;
        }

        promptBox.setVisible(true);
        promptBox.setManaged(true);

        Label label = new Label("add slider:");
        label.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-font-weight: bold;");
        promptBox.getChildren().add(label);

        for (String var : missingSliders) {
            Button btn = new Button(var);
            btn.setStyle("-fx-background-color: #eee; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 2 6 2 6;");
            btn.setOnAction(e -> addActualSlider(var, sliderContainer, promptBox, inputBox));
            promptBox.getChildren().add(btn);
        }

        if (missingSliders.size() > 1) {
            Button allBtn = new Button("all");
            allBtn.setStyle("-fx-background-color: #4a8af4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 2 6 2 6;");
            allBtn.setOnAction(e -> {
                for (String var : missingSliders) addActualSlider(var, sliderContainer, promptBox, inputBox);
            });
            promptBox.getChildren().add(allBtn);
        }
    }

    private void addActualSlider(String varName, VBox sliderContainer, HBox promptBox, TextField inputBox) {
        if (activeSliderVars.contains(varName)) return;

        globalVariables.putIfAbsent(varName, 1.0);
        activeSliderVars.add(varName);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #222; -fx-background-radius: 5; -fx-padding: 8; -fx-border-color: #444; -fx-border-radius: 5;");

        Label nameLbl = new Label(varName + " =");
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 14));

        TextField valInput = new TextField(String.format("%.2f", globalVariables.get(varName)));
        valInput.setPrefWidth(60);
        valInput.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 12px;");

        Button closeBtn = createIconButton("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", "gray", 14);

        Slider slider = new Slider(-10, 10, globalVariables.get(varName));
        slider.setPrefWidth(120);

        Runnable updateRange = () -> {
            try {
                double val = Double.parseDouble(valInput.getText());
                globalVariables.put(varName, val);
                double rangeSpan = 10;
                slider.setMin(val - rangeSpan);
                slider.setMax(val + rangeSpan);
                slider.setValue(val);
                drawGraph();
            } catch (NumberFormatException ex) {
                valInput.setText(String.format("%.2f", globalVariables.get(varName)));
            }
        };

        valInput.setOnAction(e -> updateRange.run());
        valInput.focusedProperty().addListener((obs, o, n) -> { if (!n) updateRange.run(); });

        slider.valueProperty().addListener((obs, o, n) -> {
            globalVariables.put(varName, n.doubleValue());
            valInput.setText(String.format("%.2f", n));
            drawGraph();
        });

        closeBtn.setOnAction(e -> {
            sliderContainer.getChildren().remove(row);
            activeSliderVars.remove(varName);
            updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
            drawGraph();
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(nameLbl, valInput, slider, spacer, closeBtn);
        sliderContainer.getChildren().add(row);

        updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
        drawGraph();
    }

    // --- GRAPH DRAW ---
    private void drawGraph() {
        if (canvas == null || gc == null) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width == 0 || height == 0) return;

        gc.clearRect(0, 0, width, height);

        // IMPORTANT: ensure no dash is set anywhere accidentally
        gc.setLineDashes(null);

        drawSmartGrid(width, height);
        drawAxes(width, height);

        double centerX = width / 2.0 + offsetX;
        double centerY = height / 2.0 + offsetY;

        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox mainRow) {
                Color rowColor = (Color) mainRow.getUserData();

                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                VBox fieldAndPrompt = (VBox) inputWrapper.getChildren().get(0);
                TextField inputBox = (TextField) fieldAndPrompt.getChildren().get(0);

                String equation = inputBox.getText();
                if (equation != null && !equation.trim().isEmpty()) {
                    plotEquation(equation, rowColor, centerX, centerY, width, height);
                }
            }
        }
    }

    // --- Smart Grid ---
    private void drawSmartGrid(double width, double height) {
        double centerX = width / 2.0 + offsetX;
        double centerY = height / 2.0 + offsetY;

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
        double centerX = width / 2.0 + offsetX;
        double centerY = height / 2.0 + offsetY;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);
    }

    // --- MAIN PLOT ROUTER ---
    private void plotEquation(String eqStr, Color color, double cx, double cy, double width, double height) {
        gc.setStroke(color);
        gc.setLineWidth(2.5);
        gc.setLineDashes(null);

        String eq = eqStr.toLowerCase().replace(" ", "");
        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2"); // 2x -> 2*x

        try {
            if (eq.startsWith("y=") && !eq.contains("x=")) {
                plotStandard(cx, cy, width, height, eq.substring(2));
            } else if (eq.startsWith("x=") && !eq.contains("y=")) {
                plotInverse(cx, cy, width, height, eq.substring(2));
            } else if (eq.contains("=")) {
                // ✅ NEW: linear implicit হলে marching squares না, direct solid line
                if (!tryPlotLinearImplicit(cx, cy, width, height, eq)) {
                    plotImplicit(cx, cy, width, height, eq);
                }
            } else {
                plotStandard(cx, cy, width, height, eq);
            }
        } catch (Exception ignored) {}
    }

    private void plotStandard(double cx, double cy, double width, double height, String function) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable("x");
        for (String var : globalVariables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

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
        ExpressionBuilder builder = new ExpressionBuilder(function).variable("y");
        for (String var : globalVariables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

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

    // ✅ NEW: detect ax+by+c=0 and draw solid line
    private boolean tryPlotLinearImplicit(double cx, double cy, double width, double height, String eq) {
        String[] parts = eq.split("=");
        if (parts.length != 2) return false;

        String expressionStr = parts[0] + "-(" + parts[1] + ")";

        ExpressionBuilder builder = new ExpressionBuilder(expressionStr).variables("x", "y");
        for (String var : globalVariables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        // f(x,y)=ax+by+c হলে:
        // c=f(0,0), a=f(1,0)-c, b=f(0,1)-c
        double c0 = evalMath(expr, 0, 0);
        double a = evalMath(expr, 1, 0) - c0;
        double b = evalMath(expr, 0, 1) - c0;

        if (Double.isNaN(c0) || Double.isNaN(a) || Double.isNaN(b)) return false;

        // Verify linearity (a few checks)
        double eps = 1e-6;
        boolean ok =
                nearlyEqual(evalMath(expr, 2, 0), 2 * a + c0, eps) &&
                        nearlyEqual(evalMath(expr, 0, 2), 2 * b + c0, eps) &&
                        nearlyEqual(evalMath(expr, 1, 1), a + b + c0, eps);

        if (!ok) return false;

        // Draw solid line
        double tiny = 1e-9;
        if (Math.abs(b) > tiny) {
            // y = (-a x - c)/b
            gc.beginPath();
            boolean first = true;
            for (double screenX = 0; screenX < width; screenX++) {
                double x = (screenX - cx) / scale;
                double y = (-a * x - c0) / b;
                double screenY = cy - (y * scale);

                if (screenY < -500 || screenY > height + 500) { first = true; continue; }
                if (first) { gc.moveTo(screenX, screenY); first = false; }
                else { gc.lineTo(screenX, screenY); }
            }
            gc.stroke();
            return true;
        } else if (Math.abs(a) > tiny) {
            // vertical line: ax + c = 0 => x = -c/a
            double x = -c0 / a;
            double screenX = cx + x * scale;
            gc.strokeLine(screenX, 0, screenX, height);
            return true;
        }

        return false; // degenerate case
    }

    private double evalMath(Expression expr, double x, double y) {
        try {
            expr.setVariable("x", x);
            expr.setVariable("y", y);
            return expr.evaluate();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private boolean nearlyEqual(double v1, double v2, double eps) {
        if (Double.isNaN(v1) || Double.isNaN(v2)) return false;
        return Math.abs(v1 - v2) <= eps;
    }

    // --- GENERAL IMPLICIT (marching squares) ---
    private void plotImplicit(double cx, double cy, double width, double height, String eq) {
        gc.setLineWidth(2.0);

        String[] parts = eq.split("=");
        String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq;

        ExpressionBuilder builder = new ExpressionBuilder(expressionStr).variables("x", "y");
        for (String var : globalVariables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : globalVariables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        int res = 2; // was 4; smaller = smoother
        for (double x = 0; x < width; x += res) {
            for (double y = 0; y < height; y += res) {
                double vBL = evaluate(expr, x, y + res, cx, cy);
                double vBR = evaluate(expr, x + res, y + res, cx, cy);
                double vTR = evaluate(expr, x + res, y, cx, cy);
                double vTL = evaluate(expr, x, y, cx, cy);

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

    private double evaluate(Expression expr, double screenX, double screenY, double cx, double cy) {
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
        if (event.getDeltaY() > 0) scale *= zoomFactor;
        else scale /= zoomFactor;
        drawGraph();
    }

    public static void main(String[] args) {
        launch(args);
    }
}