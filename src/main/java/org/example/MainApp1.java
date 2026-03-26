package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainApp1 extends Application {

    @Override
    public void start(Stage stage) {
        Scene introScene = IntroScene.create(stage, () -> {
            // Notun scene na toiri kore, just bhitorer ui (root) ta nilam
            BorderPane mainRoot = createMainUI();

            // Current scene-er content ta ek sec-e replace kore dilam!
            stage.getScene().setRoot(mainRoot);
        });

        stage.setTitle("Graphify");
        stage.setScene(introScene);
        stage.show();
    }

    private BorderPane createMainUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #333333;");

        AppState appState = new AppState();

        // Circular dependency resolver
        Runnable[] redrawAction = new Runnable[1];

        UIManager uiManager = new UIManager(appState, () -> {
            if (redrawAction[0] != null) redrawAction[0].run();
        });

        // Graph pane + canvas
        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12;");
        BorderPane.setMargin(graphPane, new Insets(20));

        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());
        graphPane.getChildren().add(canvas);

        // ── Home button (top-right) ──────────────────────────────────────────
        Button homeBtn = new Button("🏠 Home");
        homeBtn.setStyle(
                "-fx-background-color: white; -fx-border-color: #ccc; " +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; " +
                        "-fx-font-size: 13px; -fx-padding: 6 14;"
        );
        StackPane.setAlignment(homeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(homeBtn, new Insets(15));

        // ── Library button (top-left) ────────────────────────────────────────
        Button libraryBtn = new Button("📚 Library ▾");
        libraryBtn.setStyle(
                "-fx-background-color: #9D00FF; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-font-size: 13px; -fx-padding: 6 14;"
        );
        StackPane.setAlignment(libraryBtn, Pos.TOP_LEFT);
        StackPane.setMargin(libraryBtn, new Insets(15));

        // ── Build the dropdown ContextMenu from EquationLibrary ──────────────
        ContextMenu libraryMenu = buildLibraryMenu(uiManager);
        libraryBtn.setOnAction(e -> {
            if (libraryMenu.isShowing()) {
                libraryMenu.hide();
            } else {
                libraryMenu.show(libraryBtn,
                        javafx.geometry.Side.BOTTOM, 0, 4);
            }
        });

        // Center wrapper: graph + overlay buttons + keypad
        StackPane centerWrapper = new StackPane();
        centerWrapper.getChildren().add(graphPane);
        centerWrapper.getChildren().add(homeBtn);
        centerWrapper.getChildren().add(libraryBtn);

        VBox floatingKeypad = uiManager.createFloatingKeypad();
        StackPane.setAlignment(floatingKeypad, Pos.BOTTOM_CENTER);
        centerWrapper.getChildren().add(floatingKeypad);

        // GraphRenderer
        GraphRenderer graphRenderer = new GraphRenderer(appState, canvas, uiManager.getFunctionContainer());
        redrawAction[0] = () -> graphRenderer.drawGraph();

        homeBtn.setOnAction(e -> {
            appState.setScale(40);
            appState.setOffsetX(0);
            appState.setOffsetY(0);
            graphRenderer.drawGraph();
        });

        root.setLeft(uiManager.createSidebar());
        root.setCenter(centerWrapper);

        graphRenderer.drawGraph();
        return root;
    }

    // =========================================================================
    // Build the nested ContextMenu from EquationLibrary
    // =========================================================================
    private ContextMenu buildLibraryMenu(UIManager uiManager) {
        EquationLibrary library = new EquationLibrary();
        ContextMenu menu = new ContextMenu();

        // Style the context menu dark
        menu.setStyle(
                "-fx-background-color: #1A1A1A; " +
                        "-fx-border-color: #9D00FF; -fx-border-width: 1.5; " +
                        "-fx-background-radius: 10; -fx-border-radius: 10;"
        );

        for (EquationCategory category : library.getCategories()) {
            // Category header (non-clickable label row)
            MenuItem catLabel = new MenuItem(category.getMenuLabel());
            catLabel.setStyle(
                    "-fx-text-fill: #9D00FF; -fx-font-weight: bold; -fx-font-size: 13px;"
            );
            catLabel.setDisable(true);          // header row is not clickable
            menu.getItems().add(catLabel);

            // One MenuItem per preset in this category
            for (EquationPreset preset : category.getPresets()) {
                MenuItem presetItem = new MenuItem("    " + preset.getName());
                presetItem.setStyle(
                        "-fx-text-fill: #EEEEEE; -fx-font-size: 13px;"
                );

                // On click: load the preset — each equation uses its own built-in color
                presetItem.setOnAction(e -> {
                    uiManager.loadPreset(preset);
                    menu.hide();
                });

                menu.getItems().add(presetItem);
            }

            // Separator between categories
            SeparatorMenuItem sep = new SeparatorMenuItem();
            menu.getItems().add(sep);
        }

        // Remove trailing separator
        if (!menu.getItems().isEmpty()) {
            int last = menu.getItems().size() - 1;
            if (menu.getItems().get(last) instanceof SeparatorMenuItem) {
                menu.getItems().remove(last);
            }
        }

        return menu;
    }

    public static void main(String[] args) {
        launch(args);
    }
}