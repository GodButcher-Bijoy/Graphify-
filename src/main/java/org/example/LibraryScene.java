package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class LibraryScene {

    public static Parent createView(Runnable onBack, Consumer<EquationPreset> onPresetSelected) {
        BorderPane root = new BorderPane();
        // Set the entire scene background to black
        root.setStyle("-fx-background-color: #000000;");

        // --- Top: Back Button ---
        // Just the arrow, made much larger with transparent background
        Button backBtn = new Button("⌂");
        String normalStyle = "-fx-font-size: 30px; -fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";
        String hoverStyle = "-fx-font-size: 30px; -fx-background-color: #222222; -fx-text-fill: #9D00FF; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";

        backBtn.setStyle(normalStyle);
        backBtn.setOnAction(e -> onBack.run());

        // Hover effect to turn the arrow purple
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));

        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(10, 20, 0, 20)); // Adjusted padding slightly for the large font
        root.setTop(topBar);

        // --- Center: Category & Preset Grid ---
        VBox categoriesBox = new VBox(40);
        categoriesBox.setPadding(new Insets(0, 50, 50, 50));
        categoriesBox.setAlignment(Pos.TOP_CENTER);

        EquationLibrary lib = new EquationLibrary();
        for (EquationCategory cat : lib.getCategories()) {
            Label catLabel = new Label(cat.getMenuLabel());
            catLabel.setStyle("-fx-text-fill: #16E004; -fx-font-size: 28px; -fx-font-weight: bold;");

            FlowPane presetsPane = new FlowPane(20, 20);
            presetsPane.setAlignment(Pos.CENTER);

            for (EquationPreset preset : cat.getPresets()) {
                Button pBtn = new Button(preset.getName());
                pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #222222; -fx-text-fill: #ddd; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;");
                pBtn.setOnAction(e -> onPresetSelected.accept(preset));

                pBtn.setOnMouseEntered(e -> pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;"));
                pBtn.setOnMouseExited(e -> pBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #222222; -fx-text-fill: #ddd; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand;"));

                presetsPane.getChildren().add(pBtn);
            }

            VBox section = new VBox(15, catLabel, presetsPane);
            section.setAlignment(Pos.CENTER);
            categoriesBox.getChildren().add(section);
        }

        ScrollPane scroll = new ScrollPane(categoriesBox);
        scroll.setFitToWidth(true);
        // Ensure scrollpane is transparent so the black background shows through
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scroll);

        return root;
    }
}