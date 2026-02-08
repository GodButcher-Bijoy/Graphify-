import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class first extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final double SCALE = 40; // 1 unit = 40 pixels
    private String currentEquation = "";

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawGraph(gc, ""); // শুরুতে খালি গ্রাফ

        TextField equationInput = new TextField();
        equationInput.setPromptText("Enter equation (e.g., y = 2x + 1)");
        equationInput.setLayoutX(50);
        equationInput.setLayoutY(20);
        equationInput.setPrefWidth(300);
        equationInput.setStyle("-fx-background-radius: 20; -fx-padding: 10; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        // Real-time Update
        equationInput.textProperty().addListener((observable, oldValue, newValue) -> {
            currentEquation = newValue;
            drawGraph(gc, currentEquation);
        });

        // Smart Mouse Hover (শুধু লাইনের উপরে গেলেই দেখাবে)
        canvas.setOnMouseMoved(e -> {
            drawGraph(gc, currentEquation); // গ্রাফ রিফ্রেশ
            checkAndDrawHoverPoint(gc, currentEquation, e);
        });

        root.getChildren().addAll(canvas, equationInput);
        stage.setTitle("Professional Graph Plotter");
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

    private void drawGraph(GraphicsContext gc, String equation) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.web("#fcfcfc"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawGrid(gc);
        drawAxes(gc);

        // ইকুয়েশন প্রসেসিং
        if (!equation.isEmpty()) {
            // ১. "y=" থাকলে কেটে ফেলা
            String cleanEq = equation.toLowerCase().replace(" ", ""); // স্পেস রিমুভ
            if (cleanEq.startsWith("y=")) {
                cleanEq = cleanEq.substring(2);
            }

            // ২. 2x কে 2*x বানানো (Implicit Multiplication fix)
            // (RegEx: সংখ্যার পরে x থাকলে মাঝে * বসাবে)
            cleanEq = cleanEq.replaceAll("(\\d)(x)", "$1*$2");

            try {
                // ৩. চেক করা ইকুয়েশন ভ্যালিড কিনা
                ExpressionBuilder builder = new ExpressionBuilder(cleanEq).variable("x");
                Expression expr = builder.build(); // যদি "sin" থাকে কিন্তু (x) না থাকে, এখানে error খাবে

                // ভ্যালিড হলে প্লট করো
                plotEquation(gc, expr);
            } catch (Exception e) {
                // ইকুয়েশন ভুল বা অসম্পূর্ণ (যেমন "sin("), তাই কিছু আঁকবে না
            }
        }
    }

    private void plotEquation(GraphicsContext gc, Expression expr) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2.5);
        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;

        gc.beginPath();
        boolean firstPoint = true;

        for (double screenX = 0; screenX <= WIDTH; screenX++) {
            double mathX = (screenX - centerX) / SCALE;

            try {
                // exp4j দিয়ে ক্যালকুলেশন
                expr.setVariable("x", mathX);
                double mathY = expr.evaluate();

                if (Double.isNaN(mathY) || Double.isInfinite(mathY)) continue;

                double screenY = centerY - (mathY * SCALE);

                // গ্রাফের বাইরে চলে গেলে লাইন অফ রাখা (Clipping Logic)
                if (screenY < -HEIGHT || screenY > HEIGHT * 2) {
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
                // Math error (e.g. division by zero)
            }
        }
        gc.stroke();
    }

    // --- Smart Hover Logic ---
    private void checkAndDrawHoverPoint(GraphicsContext gc, String equation, MouseEvent e) {
        if (equation.isEmpty()) return;

        double mouseX = e.getX();
        double mouseY = e.getY();
        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;

        // মাউসের X পজিশন থেকে আমরা বের করব গ্রাফের লাইনটা ঐ X-এ কোথায় আছে
        double mathX = (mouseX - centerX) / SCALE;

        // ইকুয়েশন ক্লিনিং (সেম লজিক)
        String cleanEq = equation.toLowerCase().replace(" ", "");
        if (cleanEq.startsWith("y=")) cleanEq = cleanEq.substring(2);
        cleanEq = cleanEq.replaceAll("(\\d)(x)", "$1*$2");

        try {
            Expression expr = new ExpressionBuilder(cleanEq).variable("x").build();
            expr.setVariable("x", mathX);
            double mathY = expr.evaluate(); // আসল Y ভ্যালু (গ্রাফের)

            double graphPixelY = centerY - (mathY * SCALE); // গ্রাফের পিক্সেল Y

            // লজিক: মাউস যদি গ্রাফ লাইনের ১০ পিক্সেলের মধ্যে থাকে, তবেই পয়েন্ট দেখাবে
            if (Math.abs(mouseY - graphPixelY) < 15) { // 15px Tolerance Area

                // পয়েন্ট আঁকা (গ্রাফ লাইনের উপরে, মাউসের উপরে না)
                gc.setFill(Color.BLUE);
                gc.fillOval(mouseX - 5, graphPixelY - 5, 10, 10);

                // টেক্সট দেখানো
                String text = String.format("(%.2f, %.2f)", mathX, mathY);
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("Arial", 14));
                gc.fillText(text, mouseX + 15, graphPixelY - 15);
            }

        } catch (Exception ex) {
            // Hover error ignore
        }
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;
        for (double i = centerX; i < WIDTH; i += SCALE) gc.strokeLine(i, 0, i, HEIGHT);
        for (double i = centerX; i > 0; i -= SCALE) gc.strokeLine(i, 0, i, HEIGHT);
        for (double i = centerY; i < HEIGHT; i += SCALE) gc.strokeLine(0, i, WIDTH, i);
        for (double i = centerY; i > 0; i -= SCALE) gc.strokeLine(0, i, WIDTH, i);
    }

    private void drawAxes(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(0, HEIGHT / 2.0, WIDTH, HEIGHT / 2.0);
        gc.strokeLine(WIDTH / 2.0, 0, WIDTH / 2.0, HEIGHT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}