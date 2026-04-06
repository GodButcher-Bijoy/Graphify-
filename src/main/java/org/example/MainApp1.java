package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class MainApp1 extends Application {

    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;

        Scene introScene = IntroScene.create(stage, () -> {
            // After Intro finishes, go to Selection Screen
            showSelectionScreen();
        });

        stage.setTitle("Graphify");
        stage.setScene(introScene);
        stage.show();
    }

    private void showSelectionScreen() {
        Parent selectionRoot = SelectionScene.createView(
                () -> launchMainUI(AppState.GraphMode.STANDARD, null), // Clicked Standard
                () -> launchMainUI(AppState.GraphMode.POLAR, null),    // Clicked Polar
                () -> showLibraryScreen(),                             // Clicked Experience Curves
                () -> showAboutScreen()                                // Clicked About Us
        );
        mainStage.getScene().setRoot(selectionRoot);
    }

    // Add this method right below it to handle the About screen routing
    private void showAboutScreen() {
        // Passes the action to return to the Selection Screen when "BACK TO MENU" is clicked
        Parent aboutRoot = SelectionScene.createAboutScene(() -> showSelectionScreen());
        mainStage.getScene().setRoot(aboutRoot);
    }

    private void showLibraryScreen() {
        Parent libraryRoot = LibraryScene.createView(
                () -> showSelectionScreen(), // Back button pressed
                (preset) -> launchMainUI(AppState.GraphMode.STANDARD, preset) // Preset clicked
        );
        mainStage.getScene().setRoot(libraryRoot);
    }

    private void launchMainUI(AppState.GraphMode mode, EquationPreset presetToLoad) {
        // Create your existing Main UI and pass the preset to load it
        BorderPane mainRoot = createMainUI(mode,presetToLoad);

        // Swap the scene root directly (No wrapper needed anymore as backBtn is in sidebar)
        mainStage.getScene().setRoot(mainRoot);
    }

    private BorderPane createMainUI(AppState.GraphMode mode, EquationPreset presetToLoad) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #333333;");

        AppState appState = new AppState();
        appState.setGraphMode(mode);
        Runnable[] redrawAction = new Runnable[1];

        UIManager uiManager = new UIManager(appState, () -> {
            if (redrawAction[0] != null) redrawAction[0].run();
        });

        Pane graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12;");
        BorderPane.setMargin(graphPane, new Insets(20));

        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(graphPane.widthProperty());
        canvas.heightProperty().bind(graphPane.heightProperty());
        graphPane.getChildren().add(canvas);

        // ── 🔍 Focus (Re-center) Button (Circular, Floating on Graph) ────────
        Button focusBtn = new Button("🔍");
        String normalFocusStyle = "-fx-background-color: #1A1A1A; -fx-text-fill: white; -fx-border-color: #555555; " +
                "-fx-background-radius: 50em; -fx-border-radius: 50em; -fx-cursor: hand; " +
                "-fx-font-size: 16px; -fx-min-width: 45px; -fx-min-height: 45px; -fx-max-width: 45px; -fx-max-height: 45px;";
        String hoverFocusStyle = "-fx-background-color: #333333; -fx-text-fill: #39FF14; -fx-border-color: #39FF14; " +
                "-fx-background-radius: 50em; -fx-border-radius: 50em; -fx-cursor: hand; " +
                "-fx-font-size: 16px; -fx-min-width: 45px; -fx-min-height: 45px; -fx-max-width: 45px; -fx-max-height: 45px;";

        focusBtn.setStyle(normalFocusStyle);
        focusBtn.setOnMouseEntered(e -> focusBtn.setStyle(hoverFocusStyle));
        focusBtn.setOnMouseExited(e -> focusBtn.setStyle(normalFocusStyle));

        // ── ⌂ Home (Back) button (Now sticking to Sidebar) ──────────────────────
        Button backBtn = new Button("⌂");
        String normalStyle = "-fx-font-size: 30px; -fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";
        String hoverStyle = "-fx-font-size: 30px; -fx-background-color: #222222; -fx-text-fill: #9D00FF; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 10 0 10;";

        backBtn.setStyle(normalStyle);
        backBtn.setOnAction(e -> showSelectionScreen());
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));

        // ── Library button (Will stick to Sidebar) ──────────────────────
        Button libraryBtn = new Button("📚 Library ▾");
        libraryBtn.setStyle(
                "-fx-background-color: #9D00FF; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-cursor: hand; " +
                        "-fx-font-size: 13px; -fx-padding: 6 14;"
        );

        ContextMenu libraryMenu = buildLibraryMenu(uiManager);
        libraryBtn.setOnAction(e -> {
            if (libraryMenu.isShowing()) {
                libraryMenu.hide();
            } else {
                libraryMenu.show(libraryBtn, javafx.geometry.Side.BOTTOM, 0, 4);
            }
        });

        // Center wrapper: Graph, Keypad, and the floating Focus Button
        StackPane centerWrapper = new StackPane();
        centerWrapper.getChildren().add(graphPane);

        VBox floatingKeypad = uiManager.createFloatingKeypad();
        StackPane.setAlignment(floatingKeypad, Pos.BOTTOM_CENTER);
        centerWrapper.getChildren().add(floatingKeypad);

        // Put Focus Button on top right of the graph pane
        StackPane.setAlignment(focusBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(focusBtn, new Insets(35, 35, 0, 0)); // Ektu margin diye graph er corner e bosano
        centerWrapper.getChildren().add(focusBtn);

        GraphRenderer graphRenderer = new GraphRenderer(appState, canvas, uiManager.getFunctionContainer());
        redrawAction[0] = () -> graphRenderer.drawGraph();

        focusBtn.setOnAction(e -> {
            appState.setScale(40);
            appState.setOffsetX(0);
            appState.setOffsetY(0);
            graphRenderer.drawGraph();
        });

        // Sidebar e backBtn ebong libraryBtn pass kora hocche
        root.setLeft(uiManager.createSidebar(backBtn, libraryBtn));
        root.setCenter(centerWrapper);

        if (presetToLoad != null) {
            uiManager.loadPreset(presetToLoad);
        }

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
                    "-fx-text-fill: #1dd7f5; -fx-font-weight: bold; -fx-font-size: 13px;"
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