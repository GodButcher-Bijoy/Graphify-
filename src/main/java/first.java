import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class first extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    // --- State Variables ---
    private double scale = 40; // Pixels per unit
    private double offsetX = 0; // Panning (Future use)
    private double offsetY = 0;
    private String currentEquation = "";

    // ভেরিয়েবল স্টোর (Variable Name -> Value)
    private Map<String, Double> variables = new HashMap<>();

    // অ্যাক্টিভ স্লাইডার বক্সগুলো ট্র্যাক করার জন্য
    private Map<String, VBox> activeSliders = new HashMap<>();

    // UI Components
    private Canvas canvas;
    private GraphicsContext gc;
    private VBox sliderContainer;

    // [NEW] স্লাইডার প্রম্পট বক্স (যেখানে বাটনগুলো আসবে)
    private HBox sliderPromptBox;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // 1. Center: The Graph Canvas
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Zoom Logic
        canvas.setOnScroll(this::handleZoom);

        // Mouse Hover
        canvas.setOnMouseMoved(e -> {
            drawGraph();
            drawHoverPoint(e.getX(), e.getY());
        });

        root.setCenter(canvas);

        // 2. Top: Input Field + Prompt Box [UPDATED]
        VBox topContainer = new VBox(5);
        topContainer.setPadding(new Insets(15));
        topContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #0e88b7; -fx-border-width: 0 0 1 0;");

        TextField equationInput = new TextField();
        equationInput.setPromptText("Enter equation...");
        equationInput.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ccc; -fx-border-radius: 10;");
        equationInput.setPrefWidth(500);

        // [NEW] Prompt Box Initialization
        sliderPromptBox = new HBox(10);
        sliderPromptBox.setAlignment(Pos.CENTER_LEFT);
        sliderPromptBox.setPadding(new Insets(0, 0, 0, 10));
        sliderPromptBox.setPrefHeight(0); // শুরুতে হাইড থাকবে

        // Input Listener [UPDATED Logic]
        equationInput.textProperty().addListener((obs, oldVal, newVal) -> {
            currentEquation = newVal;
            analyzeEquation(newVal); // এখন আর সরাসরি এড করবে না, জাস্ট এনালাইজ করবে
            drawGraph();
        });

        topContainer.getChildren().addAll(equationInput, sliderPromptBox);
        root.setTop(topContainer);

        // 3. Right: Sidebar for Sliders
        sliderContainer = new VBox(10);
        sliderContainer.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(sliderContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f0f0; -fx-border-color: transparent;");
        scrollPane.setPrefWidth(300);
        root.setRight(scrollPane);

        drawGraph();

        Scene scene = new Scene(root, WIDTH + 300, HEIGHT);
        stage.setTitle("Desmos Clone (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    // --- ১. ইকুয়েশন অ্যানালাইসিস এবং প্রম্পট তৈরি [NEW] ---
    private void analyzeEquation(String eq) {
        // ১. ইকুয়েশন থেকে সব ভেরিয়েবল খুঁজে বের করা
        Set<String> foundVars = new HashSet<>();
        Pattern p = Pattern.compile("[A-z]");
        Matcher m = p.matcher(eq);
        Set<String> reserved = Set.of("x", "X", "Y", "y", "sin", "cos", "tan", "log", "sqrt", "abs", "pi", "e", "exp");

        while (m.find()) {
            String var = m.group();
            if (!reserved.contains(var)) foundVars.add(var);
        }

        // ২. বের করা কোনগুলোর স্লাইডার নেই (Missing Sliders)
        List<String> missingSliders = new ArrayList<>();
        for (String var : foundVars) {
            if (!activeSliders.containsKey(var)) {
                missingSliders.add(var);
            }
        }

        // ৩. প্রম্পট বার আপডেট করা
        updatePromptBar(missingSliders);
    }

    // --- ২. প্রম্পট বার আপডেট লজিক [NEW] ---
    private void updatePromptBar(List<String> missingVars) {
        sliderPromptBox.getChildren().clear();

        if (missingVars.isEmpty()) {
            sliderPromptBox.setPrefHeight(0);
            return;
        }

        sliderPromptBox.setPrefHeight(40);

        // Label
        Label label = new Label("add slider:");
        label.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        sliderPromptBox.getChildren().add(label);

        // Individual Buttons (e.g., "a", "b")
        for (String var : missingVars) {
            Button btn = new Button(var);
            btn.setStyle("-fx-background-color: #eee; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");

            btn.setOnAction(e -> {
                addSliderUI(var); // স্লাইডার এড করো
                analyzeEquation(currentEquation); // প্রম্পট রিফ্রেশ করো
                drawGraph();
            });
            sliderPromptBox.getChildren().add(btn);
        }

        // "all" Button
        if (missingVars.size() > 1) {
            Button allBtn = new Button("all");
            allBtn.setStyle("-fx-background-color: #4a8af4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");

            allBtn.setOnAction(e -> {
                for (String var : missingVars) {
                    addSliderUI(var);
                }
                analyzeEquation(currentEquation);
                drawGraph();
            });
            sliderPromptBox.getChildren().add(allBtn);
        }
    }

    // --- ৩. স্লাইডার তৈরি করা (UI Logic) [UPDATED Close Button] ---
    private void addSliderUI(String varName) {
        if (activeSliders.containsKey(varName)) return;

        // ডিফল্ট ভ্যালু সেট
        variables.putIfAbsent(varName, 1.0);

        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0); -fx-border-color: #eee; -fx-border-radius: 8;");

        // Top Row: Label | TextField | Close Button
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(varName);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Editable Value Field
        TextField valField = new TextField(String.format("%.2f", variables.get(varName)));
        valField.setPrefWidth(60);
        valField.setStyle("-fx-font-size: 12px;");

        // Close Button (X)
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999; -fx-font-size: 10px; -fx-cursor: hand;");

        // [IMPORTANT] ক্লোজ করলে স্লাইডার মুছে যাবে এবং প্রম্পট বারে আবার অপশন আসবে
        closeBtn.setOnAction(e -> {
            activeSliders.remove(varName);
            // ভেরিয়েবল ম্যাপ থেকে রিমুভ করছি না (Desmos Behavior)
            sliderContainer.getChildren().remove(card);

            analyzeEquation(currentEquation); // রিফ্রেশ
            drawGraph();
        });

        HBox.setHgrow(valField, Priority.NEVER);
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(label, valField, spacer, closeBtn);

        // Slider Component
        Slider slider = new Slider(-10, 10, variables.get(varName));
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        // Logic: Slider to TextField & Graph
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!slider.isValueChanging() && !valField.isFocused()) {
                valField.setText(String.format("%.2f", newVal));
                variables.put(varName, newVal.doubleValue());
                drawGraph();
            }
        });

        // Logic: TextField to Slider
        valField.setOnAction(e -> updateFromField(valField, slider, varName));
        valField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) updateFromField(valField, slider, varName);
        });

        // Live Drag Update
        slider.valueProperty().addListener((obs, o, n) -> {
            variables.put(varName, n.doubleValue());
            valField.setText(String.format("%.2f", n));
            drawGraph();
        });

        card.getChildren().addAll(topRow, slider);

        activeSliders.put(varName, card);
        sliderContainer.getChildren().add(card);
    }

    private void updateFromField(TextField field, Slider slider, String varName) {
        try {
            double val = Double.parseDouble(field.getText());
            variables.put(varName, val);

            // Auto-Range Logic
            double rangeSpan = 10;
            slider.setMin(val - rangeSpan);
            slider.setMax(val + rangeSpan);
            slider.setValue(val);

            drawGraph();
        } catch (NumberFormatException ex) {
            field.setText(String.format("%.2f", variables.get(varName)));
        }
    }

    // --- গ্রাফ আঁকার লজিক (UNCHANGED) ---
    private void drawGraph() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2.0 + offsetX;
        double centerY = canvas.getHeight() / 2.0 + offsetY;

        drawSmartGrid(centerX, centerY);
        drawAxes(centerX, centerY);

        if (!currentEquation.isEmpty()) {
            plotEquation(centerX, centerY);
        }
    }

    // --- Updated Smart Grid (Desmos Style 1-2-5 Rule) ---
    private void drawSmartGrid(double cx, double cy) {
        // Desmos Colors
        Color majorColor = Color.web("#bfbfbf"); // Darker Gray
        Color minorColor = Color.web("#e6e6e6"); // Very Light Gray
        Color textColor = Color.web("#666666");  // Text Color

        gc.setFont(new Font("Arial", 12));

        // 1. Calculate Optimal Step (The 1-2-5 Rule)
        // আমরা চাই প্রতি ~৮০-১০০ পিক্সেল পর পর একটি মেজর লাইন থাকুক
        double targetGridPixelWidth = 100;

        // বর্তমান স্কেলে ওই ১০০ পিক্সেল মানে গ্রাফের ইউনিটে কত?
        double minStep = (canvas.getWidth() / scale) * (targetGridPixelWidth / canvas.getWidth());

        // ম্যাগনিচিউড বের করা (যেমন: 0.1, 1, 10, 100...)
        double magnitude = Math.pow(10, Math.floor(Math.log10(minStep)));
        double residual = minStep / magnitude;

        // Desmos এর স্ট্যান্ডার্ড স্টেপ সিলেকশন (1, 2, 5)
        double majorStep;
        if (residual > 5) majorStep = 10 * magnitude;
        else if (residual > 2) majorStep = 5 * magnitude;
        else if (residual > 1) majorStep = 2 * magnitude;
        else majorStep = magnitude;

        // 2. Calculate Minor Step (Subdivisions)
        // মেজর স্টেপ ২ হলে ৪ ভাগ, বাকি সব ক্ষেত্রে ৫ ভাগ
        int subdivisions = (Math.abs(majorStep / magnitude - 2) < 0.001) ? 4 : 5;
        double minorStep = majorStep / subdivisions;

        // --- X-Axis Grid Drawing ---
        // স্ক্রিনের বাম পাশ থেকে শুরু
        double startX = Math.floor(-cx / scale / minorStep) * minorStep;

        for (double i = startX; i * scale + cx < canvas.getWidth(); i += minorStep) {
            double screenX = cx + (i * scale);

            // মেজর লাইন কিনা চেক করা (Floating point error fix করার জন্য epsilon ব্যবহার)
            // i-কে majorStep দিয়ে ভাগ করলে যদি পূর্ণসংখ্যা হয়, তবে এটি মেজর লাইন
            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) {
                gc.setStroke(majorColor);
                gc.setLineWidth(1.0);
            } else {
                gc.setStroke(minorColor);
                gc.setLineWidth(0.7);
            }

            gc.strokeLine(screenX, 0, screenX, canvas.getHeight());

            // শুধু মেজর লাইনে নাম্বার বসবে (0 বাদ দিয়ে)
            if (isMajor && Math.abs(i) > 0.001) {
                gc.setFill(textColor);
                // সংখ্যাটি যদি পূর্ণসংখ্যা হয় (যেমন 1.0) তবে "1" দেখাবে, নাহলে "1.5"
                String label = (Math.abs(i - Math.round(i)) < 0.001) ?
                        String.format("%d", (long) Math.round(i)) :
                        String.format("%.1f", i);
                gc.fillText(label, screenX - 4, cy + 20);
            }
        }

        // --- Y-Axis Grid Drawing ---
        double startY = Math.floor((cy - canvas.getHeight()) / scale / minorStep) * minorStep;
        double endY = Math.ceil(cy / scale / minorStep) * minorStep;

        for (double i = startY; i <= endY; i += minorStep) {
            double screenY = cy - (i * scale);

            double remainder = Math.abs(i / majorStep - Math.round(i / majorStep));
            boolean isMajor = remainder < 0.001;

            if (isMajor) {
                gc.setStroke(majorColor);
                gc.setLineWidth(1.0);
            } else {
                gc.setStroke(minorColor);
                gc.setLineWidth(0.7);
            }

            gc.strokeLine(0, screenY, canvas.getWidth(), screenY);

            if (isMajor && Math.abs(i) > 0.001) {
                gc.setFill(textColor);
                String label = (Math.abs(i - Math.round(i)) < 0.001) ?
                        String.format("%d", (long) Math.round(i)) :
                        String.format("%.1f", i);
                gc.fillText(label, cx + 8, screenY + 5);
            }
        }
    }

    // drawYLine মেথডটি এখন আর আলাদা করে লাগবে না কারণ drawSmartGrid এর ভেতরেই লজিক দিয়ে দিয়েছি।
    // তবে এরর এড়াতে আপনি চাইলে নিচের মেথডটি খালি রাখতে পারেন বা মুছে দিতে পারেন,
    // কারণ উপরের কোডে আমি সরাসরি লুপ চালিয়ে দিয়েছি।
    private void drawYLine(double screenY, double cx, double val) {
        // Deprecated inside this logic (উপরেই মার্জ করা হয়েছে)
    }

    // --- ইকুয়েশন প্লটিং (UNCHANGED) ---
    private void plotEquation(double cx, double cy) {
        gc.setStroke(Color.web("#20a513"));
        gc.setLineWidth(2.5);

        String eq = currentEquation.toLowerCase().replace(" ", "");
        eq = eq.replaceAll("(\\d)([a-z])", "$1*$2");

        try {
            if (eq.startsWith("y=") && !eq.contains("x=")) {
                plotStandard(cx, cy, eq.substring(2));
            } else if (eq.startsWith("x=") && !eq.contains("y=")) {
                plotInverse(cx, cy, eq.substring(2));
            } else if (eq.contains("=")) {
                plotImplicit(cx, cy, eq);
            } else {
                plotStandard(cx, cy, eq);
            }
        } catch (Exception e) {
        }
    }

    private void plotStandard(double cx, double cy, String function) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable("x");
        for (String var : variables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        gc.beginPath();
        boolean first = true;
        for (double screenX = 0; screenX < canvas.getWidth(); screenX++) {
            double mathX = (screenX - cx) / scale;
            expr.setVariable("x", mathX);
            double mathY = expr.evaluate();

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) continue;
            double screenY = cy - (mathY * scale);

            if (screenY < -500 || screenY > canvas.getHeight() + 500) {
                first = true; continue;
            }
            if (first) { gc.moveTo(screenX, screenY); first = false; }
            else { gc.lineTo(screenX, screenY); }
        }
        gc.stroke();
    }

    private void plotInverse(double cx, double cy, String function) {
        ExpressionBuilder builder = new ExpressionBuilder(function).variable("y");
        for (String var : variables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        gc.beginPath();
        boolean first = true;
        for (double screenY = 0; screenY < canvas.getHeight(); screenY++) {
            double mathY = (cy - screenY) / scale;
            expr.setVariable("y", mathY);
            double mathX = expr.evaluate();

            if (Double.isNaN(mathX) || Double.isInfinite(mathX)) continue;
            double screenX = cx + (mathX * scale);

            if (screenX < -500 || screenX > canvas.getWidth() + 500) {
                first = true; continue;
            }
            if (first) { gc.moveTo(screenX, screenY); first = false; }
            else { gc.lineTo(screenX, screenY); }
        }
        gc.stroke();
    }

    // --- Marching Squares Algorithm (UNCHANGED) ---
    private void plotImplicit(double cx, double cy, String eq) {
        gc.setStroke(Color.web("#d92b2b"));
        gc.setLineWidth(2.0);

        String[] parts = eq.split("=");
        String expressionStr = (parts.length == 2) ? parts[0] + "-(" + parts[1] + ")" : eq;

        ExpressionBuilder builder = new ExpressionBuilder(expressionStr).variables("x", "y");
        for (String var : variables.keySet()) builder.variable(var);
        Expression expr = builder.build();
        for (Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

        int res = 4;

        for (double x = 0; x < canvas.getWidth(); x += res) {
            for (double y = 0; y < canvas.getHeight(); y += res) {
                double vBL = evaluate(expr, x, y + res, cx, cy);
                double vBR = evaluate(expr, x + res, y + res, cx, cy);
                double vTR = evaluate(expr, x + res, y, cx, cy);
                double vTL = evaluate(expr, x, y, cx, cy);

                double[][][] hits = new double[4][2][];
                int hitCount = 0;

                if (isSignDifferent(vBL, vTL)) {
                    double t = -vBL / (vTL - vBL);
                    hits[hitCount++] = new double[][]{{x, y + res - (t * res)}};
                }
                if (isSignDifferent(vBL, vBR)) {
                    double t = -vBL / (vBR - vBL);
                    hits[hitCount++] = new double[][]{{x + (t * res), y + res}};
                }
                if (isSignDifferent(vBR, vTR)) {
                    double t = -vBR / (vTR - vBR);
                    hits[hitCount++] = new double[][]{{x + res, y + res - (t * res)}};
                }
                if (isSignDifferent(vTR, vTL)) {
                    double t = -vTR / (vTL - vTR);
                    hits[hitCount++] = new double[][]{{x + res - (t * res), y}};
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
        try {
            return expr.evaluate();
        } catch (Exception e) {
            return Double.NaN;
        }
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

    private void drawAxes(double cx, double cy) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, cy, canvas.getWidth(), cy);
        gc.strokeLine(cx, 0, cx, canvas.getHeight());
    }

    private void drawHoverPoint(double mx, double my) {
        // Hover logic (Empty)
    }

    public static void main(String[] args) {
        launch(args);
    }
}