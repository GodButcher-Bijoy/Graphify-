import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class first extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ১. লেআউট তৈরি (BorderPane)
        BorderPane root = new BorderPane();

        // ২. ক্যানভাস তৈরি (ড্রয়িং বোর্ড)
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D(); // কলম নিলাম
        root.setCenter(canvas); // ক্যানভাস মাঝখানে রাখলাম

        // ৩. নিচের কন্ট্রোল প্যানেল তৈরি
        TextField equationInput = new TextField("x^2"); // ইনপুট বক্স
        Button plotBtn = new Button("Plot"); // বাটন

        // বাটন ক্লিক করলে কী হবে (Event Handling)
        plotBtn.setOnAction(e -> {
            // আগের ড্রয়িং মুছে ফেলব
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // নতুন কিছু আঁকব (Testing)
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            gc.strokeLine(0, 0, 800, 600); // একটা কোণাকুনি দাগ
            System.out.println("Equation Plotted: " + equationInput.getText());
        });

        // কন্ট্রোলগুলো নিচে (Bottom) রাখা
        HBox controls = new HBox(10, equationInput, plotBtn); // ১০ পিক্সেল গ্যাপ
        root.setBottom(controls);

        // ৪. সিন ও স্টেজ সেটআপ
        Scene scene = new Scene(root, 800, 650);
        primaryStage.setTitle("My Equation Plotter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}