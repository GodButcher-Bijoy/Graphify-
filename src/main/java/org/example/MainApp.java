package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // ১. একটি টেক্সট ফিল্ড (যেখানে ইউজার নাম লিখবে)
        TextField nameInput = new TextField();
        nameInput.setPromptText("আপনার নাম লিখুন...");
        nameInput.setMaxWidth(200); // বক্সটি বেশি বড় না হওয়ার জন্য

        // ২. একটি লেবেল (যেখানে মেসেজ দেখাবে)
        Label messageLabel = new Label("স্বাগতম!");
        messageLabel.setFont(new Font("Arial", 16));

        // ৩. একটি বাটন
        Button clickMeBtn = new Button("আমাকে ক্লিক করুন");

        // ৪. বাটনে ক্লিক করলে কী হবে (Action Event)
        clickMeBtn.setOnAction(e -> {
            String name = nameInput.getText();
            if (!name.isEmpty()) {
                messageLabel.setText("হ্যালো, " + name + " bokachoda    ! JavaFX শেখা শুরু!");
            } else {
                messageLabel.setText("দয়া করে আগে নাম লিখুন!");
            }
        });

        // ৫. লেআউট (VBox সব কিছু উপর থেকে নিচে সাজায়)
        VBox root = new VBox(15); // ১০ পিক্সেল গ্যাপ
        root.setAlignment(Pos.CENTER); // সব মাঝখানে থাকবে
        root.getChildren().addAll(messageLabel, nameInput, clickMeBtn);

        // ৬. সিন এবং স্টেজ সেটআপ
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("My First Interactive App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
