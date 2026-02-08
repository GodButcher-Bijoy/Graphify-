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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainApp extends Application {

    // Global variables for Graphing
    private Canvas canvas;
    private GraphicsContext gc;
    private VBox functionContainer; // Holds all input boxes
    private static final double SCALE = 40; // 1 unit = 40 pixels

    // Colors for different graphs (cycle through these)
    private final Color[] graphColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};

    @Override
    public void start(Stage stage) {
        // --- INTRO ANIMATION SETUP (Your original code) ---
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
        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(400);
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setStyle("-fx-background-color: #121212; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        Label inputLabel = new Label("Enter Function:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // Initialize Function Container
        functionContainer = new VBox(15);
        functionContainer.setStyle("-fx-background-color: transparent;");

        // Add initial inputs
        for (int i = 0; i < 3; i++) {
            addFunctionInputBox(functionContainer);
        }

        ScrollPane scrollPane = new ScrollPane(functionContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // X-Range Inputs (Visual only for now, can be linked to logic later)
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

        // 2. GRAPH AREA (Canvas Integration)
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        // Create Resizable Canvas
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        // Bind canvas size to pane size
        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());

        // Redraw when size changes
        canvas.widthProperty().addListener(evt -> drawGraph());
        canvas.heightProperty().addListener(evt -> drawGraph());

        // Smart Mouse Hover Logic
        canvas.setOnMouseMoved(e -> {
            drawGraph(); // Refresh to clear old points
            checkAndDrawHoverPoint(e);
        });

        graphPane.getChildren().add(canvas);

        root.setLeft(sidebar);
        root.setCenter(graphPane);

        return new Scene(root, 1000, 700);
    }

    // --- GRAPHING LOGIC (From Backend) ---

    private void drawGraph() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Clear Canvas
        gc.clearRect(0, 0, width, height);

        // Draw Grid & Axes
        drawGrid(width, height);
        drawAxes(width, height);

        // Loop through all input boxes and plot valid equations
        int colorIndex = 0;
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof TextField) {
                String equation = ((TextField) node).getText();
                if (!equation.trim().isEmpty()) {
                    // Cycle colors
                    gc.setStroke(graphColors[colorIndex % graphColors.length]);
                    plotEquation(equation, width, height);
                    colorIndex++;
                }
            }
        }
    }

    private void plotEquation(String equation, double width, double height) {
        String cleanEq = cleanEquation(equation);
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        try {
            ExpressionBuilder builder = new ExpressionBuilder(cleanEq).variable("x");
            Expression expr = builder.build();

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

                    // Clipping logic
                    if (screenY < -height || screenY > height * 2) {
                        firstPoint = true;
                        continue;
                    }

                    if (firstPoint) {
                        gc.moveTo(screenX, screenY);
                        firstPoint = false;
                    } else {
                        gc.lineTo(screenX, screenY);
                    }
                } catch (Exception e) {
                    // Math error ignore
                }
            }
            gc.stroke();
        } catch (Exception e) {
            // Invalid equation ignore
        }
    }

    private void checkAndDrawHoverPoint(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double mathX = (mouseX - centerX) / SCALE;

        // Check ALL equations to see if mouse is near any
        for (javafx.scene.Node node : functionContainer.getChildren()) {
            if (node instanceof TextField) {
                String equation = ((TextField) node).getText();
                if (equation.trim().isEmpty()) continue;

                try {
                    String cleanEq = cleanEquation(equation);
                    Expression expr = new ExpressionBuilder(cleanEq).variable("x").build();
                    expr.setVariable("x", mathX);
                    double mathY = expr.evaluate();

                    double graphPixelY = centerY - (mathY * SCALE);

                    // 15px Tolerance Area
                    if (Math.abs(mouseY - graphPixelY) < 15) {
                        // Draw Point
                        gc.setFill(Color.BLACK);
                        gc.fillOval(mouseX - 5, graphPixelY - 5, 10, 10);

                        // Draw Text Background
                        String text = String.format("(%.2f, %.2f)", mathX, mathY);
                        gc.setFill(Color.rgb(255, 255, 255, 0.8));
                        gc.fillRoundRect(mouseX + 10, graphPixelY - 30, 120, 20, 10, 10);

                        // Draw Text
                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("Arial", 12));
                        gc.fillText(text, mouseX + 15, graphPixelY - 15);
                    }
                } catch (Exception ex) {
                    // Ignore hover errors
                }
            }
        }
    }

    private String cleanEquation(String equation) {
        String cleanEq = equation.toLowerCase().replace(" ", "");
        if (cleanEq.startsWith("y=")) cleanEq = cleanEq.substring(2);
        return cleanEq.replaceAll("(\\d)(x)", "$1*$2"); // Fix 2x -> 2*x
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
        gc.strokeLine(0, height / 2.0, width, height / 2.0); // X Axis
        gc.strokeLine(width / 2.0, 0, width / 2.0, height); // Y Axis
    }

    private void addFunctionInputBox(VBox container) {
        TextField inputBox = new TextField();
        inputBox.setPromptText("y = ...");
        inputBox.setPrefHeight(60);
        inputBox.setPadding(new Insets(5, 10, 5, 10));

        inputBox.setStyle(
                "-fx-background-color: White; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #9D00FF; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 8; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 15px; " +
                        "-fx-font-family: 'Verdana'; " +
                        "-fx-font-weight: bold;"
        );

        // Update graph immediately when user types
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            drawGraph();
        });

        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int index = container.getChildren().indexOf(inputBox);
                if (index == container.getChildren().size() - 1) {
                    addFunctionInputBox(container);
                }
                if (index + 1 < container.getChildren().size()) {
                    container.getChildren().get(index + 1).requestFocus();
                }
            }
        });

        container.getChildren().add(inputBox);
        inputBox.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}