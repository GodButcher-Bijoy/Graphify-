package org.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class MainApp extends Application {

    // Global variables for Graphing
    private Canvas canvas;
    private GraphicsContext gc;
    private VBox functionContainer; // Holds all input boxes
    private static final double SCALE = 40; // 1 unit = 40 pixels


    // Colors for different graphs (cycle through these)
    private final Color[] graphColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,Color.CYAN};
    private int globalColorIndex = 0;

    @Override
    public void start(Stage stage) {
        // --- INTRO ANIMATION SETUP ---
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

        Text title = new Text("Graphify");
        title.setFont(Font.font("Pristina", FontWeight.BOLD, 85));
        title.setStroke(Color.WHITE);
        title.setStrokeWidth(0.3);
        title.setFill(Color.TRANSPARENT);
        title.setOpacity(0);
        title.setX((width / 2) - 120);
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
        stage.setTitle("Graphify Intro");
        stage.setScene(scene);
        stage.show();
    }

    // --- MAIN APP SCENE ---
    private Scene createMainScene(Stage stage) {
        BorderPane root = new BorderPane();

        // 1. SIDEBAR
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(100, 30, 30, 30)); // Your specific padding
        sidebar.setPrefWidth(400);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #121212; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        Label inputLabel = new Label("Enter Functions:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        // Initialize Function Container
        functionContainer = new VBox(40); // Your specific spacing
        functionContainer.setStyle("-fx-background-color: transparent;");

        // Add initial inputs
        for (int i = 0; i < 3; i++) {
            addFunctionInputBox(functionContainer);
        }

        ScrollPane scrollPane = new ScrollPane(functionContainer);
        VBox.setMargin(scrollPane, new Insets(80, 0, 0, 0)); // Your margin
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // X-Range Inputs
        Label rangeLabel = new Label("X Range (Min, Max):");
        rangeLabel.setTextFill(Color.LIGHTGRAY);
        HBox rangeBox = new HBox(10);
        TextField minInput = new TextField("-10");
        TextField maxInput = new TextField("10");
        String rangeStyle = "-fx-background-color: #1F1F1F; -fx-text-fill: white; -fx-border-color: gray; -fx-border-radius: 5;";
        minInput.setStyle(rangeStyle); maxInput.setStyle(rangeStyle);
        rangeBox.getChildren().addAll(minInput, maxInput);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        sidebar.getChildren().addAll(inputLabel, scrollPane, rangeLabel, rangeBox);

        // 2. GRAPH AREA
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());

        canvas.widthProperty().addListener(evt -> drawGraph());
        canvas.heightProperty().addListener(evt -> drawGraph());

        canvas.setOnMouseMoved(e -> {
            drawGraph();
            checkAndDrawHoverPoint(e);
        });

        graphPane.getChildren().add(canvas);

        root.setLeft(sidebar);
        root.setCenter(graphPane);

        return new Scene(root, 1000, 700);
    }

    // --- GRAPHING LOGIC ---

    private void drawGraph() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);
        drawGrid(width, height);
        drawAxes(width, height);

        // এখানে int colorIndex = 0; আর লাগবে না, মুছে ফেলো।

        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;

                // ১. ইমপ্লিসিটলি সেভ করা কালারটা বের করা
                Color rowColor = (Color) mainRow.getUserData();
                if (rowColor == null) rowColor = Color.BLACK; // Fallback

                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                TextField inputBox = (TextField) inputWrapper.getChildren().get(0);
                String equation = inputBox.getText();

                VBox sliderContainer = (VBox) mainRow.getChildren().get(1);
                Map<String, Double> variables = new HashMap<>();

                for(javafx.scene.Node sliderRow : sliderContainer.getChildren()) {
                    if(sliderRow instanceof HBox) {
                        HBox row = (HBox) sliderRow;
                        Label lbl = (Label) row.getChildren().get(0);
                        String varName = lbl.getText().replace(" = ", "");
                        TextField valInput = (TextField) row.getChildren().get(2);
                        try {
                            variables.put(varName, Double.parseDouble(valInput.getText()));
                        } catch (Exception e) {}
                    }
                }

                if (!equation.trim().isEmpty()) {
                    // ২. এই কালার দিয়েই গ্রাফ আঁকা হবে
                    gc.setStroke(rowColor);
                    plotEquation(equation, variables, width, height);
                    // colorIndex++; // এই লাইন এখন দরকার নেই
                }
            }
        }
    }

    private void plotEquation(String equation, Map<String, Double> variables, double width, double height) {
        String cleanEq = cleanEquation(equation);
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        try {
            ExpressionBuilder builder = new ExpressionBuilder(cleanEq).variable("x");
            // ডাইনামিক ভেরিয়েবল ডিক্লেয়ার
            for(String var : variables.keySet()) builder.variable(var);

            Expression expr = builder.build();
            // মান সেট করা
            for(Map.Entry<String, Double> entry : variables.entrySet()) {
                expr.setVariable(entry.getKey(), entry.getValue());
            }

            gc.setLineWidth(2.5);
            gc.beginPath();
            boolean firstPoint = true;

            for (double screenX = 0; screenX <= width; screenX++) {
                double mathX = (screenX - centerX) / SCALE;
                try {
                    expr.setVariable("x", mathX);
                    double mathY = expr.evaluate();

                    if (Double.isNaN(mathY) || Double.isInfinite(mathY)) continue;
                    double screenY = centerY - (mathY * SCALE);

                    if (screenY < -height || screenY > height * 2) {
                        firstPoint = true; continue;
                    }

                    if (firstPoint) { gc.moveTo(screenX, screenY); firstPoint = false; }
                    else { gc.lineTo(screenX, screenY); }
                } catch (Exception e) {}
            }
            gc.stroke();
        } catch (Exception e) { }
    }

    private void checkAndDrawHoverPoint(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double mathX = (mouseX - centerX) / SCALE;

        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox mainRow = (VBox) node;
                StackPane inputWrapper = (StackPane) mainRow.getChildren().get(0);
                TextField inputBox = (TextField) inputWrapper.getChildren().get(0);
                String equation = inputBox.getText();

                if (equation.trim().isEmpty()) continue;

                // ভেরিয়েবল বের করা (Hover এর জন্যও দরকার)
                VBox sliderContainer = (VBox) mainRow.getChildren().get(1);
                Map<String, Double> variables = new HashMap<>();
                for(javafx.scene.Node sliderRow : sliderContainer.getChildren()) {
                    if(sliderRow instanceof HBox) {
                        HBox row = (HBox) sliderRow;
                        Label lbl = (Label) row.getChildren().get(0);
                        String varName = lbl.getText().replace(" = ", "");
                        TextField valInput = (TextField) row.getChildren().get(2);
                        try { variables.put(varName, Double.parseDouble(valInput.getText())); } catch (Exception ex) {}
                    }
                }

                try {
                    String cleanEq = cleanEquation(equation);
                    ExpressionBuilder builder = new ExpressionBuilder(cleanEq).variable("x");
                    for(String var : variables.keySet()) builder.variable(var);
                    Expression expr = builder.build();
                    for(Map.Entry<String, Double> entry : variables.entrySet()) expr.setVariable(entry.getKey(), entry.getValue());

                    expr.setVariable("x", mathX);
                    double mathY = expr.evaluate();
                    double graphPixelY = centerY - (mathY * SCALE);

                    if (Math.abs(mouseY - graphPixelY) < 15) {
                        gc.setFill(Color.BLACK);
                        gc.fillOval(mouseX - 5, graphPixelY - 5, 10, 10);
                        String text = String.format("(%.2f, %.2f)", mathX, mathY);
                        gc.setFill(Color.rgb(255, 255, 255, 0.8));
                        gc.fillRoundRect(mouseX + 10, graphPixelY - 30, 120, 20, 10, 10);
                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("Arial", 12));
                        gc.fillText(text, mouseX + 15, graphPixelY - 15);
                    }
                } catch (Exception ex) {}
            }
        }
    }

    // --- INPUT BOX & SLIDER LOGIC ---

    private void addFunctionInputBox(VBox container) {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");

        // ১. কালার অ্যাসাইন করা (স্থায়ীভাবে)
        Color assignedColor = graphColors[globalColorIndex % graphColors.length];
        globalColorIndex++;

        // কালারটা রোর (Row) মেমোরিতে সেভ করে রাখা
        mainRow.setUserData(assignedColor);

        StackPane inputWrapper = new StackPane();
        inputWrapper.setAlignment(Pos.CENTER_RIGHT);

        // ২. কালার ডট (Dot) তৈরি
        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor); // Radius = 6
        StackPane.setAlignment(colorDot, Pos.CENTER_LEFT); // বাম পাশে সেট করা
        StackPane.setMargin(colorDot, new Insets(0, 0, 0, 15)); // বাম পাশ থেকে ১৫ পিক্সেল দূরে

        TextField inputBox = new TextField();
        inputBox.setPromptText("Ex: ax + b");
        inputBox.setPrefHeight(50);
        // বাম পাশে প্যাডিং বাড়ানো হলো (৩৫) যাতে ডটের নিচে লেখা না পড়ে
        inputBox.setPadding(new Insets(5, 80, 5, 35));

        inputBox.setStyle(
                "-fx-background-color: White; -fx-background-radius: 10; " +
                        "-fx-border-color: #9D00FF; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-text-fill: black; -fx-font-size: 15px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;"
        );

        // --- ICONS SETUP ---
        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(70);
        StackPane.setMargin(buttonBox, new Insets(0, 10, 0, 0));

        Button settingsBtn = createIconButton(
                "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z",
                "gray", 18
        );

        Button closeBtn = createIconButton(
                "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                "gray", 18
        );

        // Hover effects
        closeBtn.setOnMouseEntered(e -> ((javafx.scene.shape.SVGPath)closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((javafx.scene.shape.SVGPath)closeBtn.getGraphic()).setFill(Color.GRAY));
        settingsBtn.setOnMouseEntered(e -> ((javafx.scene.shape.SVGPath)settingsBtn.getGraphic()).setFill(Color.web("#9D00FF")));
        settingsBtn.setOnMouseExited(e -> ((javafx.scene.shape.SVGPath)settingsBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().addAll(settingsBtn, closeBtn);

        VBox sliderContainer = new VBox(10);
        sliderContainer.setPadding(new Insets(0, 0, 0, 20));
        sliderContainer.setVisible(false);
        sliderContainer.setManaged(false);

        settingsBtn.setOnAction(e -> {
            boolean isVisible = sliderContainer.isVisible();
            sliderContainer.setVisible(!isVisible);
            sliderContainer.setManaged(!isVisible);
        });

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
            updateVariables(newVal, sliderContainer);
            drawGraph();
        });

        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addFunctionInputBox(container);
            }
            if (event.getCode() == KeyCode.BACK_SPACE && inputBox.getText().isEmpty()) {
                int index = container.getChildren().indexOf(mainRow);
                if (index > 0) {
                    VBox prevRow = (VBox) container.getChildren().get(index - 1);
                    StackPane prevWrapper = (StackPane) prevRow.getChildren().get(0);
                    prevWrapper.getChildren().get(0).requestFocus();
                    deleteAction.run();
                }
            }
        });

        // ৩. বক্সের স্ট্রাকচারে কালার ডট যোগ করা হলো
        inputWrapper.getChildren().addAll(inputBox, colorDot, buttonBox);
        mainRow.getChildren().addAll(inputWrapper, sliderContainer);
        container.getChildren().add(mainRow);
        inputBox.requestFocus();
    }
    private Button createIconButton(String svgData, String colorHex, double size) {
        javafx.scene.shape.SVGPath path = new javafx.scene.shape.SVGPath();
        path.setContent(svgData);
        path.setFill(Color.web(colorHex));

        // Scale the icon
        double originalWidth = path.getBoundsInLocal().getWidth();
        double scaleFactor = size / originalWidth;
        path.setScaleX(scaleFactor);
        path.setScaleY(scaleFactor);

        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        return btn;
    }

    private void updateVariables(String equation, VBox sliderContainer) {
        Set<String> foundVars = new HashSet<>();

        // ১. টেম্পোরারি স্ট্রিং থেকে ম্যাথ ফাংশনগুলো রিমুভ করে ফেলি
        // যাতে 'sin' এর s, i, n কে ভেরিয়েবল না ভাবে
        String tempEq = equation.toLowerCase();
        String[] functions = {"sin", "cos", "tan", "asin", "acos", "atan", "sqrt", "cbrt", "log", "exp", "abs"};

        for (String func : functions) {
            tempEq = tempEq.replace(func, " "); // ফাংশনের নামগুলো স্পেস দিয়ে রিপ্লেস
        }

        // ২. বাকি স্ট্রিং থেকে প্রতিটি অক্ষর চেক করি
        for (char c : tempEq.toCharArray()) {
            // যদি অক্ষর হয় এবং x, e, pi বা স্পেস না হয়
            if (Character.isLetter(c) && c != 'x' && c != 'e' && c != ' ') {
                // pi আলাদাভাবে হ্যান্ডেল করা কঠিন ক্যারেক্টার লুপে, তাই আমরা p এবং i কেও ইগনোর করতে পারি যদি দরকার হয়
                // আপাতত সিম্পল রাখছি:
                if (c != 'p' && c != 'i') { // pi এর কনফিউশন এড়াতে
                    foundVars.add(String.valueOf(c));
                }
            }
        }

        // ৩. স্লাইডার তৈরি (আগের মতোই)
        List<javafx.scene.Node> currentNodes = new ArrayList<>(sliderContainer.getChildren());
        sliderContainer.getChildren().clear();

        for (String varName : foundVars) {
            HBox existingRow = null;
            for(javafx.scene.Node node : currentNodes) {
                if(node instanceof HBox) {
                    Label lbl = (Label) ((HBox) node).getChildren().get(0);
                    if(lbl.getText().startsWith(varName)) {
                        existingRow = (HBox) node; break;
                    }
                }
            }
            if(existingRow != null) sliderContainer.getChildren().add(existingRow);
            else sliderContainer.getChildren().add(createSliderRow(varName));
        }
    }
    // ভেরিয়েবল চেক করার জন্য হেল্পার মেথড
    private boolean isReserved(String str) {
        // এই শব্দগুলো ভেরিয়েবল হিসেবে গণ্য হবে না
        return str.equals("x") || str.equals("pi") || str.equals("e") ||
                str.equals("sin") || str.equals("cos") || str.equals("tan") ||
                str.equals("asin") || str.equals("acos") || str.equals("atan") ||
                str.equals("sqrt") || str.equals("cbrt") || str.equals("log") ||
                str.equals("log10") || str.equals("log2") || str.equals("abs") ||
                str.equals("ceil") || str.equals("floor") || str.equals("exp") ||
                str.equals("signum");
    }

    private HBox createSliderRow(String varName) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #222; -fx-background-radius: 5; -fx-padding: 5;");

        Label nameLbl = new Label(varName + " = ");
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Consolas", 14));

        Slider slider = new Slider(-10, 10, 1);
        slider.setPrefWidth(150);

        TextField valInput = new TextField("1");
        valInput.setPrefWidth(60);
        valInput.setStyle("-fx-background-color: #333; -fx-text-fill: white;");

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            valInput.setText(String.format("%.2f", newVal));
            drawGraph();
        });

        valInput.setOnAction(e -> {
            try {
                double val = Double.parseDouble(valInput.getText());
                slider.setValue(val);
                if(val > slider.getMax()) slider.setMax(val + 50);
                if(val < slider.getMin()) slider.setMin(val - 50);
                drawGraph();
            } catch (NumberFormatException ex) {}
        });

        row.getChildren().addAll(nameLbl, slider, valInput);
        return row;
    }

    private String cleanEquation(String equation) {
        String cleanEq = equation.toLowerCase().replace(" ", "");
        if (cleanEq.startsWith("y=")) cleanEq = cleanEq.substring(2);

        // ১. ইমপ্লিসিট মাল্টিপ্লিকেশন হ্যান্ডেল করা (2x -> 2*x)
        cleanEq = cleanEq.replaceAll("(\\d)([a-z])", "$1*$2");

        // ২. দুটি ভেরিয়েবল পাশাপাশি থাকলে মাঝখানে * বসানো (ax -> a*x)
        // তবে সাবধান! sin যেন s*i*n না হয়ে যায়।
        // এই পার্টটা খুব জটিল Regex ছাড়া করা কঠিন।
        // তাই সবচেয়ে ভালো উপায় হলো ইউজারকে হিন্ট দেওয়া যে 'a*x' লিখতে হবে।
        // তবে আমরা সাধারণ 'ax' বা 'bx' হ্যান্ডেল করতে পারি এভাবে:

        // সাধারণ ফরম্যাট (single letter variable followed by x)
        // যেমন: ax, bx, mx -> a*x, b*x
        cleanEq = cleanEq.replaceAll("([a-wz])(x)", "$1*$2");
        // [a-wz] মানে x বাদে অন্য অক্ষর। y তো এমনিতেই নেই।

        return cleanEq;
    }

    private void drawGrid(double width, double height) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        for (double i = centerX; i < width; i += SCALE) gc.strokeLine(i, 0, i, height);
        for (double i = centerX; i > 0; i -= SCALE) gc.strokeLine(i, 0, i, height);
        for (double i = centerY; i < height; i += SCALE) gc.strokeLine(0, i, width, i);
        for (double i = centerY; i > 0; i -= SCALE) gc.strokeLine(0, i, width, i);
    }

    private void drawAxes(double width, double height) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, height / 2.0, width, height / 2.0);
        gc.strokeLine(width / 2.0, 0, width / 2.0, height);
    }



    public static void main(String[] args) {
        launch();
    }
}