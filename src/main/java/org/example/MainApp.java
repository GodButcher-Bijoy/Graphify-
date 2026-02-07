package org.example;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ১. একটি ক্যানভাস তৈরি করলাম (এটি আপনার ড্রয়িং পেপার)
        // ক্যানভাসের সাইজ: প্রস্থ ৬০০, উচ্চতা ৪০০
        Canvas canvas = new Canvas(600, 400);

        // ২. "কলম" বা GraphicsContext নিলাম
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // ৩. ড্রয়িং শুরু (লাইন আঁকা)
        // ধাপ ক: লাইনের কালার ঠিক করা
        gc.setStroke(Color.RED);

        // ধাপ খ: লাইনটি কতটুকু মোটা হবে (Thickness)
        gc.setLineWidth(5);

        // ধাপ গ: লাইন আঁকা (Main Logic)
        // gc.strokeLine(x1, y1, x2, y2);
        // (50, 50) বিন্দু থেকে (500, 350) বিন্দু পর্যন্ত লাইন হবে
        gc.strokeLine(50, 50, 500, 350);

        // ৪. স্টেজ এবং সিন সেটআপ (উইন্ডো তৈরি)
        Group root = new Group(canvas); // ক্যানভাসটি গ্রুপে রাখলাম
        Scene scene = new Scene(root, 600, 400); // উইন্ডোর সাইজ ঠিক করলাম

        primaryStage.setTitle("Simple Line Plot Demo"); // টাইটেল
        primaryStage.setScene(scene);
        primaryStage.show(); // উইন্ডো দেখানো
    }

    public static void main(String[] args) {
        launch(args); // অ্যাপলিকেশন স্টার্ট
    }
}