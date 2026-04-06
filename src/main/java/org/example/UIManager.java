package org.example;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UIManager {

    private final AppState appState;
    private final VBox functionContainer;
    private final Runnable redrawCallback;
    private ScrollPane scrollPane;
    private TextField activeTextField;
    private boolean isKeypadVisible = false;
    // Shared animation timer for all slider animations
    private final List<Runnable> sliderSteppers = new ArrayList<>();
    private int activeAnimationCount = 0;
    private javafx.animation.AnimationTimer sharedAnimationTimer = null;

    public UIManager(AppState appState, Runnable redrawCallback) {
        this.appState = appState;
        this.redrawCallback = redrawCallback;
        this.functionContainer = new VBox(20);
        this.functionContainer.setStyle("-fx-background-color: transparent;");
    }

    public VBox getFunctionContainer() {
        return functionContainer;
    }
    // Parameter e backBtn ar libraryBtn add kora holo
    public HBox createSidebar(Button backBtn, Button libraryBtn) {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(400);
        sidebar.setMinWidth(400);
        sidebar.setAlignment(Pos.TOP_CENTER);

        sidebar.setStyle("-fx-background-color: #121212; -fx-background-radius: 0 20 20 0;");
        javafx.scene.effect.DropShadow sidebarGlow = new javafx.scene.effect.DropShadow();
        sidebarGlow.setRadius(30);
        sidebarGlow.setOffsetX(8);
        sidebarGlow.setColor(Color.web("#9D00FF", 0.5));
        sidebar.setEffect(sidebarGlow);

        // --- Home (Back) ebang Library button ke pasapasi rakha holo ---
        HBox topButtons = new HBox(15, backBtn, libraryBtn); // 15 hocche spacing
        topButtons.setAlignment(Pos.CENTER_LEFT); // Bam pashe align korlam
        VBox.setMargin(topButtons, new Insets(0, 0, 10, 0));

        Label inputLabel = new Label(" ");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        addFunctionInputBox(0);

        scrollPane = new ScrollPane(functionContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        String css = ".scroll-pane { -fx-background: transparent; -fx-background-color: transparent; } " +
                ".scroll-bar:vertical { -fx-background-color: transparent; } " +
                ".scroll-bar:vertical .track { -fx-background-color: transparent; -fx-border-color: transparent; } " +
                ".scroll-bar:vertical .thumb { -fx-background-color: #555555; -fx-background-radius: 5; } " +
                ".scroll-bar:vertical .thumb:hover { -fx-background-color: #777777; } " +
                ".scroll-bar .increment-button, .scroll-bar .decrement-button { -fx-background-color: transparent; -fx-padding: 0; } " +
                ".scroll-bar .increment-arrow, .scroll-bar .decrement-arrow { -fx-shape: ' '; -fx-padding: 0; }";

        String b64Css = "data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(css.getBytes());
        scrollPane.getStylesheets().add(b64Css);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox floatingBoxContainer = new VBox(scrollPane);
        floatingBoxContainer.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #00FFFF; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        floatingBoxContainer.setPadding(new Insets(15, 0, 15, 0));

        javafx.scene.effect.DropShadow containerShadow = new javafx.scene.effect.DropShadow();
        containerShadow.setRadius(25);
        containerShadow.setSpread(0.2);
        containerShadow.setColor(Color.web("#00FFFF", 0.6));
        floatingBoxContainer.setEffect(containerShadow);

        VBox.setVgrow(floatingBoxContainer, Priority.ALWAYS);

        // topButtons sidebar er baccha hishebe add kora holo
        sidebar.getChildren().addAll(topButtons, inputLabel, floatingBoxContainer);

        Pane slideContainer = new Pane(sidebar);
        slideContainer.setPrefWidth(400);
        slideContainer.setMinWidth(0);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(slideContainer.prefWidthProperty());
        clip.heightProperty().bind(slideContainer.heightProperty());
        slideContainer.setClip(clip);

        sidebar.prefHeightProperty().bind(slideContainer.heightProperty());

        Button toggleBtn = createIconButton("M17.59 7.41L16.17 6l-6 6 6 6 1.41-1.41L13 12l4.59-4.59zM11.59 7.41L10.17 6l-6 6 6 6 1.41-1.41L7 12l4.59-4.59z", "#AAAAAA", 24);

        String baseStyle = "-fx-background-color: #111111; -fx-background-radius: 0 8 8 0; -fx-cursor: hand; -fx-border-color: #333333; -fx-border-width: 1 1 1 0; -fx-border-radius: 0 8 8 0;";
        String hoverStyle = "-fx-background-color: #252525; -fx-background-radius: 0 8 8 0; -fx-cursor: hand; -fx-border-color: #444444; -fx-border-width: 1 1 1 0; -fx-border-radius: 0 8 8 0;";

        toggleBtn.setStyle(baseStyle);
        toggleBtn.setPrefHeight(60);
        toggleBtn.setMaxHeight(60);
        toggleBtn.setPrefWidth(32);

        toggleBtn.setOnMouseEntered(e -> {
            toggleBtn.setStyle(hoverStyle);
            ((SVGPath)toggleBtn.getGraphic()).setFill(Color.WHITE);
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), toggleBtn);
            st.setToX(0.85); st.setToY(0.85);
            st.play();
        });
        toggleBtn.setOnMouseExited(e -> {
            if (slideContainer.getPrefWidth() > 0) {
                toggleBtn.setStyle(baseStyle);
                ((SVGPath)toggleBtn.getGraphic()).setFill(Color.web("#AAAAAA"));
            }
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), toggleBtn);
            st.setToX(1.0); st.setToY(1.0);
            st.play();
        });

        toggleBtn.setOnAction(e -> {
            boolean isExpanded = slideContainer.getPrefWidth() > 0;
            javafx.animation.Timeline timeline = new javafx.animation.Timeline();

            if (isExpanded) {
                ((SVGPath)toggleBtn.getGraphic()).setContent("M6.41 6L5 7.41 9.58 12 5 16.59 6.41 18l6-6-6-6zM12.41 6L11 7.41 15.58 12 11 16.59 12.41 18l6-6-6-6z");
                toggleBtn.setStyle("-fx-background-color: #11111199; -fx-background-radius: 0 8 8 0; -fx-cursor: hand;");

                timeline.getKeyFrames().add(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                                new javafx.animation.KeyValue(slideContainer.prefWidthProperty(), 0, javafx.animation.Interpolator.EASE_BOTH),
                                new javafx.animation.KeyValue(sidebar.translateXProperty(), -400, javafx.animation.Interpolator.EASE_BOTH)
                        )
                );
            } else {
                ((SVGPath)toggleBtn.getGraphic()).setContent("M17.59 7.41L16.17 6l-6 6 6 6 1.41-1.41L13 12l4.59-4.59zM11.59 7.41L10.17 6l-6 6 6 6 1.41-1.41L7 12l4.59-4.59z");
                toggleBtn.setStyle(baseStyle);

                timeline.getKeyFrames().add(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                                new javafx.animation.KeyValue(slideContainer.prefWidthProperty(), 400, javafx.animation.Interpolator.EASE_BOTH),
                                new javafx.animation.KeyValue(sidebar.translateXProperty(), 0, javafx.animation.Interpolator.EASE_BOTH)
                        )
                );
            }
            timeline.play();
        });

        HBox wrapper = new HBox(slideContainer, toggleBtn);
        wrapper.setAlignment(Pos.CENTER_LEFT);

        return wrapper;
    }
    private void addFunctionInputBox(int insertIndex) {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");

        VBox.setMargin(mainRow, new Insets(5, 20, 15, 20));

        Color assignedColor = appState.getNextColor();
        mainRow.setUserData(assignedColor);

        VBox fieldAndPrompt = new VBox(0);

        String normalBoxStyle = "-fx-background-color: #333333; -fx-background-radius: 20px;";
        String activeBoxStyle = "-fx-background-color: #444444; -fx-background-radius: 20px;";

        fieldAndPrompt.setStyle(normalBoxStyle);

        Color shadowColor = Color.color(assignedColor.getRed(), assignedColor.getGreen(), assignedColor.getBlue());

        javafx.scene.effect.DropShadow normalShadow = new javafx.scene.effect.DropShadow();
        normalShadow.setRadius(10);
        normalShadow.setColor(Color.color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0.25));
        mainRow.setEffect(normalShadow);
        // --- FIX: Create activeShadow here instead of inside the focus listener ---
        javafx.scene.effect.DropShadow activeShadow = new javafx.scene.effect.DropShadow();
        activeShadow.setRadius(25);
        activeShadow.setSpread(0.3);
        activeShadow.setColor(Color.color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0.8));

        // --- FIX: Stash them in properties so updateRowShadowColor can find and update them ---
        mainRow.getProperties().put("normalShadow", normalShadow);
        mainRow.getProperties().put("activeShadow", activeShadow);

        TextField inputBox = new TextField();
        inputBox.setPromptText("Ex: ax + b");

        inputBox.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        inputBox.setPadding(new Insets(10, 75, 10, 30));

        // ── Real-time auto-format: converts raw sequences → Unicode as user types ─
        final boolean[] isAutoFormatting = {false};
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isAutoFormatting[0]) return;
            String formatted = autoFormatEquation(newVal);
            if (!formatted.equals(newVal)) {
                isAutoFormatting[0] = true;
                int caret = inputBox.getCaretPosition();
                inputBox.setText(formatted);
                // Adjust caret: if substitution shortened the text, move caret back
                inputBox.positionCaret(Math.max(0, Math.min(caret + (formatted.length() - newVal.length()), formatted.length())));
                isAutoFormatting[0] = false;
            }
        });

        // ── Math display overlay (pretty LaTeX-style when unfocused) ────────────
        TextFlow mathDisplay = new TextFlow();
        mathDisplay.setPadding(new Insets(12, 75, 10, 30));
        mathDisplay.setMinHeight(40);
        mathDisplay.setMouseTransparent(false);
        mathDisplay.setVisible(false);
        mathDisplay.setOnMouseClicked(e -> inputBox.requestFocus());
        StackPane inputInner = new StackPane(inputBox, mathDisplay);

        inputBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                activeTextField = inputBox;
                int currentIndex = functionContainer.getChildren().indexOf(mainRow);
                appState.setFocusedEquationIndex(currentIndex);

                javafx.animation.ScaleTransition scaleUp = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), mainRow);
                scaleUp.setToX(1.08); scaleUp.setToY(1.08);
                scaleUp.play();

                fieldAndPrompt.setStyle(activeBoxStyle);
                mainRow.setEffect(activeShadow);

                // Show the raw text field, hide the rendered overlay
                inputBox.setOpacity(1);
                mathDisplay.setVisible(false);

            } else {
                appState.setFocusedEquationIndex(-1);
                appState.getTemporaryPoints().clear();

                javafx.animation.ScaleTransition scaleDown = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), mainRow);
                scaleDown.setToX(1.0); scaleDown.setToY(1.0);
                scaleDown.play();

                fieldAndPrompt.setStyle(normalBoxStyle);
                mainRow.setEffect(normalShadow);

                // Show pretty math display when not editing; hide the raw text field
                if (!inputBox.getText().trim().isEmpty()) {
                    MathRenderer.update(mathDisplay, EquationHandler.reverseAutoFormat(inputBox.getText()), Color.WHITE);
                    inputBox.setOpacity(0);
                    mathDisplay.setVisible(true);
                } else {
                    inputBox.setOpacity(1);
                    mathDisplay.setVisible(false);
                }
            }
            redrawCallback.run();
        });

        // Arrow keys & Backspace logic
        inputBox.setOnKeyPressed(event -> {
            var rows = functionContainer.getChildren();
            int currentIndex = rows.indexOf(mainRow);

            if (event.getCode() == KeyCode.UP) {
                if (currentIndex > 0) {
                    focusTextFieldInRow(rows.get(currentIndex - 1));
                    event.consume();
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                if (currentIndex < rows.size() - 1) {
                    focusTextFieldInRow(rows.get(currentIndex + 1));
                    event.consume();
                }
            } else if (event.getCode() == KeyCode.ENTER) {
                addFunctionInputBox(currentIndex + 1);
                event.consume();
            }
        });

        HBox promptBox = new HBox(8);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.setPadding(new Insets(0, 10, 10, 35));
        promptBox.setVisible(false);
        promptBox.setManaged(false);

        fieldAndPrompt.getChildren().addAll(inputInner, promptBox);

        StackPane inputWrapper = new StackPane();
        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor);

        StackPane.setAlignment(colorDot, Pos.TOP_LEFT);
        StackPane.setMargin(colorDot, new Insets(14, 0, 0, 12));

        colorDot.setCursor(javafx.scene.Cursor.HAND);
        colorDot.setOnMouseClicked(event -> showColorPopup(colorDot, mainRow, event));

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setMaxWidth(70);
        buttonBox.setPickOnBounds(false);

        StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT);
        // --- LOOK UPDATE: ক্রস বাটনকে উপরের দিকে (Margin কমিয়ে) সরানো হয়েছে ---
        StackPane.setMargin(buttonBox, new Insets(3, 5, 0, 0));
        String EYE_OPEN = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
        String EYE_CLOSED = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92C21.42 14.97 22.52 13.56 23 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.06 7.13 11 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";

        mainRow.getProperties().put("isHidden", false); // ডিফল্টভাবে Visible থাকবে

        Button eyeBtn = createIconButton(EYE_OPEN, "gray", 12);
        HBox.setMargin(eyeBtn, new Insets(1, 2, 0, 0));

        eyeBtn.setOnMouseEntered(e -> ((SVGPath) eyeBtn.getGraphic()).setFill(Color.WHITE));
        eyeBtn.setOnMouseExited(e -> ((SVGPath) eyeBtn.getGraphic()).setFill(Color.GRAY));

        eyeBtn.setOnAction(e -> {
            boolean isHidden = (boolean) mainRow.getProperties().getOrDefault("isHidden", false);
            mainRow.getProperties().put("isHidden", !isHidden);

            if (!isHidden) {
                // বন্ধ করার আইকন এবং ইনপুট বক্স হালকা করে দেওয়া
                ((SVGPath) eyeBtn.getGraphic()).setContent(EYE_CLOSED);
                inputInner.setOpacity(0.4);
            } else {
                // খোলার আইকন এবং ইনপুট বক্স স্বাভাবিক করে দেওয়া
                ((SVGPath) eyeBtn.getGraphic()).setContent(EYE_OPEN);
                inputInner.setOpacity(1.0);
            }
            redrawCallback.run(); // Graph Canvas আপডেট করবে
        });
        Button closeBtn = createIconButton("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", "gray", 10);
        // --- LOOK UPDATE: এখান থেকেও টপ মার্জিন কমিয়ে দেওয়া হয়েছে ---
        HBox.setMargin(closeBtn, new Insets(2, 5, 0, 0));

        closeBtn.setOnMouseEntered(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().addAll(eyeBtn,closeBtn);
        inputWrapper.getChildren().addAll(fieldAndPrompt, colorDot, buttonBox);

        VBox sliderContainer = new VBox(5);
        sliderContainer.setPadding(new Insets(5, 0, 0, 20));

        Runnable deleteAction = () -> {
            var rows = functionContainer.getChildren();
            int currentIndex = rows.indexOf(mainRow);

            if (rows.size() > 1) {
                javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), mainRow);
                st.setToX(0.0);

                javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), mainRow);
                ft.setToValue(0.0);

                javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(st, ft);
                pt.setOnFinished(e -> {
                    rows.remove(mainRow);
                    if (currentIndex > 0) {
                        focusTextFieldInRow(rows.get(currentIndex - 1));
                    } else if (!rows.isEmpty()) {
                        focusTextFieldInRow(rows.get(0));
                    }
                    redrawCallback.run();
                });
                pt.play();
            } else {
                inputBox.clear();
                sliderContainer.getChildren().clear();
                // Reset the math display overlay so it doesn't linger after clear
                mathDisplay.getChildren().clear();
                mathDisplay.setVisible(false);
                inputBox.setOpacity(1);
                redrawCallback.run();
            }
        };
        closeBtn.setOnAction(e -> deleteAction.run());

        javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
        debounce.setOnFinished(evt -> redrawCallback.run());
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            Set<String> foundVars = EquationHandler.extractVariables(newVal);

            for (javafx.scene.Node sliderRow : new java.util.ArrayList<>(sliderContainer.getChildren())) {
                String vName = (String) sliderRow.getProperties().get("varName");
                if (vName != null && !foundVars.contains(vName)) {
                    if (sliderRow.getUserData() instanceof Runnable) {
                        ((Runnable) sliderRow.getUserData()).run(); // Slider remove korbe
                    }
                }
            }
            updateSliderPrompt(newVal, promptBox, sliderContainer, inputBox);
            debounce.playFromStart();
        });

        mainRow.getChildren().addAll(inputWrapper, sliderContainer);

        // --- NEW: Creation Animation (মাঝখান থেকে প্রসারিত হবে, একটু ধীরগতিতে) ---
        mainRow.setScaleX(0.0);
        mainRow.setOpacity(0.0);

        if (insertIndex >= 0 && insertIndex <= functionContainer.getChildren().size()) {
            functionContainer.getChildren().add(insertIndex, mainRow);
        } else {
            functionContainer.getChildren().add(mainRow);
        }

        // 300ms Duration for slightly slower creation animation
        javafx.animation.ScaleTransition stIn = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(300), mainRow);
        stIn.setToX(1.0);

        javafx.animation.FadeTransition ftIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), mainRow);
        ftIn.setToValue(1.0);

        javafx.animation.ParallelTransition ptIn = new javafx.animation.ParallelTransition(stIn, ftIn);
        ptIn.play();

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(50));
        pause.setOnFinished(e -> {
            inputBox.requestFocus();
            inputBox.positionCaret(inputBox.getText().length());

            if (scrollPane != null) {
                double contentHeight = functionContainer.getHeight();
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                if (contentHeight > viewportHeight) {
                    double nodeY = mainRow.getBoundsInParent().getMinY();
                    double maxScroll = contentHeight - viewportHeight;
                    scrollPane.setVvalue(nodeY / maxScroll);
                }
            }
        });
        pause.play();
    }
    private Button createIconButton(String svgData, String colorHex, double size) {
        SVGPath path = new SVGPath();
        path.setContent(svgData);
        path.setFill(Color.web(colorHex));
        double scaleFactor = size / path.getBoundsInLocal().getWidth();
        path.setScaleX(scaleFactor); path.setScaleY(scaleFactor);
        Button btn = new Button();
        btn.setGraphic(path);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        return btn;
    }

    private void updateSliderPrompt(String eq, HBox promptBox, VBox sliderContainer, TextField inputBox) {
        Set<String> foundVars = EquationHandler.extractVariables(eq);
        List<String> missingSliders = new ArrayList<>();

        for (String var : foundVars) {
            if (!appState.getActiveSliderVars().contains(var)) {
                missingSliders.add(var);
            }
        }

        promptBox.getChildren().clear();

        if (missingSliders.isEmpty()) {
            promptBox.setVisible(false);
            promptBox.setManaged(false);
            return;
        }

        promptBox.setVisible(true);
        promptBox.setManaged(true);

        Label label = new Label("add slider:");
        label.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-font-weight: bold;");
        promptBox.getChildren().add(label);

        for (String var : missingSliders) {
            Button btn = new Button(var);
            btn.setStyle("-fx-background-color: #eee; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 2 6 2 6;");

            btn.setOnAction(e -> addActualSlider(var, sliderContainer, promptBox, inputBox));
            promptBox.getChildren().add(btn);
        }

        if (missingSliders.size() > 1) {
            Button allBtn = new Button("all");
            allBtn.setStyle("-fx-background-color: #4a8af4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 2 6 2 6;");

            allBtn.setOnAction(e -> {
                for (String var : missingSliders) addActualSlider(var, sliderContainer, promptBox, inputBox);
            });
            promptBox.getChildren().add(allBtn);
        }
    }

    private void addActualSlider(String varName, VBox sliderContainer, HBox promptBox, TextField inputBox) {
        if (appState.getActiveSliderVars().contains(varName)) return;

        appState.getGlobalVariables().putIfAbsent(varName, 1.0);
        appState.getActiveSliderVars().add(varName);

        // ১. Deleted flag
        final boolean[] isDeleted = {false};

        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getProperties().put("varName", varName);
        row.setStyle("-fx-background-color: #222; -fx-background-radius: 5; -fx-padding: 8; -fx-border-color: #444; -fx-border-radius: 5;");
        row.setMinWidth(Region.USE_PREF_SIZE);

        Label nameLbl = new Label(varName + " =");
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        nameLbl.setMinWidth(Region.USE_PREF_SIZE);

        // Current value display
        TextField valInput = new TextField(String.format("%.2f", appState.getGlobalVariables().get(varName)));
        valInput.setPrefWidth(50);
        valInput.setMinWidth(50);
        valInput.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 11px;");

        // ── NEW: min / max range inputs ──────────────────────────────────────
        String rangeFldStyle = "-fx-background-color: #2a2a2a; -fx-text-fill: #aaa; " +
                "-fx-font-size: 10px; -fx-border-color: #555; -fx-border-radius: 3; " +
                "-fx-background-radius: 3; -fx-padding: 2 3;";

        TextField minField = new TextField("-10");
        minField.setPrefWidth(38);
        minField.setMinWidth(38);
        minField.setMaxWidth(38);
        minField.setStyle(rangeFldStyle);
        minField.setPromptText("min");

        TextField maxField = new TextField("10");
        maxField.setPrefWidth(38);
        maxField.setMinWidth(38);
        maxField.setMaxWidth(38);
        maxField.setStyle(rangeFldStyle);
        maxField.setPromptText("max");

        // Initial slider uses -10…10 matching the field defaults
        Slider slider = new Slider(-10, 10, appState.getGlobalVariables().get(varName));
        slider.setPrefWidth(80);
        HBox.setHgrow(slider, Priority.ALWAYS);

        // ── Dynamic animation step = (max − min) / 200 ───────────────────────
        // Stored in a 1-element array so the lambda can read the latest value.
        final double[] animStep = { (slider.getMax() - slider.getMin()) / 200.0 };

        // Helper: parse a range field safely; returns fallback on bad input
        java.util.function.BiConsumer<Boolean, Boolean> applyRange = (updateMin, updateMax) -> {
            try {
                double newMin = Double.parseDouble(minField.getText().trim());
                double newMax = Double.parseDouble(maxField.getText().trim());
                if (newMin >= newMax) return;            // ignore invalid range
                if (updateMin) slider.setMin(newMin);
                if (updateMax) slider.setMax(newMax);
                animStep[0] = (slider.getMax() - slider.getMin()) / 200.0;
                // Clamp current value into new range
                double clamped = Math.max(slider.getMin(), Math.min(slider.getMax(), slider.getValue()));
                slider.setValue(clamped);
            } catch (NumberFormatException ignored) {}
        };

        minField.setOnAction(e -> applyRange.accept(true,  false));
        maxField.setOnAction(e -> applyRange.accept(false, true));
        minField.focusedProperty().addListener((obs, o, n) -> { if (!n) applyRange.accept(true,  false); });
        maxField.focusedProperty().addListener((obs, o, n) -> { if (!n) applyRange.accept(false, true);  });

        boolean[] isPlaying   = {false};
        boolean[] goingForward = {true};
        Button playBtn = new Button("\u25B6");
        playBtn.setMinWidth(28);
        playBtn.setStyle("-fx-background-color: #4a8af4; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        // Stepper uses dynamic animStep
        Runnable stepper = () -> {
            if (isDeleted[0]) return;
            double cur  = slider.getValue();
            double step = animStep[0];
            if (goingForward[0]) {
                if (cur + step >= slider.getMax()) goingForward[0] = false;
                slider.setValue(cur + step);
            } else {
                if (cur - step <= slider.getMin()) goingForward[0] = true;
                slider.setValue(cur - step);
            }
        };

        Runnable stopAnimation = () -> {
            if (isPlaying[0]) {
                isPlaying[0] = false;
                sliderSteppers.remove(stepper);
                activeAnimationCount = Math.max(0, activeAnimationCount - 1);
                playBtn.setText("\u25B6");
                ensureSharedTimerRunning();
            }
        };

        playBtn.setOnAction(e -> {
            if (!isPlaying[0]) {
                isPlaying[0] = true;
                sliderSteppers.add(stepper);
                activeAnimationCount++;
                playBtn.setText("\u23F8");
            } else {
                stopAnimation.run();
            }
            ensureSharedTimerRunning();
        });

        // ২. Remove action
        Runnable removeSliderAction = () -> {
            if (isDeleted[0]) return;
            isDeleted[0] = true;
            stopAnimation.run();
            sliderContainer.getChildren().remove(row);
            appState.getActiveSliderVars().remove(varName);
            appState.getGlobalVariables().remove(varName);
            updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
            redrawCallback.run();
        };

        row.setUserData(removeSliderAction);

        Button closeBtn = createIconButton("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", "gray", 14);
        closeBtn.setOnAction(e -> removeSliderAction.run());

        // ৩. Slider value listener
        slider.valueProperty().addListener((obs, o, n) -> {
            if (isDeleted[0]) return;
            appState.getGlobalVariables().put(varName, n.doubleValue());
            valInput.setText(String.format("%.2f", n.doubleValue()));
            if (!isPlaying[0]) redrawCallback.run();
        });

        // Allow typing directly in valInput to jump slider to that value
        valInput.setOnAction(e -> {
            try {
                double v = Double.parseDouble(valInput.getText().trim());
                v = Math.max(slider.getMin(), Math.min(slider.getMax(), v));
                slider.setValue(v);
            } catch (NumberFormatException ignored) {}
        });

        row.getChildren().addAll(nameLbl, valInput, minField, slider, maxField, playBtn, closeBtn);
        sliderContainer.getChildren().add(row);
        updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
        redrawCallback.run();
    }

    // Shared animation timer: sob slider mile ekbare redraw korbe
    private void ensureSharedTimerRunning() {
        if (sharedAnimationTimer == null) {
            sharedAnimationTimer = new javafx.animation.AnimationTimer() {
                private long lastNanos = 0;
                @Override
                public void handle(long now) {
                    if (lastNanos == 0) { lastNanos = now; return; }
                    double elapsedMs = (now - lastNanos) / 1_000_000.0;
                    if (elapsedMs < 16) return; // ~60fps e cap kora holo
                    lastNanos = now;

                    boolean anyChanged = false;
                    for (Runnable stepper : new ArrayList<>(sliderSteppers)) {
                        stepper.run();
                        anyChanged = true;
                    }
                    if (anyChanged) redrawCallback.run();
                }
            };
        }
        if (activeAnimationCount > 0) {
            sharedAnimationTimer.start();
        } else {
            sharedAnimationTimer.stop();
        }
    }

    // --- Custom Color Palette Popup Logic ---
    private void showColorPopup(javafx.scene.shape.Circle colorDot, VBox mainRow, javafx.scene.input.MouseEvent event) {
        javafx.stage.Popup popup = new javafx.stage.Popup();
        popup.setAutoHide(true); // Baire click korle auto close hobe

        // Popup er main container (Grid 5 column kore)
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setStyle("-fx-background-color: #2A2A2A; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #555; -fx-border-width: 2; -fx-border-radius: 10;");

        // AppState theke 20 ta default color niye aslam
        Color[] defaultColors = appState.getGraphColors();
        int col = 0, row = 0;

        for (Color c : defaultColors) {
            javafx.scene.shape.Circle cDot = new javafx.scene.shape.Circle(12, c);
            cDot.setCursor(javafx.scene.Cursor.HAND);
            cDot.setStroke(Color.WHITE);
            cDot.setStrokeWidth(0.5);

            // Kon color e click korle ki hobe
            cDot.setOnMouseClicked(e -> {
                colorDot.setFill(c);
                mainRow.setUserData(c);
                updateRowShadowColor(mainRow, c);  // live neon update
                redrawCallback.run();
                popup.hide();
            });

            grid.add(cDot, col, row);
            col++;
            if (col == 5) { col = 0; row++; } // 5 ta color er por notun line
        }



        // --- Custom Color Picker (+ Button) ---
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setVisible(false);
        colorPicker.setManaged(false);

        // Custom color choose korle ki hobe
        colorPicker.setOnAction(e -> {
            Color c = colorPicker.getValue();
            colorDot.setFill(c);
            mainRow.setUserData(c);
            updateRowShadowColor(mainRow, c);  // live neon update
            redrawCallback.run();
            popup.hide();
        });

        // Plus Button design
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 50%;");
        plusBtn.setPrefSize(25, 25);
        plusBtn.setCursor(javafx.scene.Cursor.HAND);
        plusBtn.setOnAction(e -> colorPicker.show()); // + e click korle color picker popup asbe

        grid.add(plusBtn, col, row);
        grid.getChildren().add(colorPicker); // Hidden color picker ta grid e add korlam

        popup.getContent().add(grid);

        // Popup ta exactly mouse click er jaigay show korbe
        popup.show(colorDot.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }


    private void focusTextFieldInRow(javafx.scene.Node row) {
        focusTextFieldInRowRobust(row);
    }

    // --- Full Width Keyboard (Dark Theme) ---


    public VBox createFloatingKeypad() {
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.BOTTOM_RIGHT);
        overlay.setPickOnBounds(false);

        // --- Toggle Button (Dark/Purple) ---
        Button toggleBtn = new Button("⌨ Keypad");
        toggleBtn.setStyle("-fx-background-color: #9D00FF; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-padding: 8 16; -fx-cursor: hand;");
        toggleBtn.setFocusTraversable(false);

        HBox toggleWrapper = new HBox(toggleBtn);
        toggleWrapper.setAlignment(Pos.BOTTOM_RIGHT);
        toggleWrapper.setPadding(new Insets(0, 20, 10, 0));

        // --- Main Keypad Container ---
        HBox keypadAndFuncs = new HBox(15);
        keypadAndFuncs.setAlignment(Pos.CENTER);
        keypadAndFuncs.setMaxWidth(Double.MAX_VALUE);
        keypadAndFuncs.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #333333; -fx-border-width: 2 0 0 0;");
        keypadAndFuncs.setPadding(new Insets(15));
        keypadAndFuncs.setVisible(false);
        keypadAndFuncs.setManaged(false);

        // --- ১. Left Side: Number & Math Pad ---
        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        HBox.setHgrow(grid, Priority.ALWAYS);

        for (int i = 0; i < 9; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 9.0);
            grid.getColumnConstraints().add(col);
        }

        // ⚠️ NEW: "DEL" এর জায়গায় "⌫" এবং "!" এর জায়গায় "AC"
        String[][] keys = {
                {"x", "y", "a²", "aᵇ",  "7", "8", "9", "÷", "⌫"},
                {"√", "|a|", "<", ">", "4", "5", "6", "×", "="},
                {"(", ")", "≤", "≥",      "1", "2", "3", "-", "↵"},
                {"pi", "e", ",", "%",     "0", ".", "AC", "+", ""}
        };

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 9; c++) {
                String key = keys[r][c];
                if (key.equals("")) continue;

                Button btn = new Button(key);
                btn.setFocusTraversable(false);
                btn.setMaxWidth(Double.MAX_VALUE);

                if (key.equals("↵")) {
                    btn.setPrefHeight(98);
                    GridPane.setRowSpan(btn, 2);
                } else {
                    btn.setPrefHeight(45);
                }

                // --- Dark Theme Button Colors ---
                boolean isNumberOrDot = key.matches("[0-9.]");
                if (key.equals("↵") || key.equals("=")) {
                    btn.setStyle("-fx-background-color: #4a8af4; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand; -fx-background-radius: 6;");
                } else if (key.equals("⌫") || key.equals("AC")) {
                    // ⚠️ NEW: ⌫ এবং AC দুটোই লাল রঙের হবে যাতে সহজে চোখে পড়ে
                    btn.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-background-radius: 6;");
                } else if (isNumberOrDot || key.equals("x") || key.equals("y")) {
                    btn.setStyle("-fx-background-color: #2D2D2D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand; -fx-background-radius: 6;");
                } else {
                    btn.setStyle("-fx-background-color: #222222; -fx-text-fill: #E0E0E0; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 6;");
                }

                btn.setOnAction(e -> handleKeypadInput(key));
                grid.add(btn, c, r);
            }
        }

        // --- ২. Right Side: Scrollable Functions ---
        VBox funcsContainer = new VBox(10);
        funcsContainer.setPadding(new Insets(5));
        funcsContainer.setStyle("-fx-background-color: #1A1A1A;");

        addFuncCategory(funcsContainer, "Trigonometry", "sin", "cos", "tan", "sec", "csc", "cot");
        addFuncCategory(funcsContainer, "Inverse Trig", "asin", "acos", "atan");
        addFuncCategory(funcsContainer, "Calculus", "d/dx", "int");
        addFuncCategory(funcsContainer, "Math", "abs", "log");

        ScrollPane scrollPane = new ScrollPane(funcsContainer);
        scrollPane.setPrefViewportHeight(190);
        scrollPane.setPrefWidth(180);
        scrollPane.setMinWidth(180);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #1A1A1A; -fx-background-color: transparent; -fx-border-color: #333333; -fx-border-width: 0 0 0 1; -fx-padding: 0 0 0 10;");
        scrollPane.getStylesheets().add(createDarkScrollbarCSS());

        keypadAndFuncs.getChildren().addAll(grid, scrollPane);

        toggleBtn.setOnAction(e -> {
            isKeypadVisible = !isKeypadVisible;
            keypadAndFuncs.setVisible(isKeypadVisible);
            keypadAndFuncs.setManaged(isKeypadVisible);
        });

        overlay.getChildren().addAll(toggleWrapper, keypadAndFuncs);
        return overlay;
    }
    private void addFuncCategory(VBox container, String title, String... funcs) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: #9D00FF; -fx-font-weight: bold; -fx-font-size: 12px;");
        container.getChildren().add(lbl);

        FlowPane flow = new FlowPane(5, 5);
        for (String f : funcs) {
            Button btn = new Button(f);
            btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6;");
            btn.setFocusTraversable(false);
            btn.setOnAction(e -> handleKeypadInput(f + "("));
            flow.getChildren().add(btn);
        }
        container.getChildren().add(flow);
    }
    private void handleKeypadInput(String key) {
        if (activeTextField == null) return;

        int caretPos = activeTextField.getCaretPosition();
        String textToInsert = key;

        switch (key) {
            case "↵":
                focusNextField();
                return;
            case "⌫": // ⚠️ NEW: ব্যাকস্পেস আইকনের লজিক
                if (caretPos > 0) {
                    activeTextField.deleteText(caretPos - 1, caretPos);
                    activeTextField.positionCaret(caretPos - 1);
                }
                return;
            case "AC": // ⚠️ NEW: All Clear লজিক, পুরো টেক্সট মুছে ফেলবে
                activeTextField.clear();
                return;
            case "a²": textToInsert = "^2"; break;
            case "aᵇ": textToInsert = "^"; break;
            case "√": textToInsert = "sqrt("; break;
            case "|a|": textToInsert = "abs("; break;
            case "÷": textToInsert = "/"; break;
            case "×": textToInsert = "*"; break;
            case "≤": textToInsert = "<="; break;
            case "≥": textToInsert = ">="; break;
        }

        activeTextField.insertText(caretPos, textToInsert);
        activeTextField.positionCaret(caretPos + textToInsert.length());
    }
    // Enter (↵) বাটনের আপডেট লজিক
    private void focusNextField() {
        if (activeTextField != null && functionContainer != null) {
            var rows = functionContainer.getChildren();
            int currentIndex = -1;

            // ১. বর্তমান টেক্সটফিল্ডটি কোন রো (Row) এর ভেতর আছে তা খুঁজে বের করা
            for (int i = 0; i < rows.size(); i++) {
                if (containsNode(rows.get(i), activeTextField)) {
                    currentIndex = i;
                    break;
                }
            }

            // ২. যদি বর্তমান বক্সটি পাওয়া যায়
            if (currentIndex != -1) {
                if (currentIndex == rows.size() - 1) {
                    // যদি এটি একদম শেষের বক্স হয়, তবে নতুন একটি বক্স তৈরি করবে
                    addFunctionInputBox(currentIndex + 1);
                } else {
                    // যদি নিচে আরও বক্স থাকে, তবে শুধু নিচের বক্সে ফোকাস করবে
                    focusTextFieldInRowRobust(rows.get(currentIndex + 1));
                }
            }
        }
    }

    // হেল্পার মেথড ১: টেক্সটফিল্ডটি কোনো কন্টেইনারের ভেতর আছে কি না তা ডিপ-সার্চ করে
    private boolean containsNode(javafx.scene.Node parent, javafx.scene.Node target) {
        if (parent == target) return true;
        if (parent instanceof Pane) {
            for (javafx.scene.Node child : ((Pane) parent).getChildren()) {
                if (containsNode(child, target)) return true;
            }
        }
        return false;
    }

    private void focusTextFieldInRowRobust(javafx.scene.Node node) {
        findAndFocusTextField(node);
    }

    private boolean findAndFocusTextField(javafx.scene.Node node) {
        if (node instanceof TextField) {
            TextField tf = (TextField) node;
            tf.requestFocus();
            activeTextField = tf;
            javafx.application.Platform.runLater(() -> tf.positionCaret(tf.getText().length()));
            return true;
        }
        if (node instanceof Pane) {
            for (javafx.scene.Node child : ((Pane) node).getChildren()) {
                if (findAndFocusTextField(child)) return true;
            }
        }
        return false;
    }
    private String createDarkScrollbarCSS() {
        String css = ".scroll-pane .scroll-bar:vertical { -fx-background-color: #1A1A1A; } " +
                ".scroll-pane .scroll-bar:vertical .track { -fx-background-color: #1A1A1A; } " +
                ".scroll-pane .scroll-bar:vertical .thumb { -fx-background-color: #555; -fx-background-radius: 5; } " +
                ".scroll-pane .scroll-bar:vertical .thumb:hover { -fx-background-color: #777; }";
        return "data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(css.getBytes());
    }

    private void addItems(Menu menu, String... items) {
        for (String s : items) {
            MenuItem item = new MenuItem(s);
            item.setStyle("-fx-text-fill: black;");
            item.setOnAction(e -> {
                if (activeTextField != null) {
                    int caretPos = activeTextField.getCaretPosition();
                    activeTextField.insertText(caretPos, s);
                    // লেখার সাইজ অনুযায়ী কার্সরকে সামনে সরিয়ে দেওয়া
                    activeTextField.positionCaret(caretPos + s.length());
                }
            });
            menu.getItems().add(item);
        }
    }
    // -------------------------------------------------------------------------
    // NEW: loadPreset
    // Called from MainApp1 when the user picks an equation from the Library menu.
    // -------------------------------------------------------------------------
    public void loadPreset(EquationPreset preset) {
        functionContainer.getChildren().clear();

        // Reset viewport so the shape is nicely centred
        appState.setScale(preset.getSuggestedScale());
        appState.setOffsetX(0);
        appState.setOffsetY(0);

        List<EquationEntry> entries = preset.getEntries();

        if (entries == null || entries.isEmpty()) {
            addFunctionInputBox(0);
            redrawCallback.run();
            return;
        }

        // Populate rows — every equation gets its individually specified color
        for (int i = 0; i < entries.size(); i++) {
            addFunctionInputBoxSilent(i, entries.get(i).getEquation(), entries.get(i).getColor());
        }

        // Leave one blank row so the user can keep adding equations
        addFunctionInputBox(entries.size());

        redrawCallback.run();
    }

    // -------------------------------------------------------------------------
    // NEW: addFunctionInputBoxSilent
    // Same as addFunctionInputBox() but pre-fills text and skips auto-focus.
    //   • Uses the exact Color passed in (no appState.getNextColor() call)
    //   • Pre-fills the TextField with initialText immediately
    //   • Does NOT steal keyboard focus — avoids janky multi-row loading
    // -------------------------------------------------------------------------
    private void addFunctionInputBoxSilent(int insertIndex, String initialText, Color assignedColor) {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(mainRow, new Insets(5, 20, 15, 20));

        mainRow.setUserData(assignedColor);

        VBox fieldAndPrompt = new VBox(0);
        String normalBoxStyle = "-fx-background-color: #333333; -fx-background-radius: 20px;";
        String activeBoxStyle  = "-fx-background-color: #444444; -fx-background-radius: 20px;";
        fieldAndPrompt.setStyle(normalBoxStyle);

        Color shadowColor = Color.color(assignedColor.getRed(), assignedColor.getGreen(), assignedColor.getBlue());

        javafx.scene.effect.DropShadow normalShadow = new javafx.scene.effect.DropShadow();
        normalShadow.setRadius(10);
        normalShadow.setColor(Color.color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0.25));
        mainRow.setEffect(normalShadow);

        javafx.scene.effect.DropShadow activeShadow = new javafx.scene.effect.DropShadow();
        activeShadow.setRadius(25);
        activeShadow.setSpread(0.3);
        activeShadow.setColor(Color.color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0.8));

        mainRow.getProperties().put("normalShadow", normalShadow);
        mainRow.getProperties().put("activeShadow", activeShadow);

        TextField inputBox = new TextField(autoFormatEquation(initialText));
        inputBox.setPromptText("Ex: ax + b");
        inputBox.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        inputBox.setPadding(new Insets(10, 75, 10, 30));

        // ── Real-time auto-format listener ───────────────────────────────────────
        final boolean[] isAutoFormatting = {false};
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isAutoFormatting[0]) return;
            String formatted = autoFormatEquation(newVal);
            if (!formatted.equals(newVal)) {
                isAutoFormatting[0] = true;
                int caret = inputBox.getCaretPosition();
                inputBox.setText(formatted);
                inputBox.positionCaret(Math.max(0, Math.min(caret + (formatted.length() - newVal.length()), formatted.length())));
                isAutoFormatting[0] = false;
            }
        });

        // ── Math display overlay (pre-filled since this starts unfocused) ───────
        TextFlow mathDisplay = new TextFlow();
        mathDisplay.setPadding(new Insets(12, 75, 10, 30));
        mathDisplay.setMinHeight(40);
        mathDisplay.setMouseTransparent(false);
        mathDisplay.setVisible(false);
        mathDisplay.setOnMouseClicked(e -> inputBox.requestFocus());
        StackPane inputInner = new StackPane(inputBox, mathDisplay);

        // Show the rendered math immediately (row starts unfocused)
        // Pass the original ASCII text to MathRenderer so it renders correctly
        if (!initialText.isEmpty()) {
            MathRenderer.update(mathDisplay, initialText, Color.WHITE);
            inputBox.setOpacity(0);
            mathDisplay.setVisible(true);
        }

        VBox sliderContainer = new VBox(5);
        sliderContainer.setPadding(new Insets(5, 0, 0, 20));

        Runnable cleanupAllSliders = () -> {
            for (javafx.scene.Node sliderRow : new java.util.ArrayList<>(sliderContainer.getChildren())) {
                if (sliderRow.getUserData() instanceof Runnable) {
                    ((Runnable) sliderRow.getUserData()).run(); // Memory cleanup & animation stop
                }
            }
        };

        Runnable deleteAction = () -> {
            cleanupAllSliders.run(); // Sider gulo age clear hobe
            var rows = functionContainer.getChildren();
            int currentIndex = rows.indexOf(mainRow);

            if (rows.size() > 1) {
                // Animation code thakbe...
                rows.remove(mainRow);
            } else {
                inputBox.clear();
                sliderContainer.getChildren().clear();
            }
            redrawCallback.run();
        };

        inputBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                activeTextField = inputBox;
                int currentIndex = functionContainer.getChildren().indexOf(mainRow);
                appState.setFocusedEquationIndex(currentIndex);

                javafx.animation.ScaleTransition scaleUp = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), mainRow);
                scaleUp.setToX(1.08); scaleUp.setToY(1.08);
                scaleUp.play();
                fieldAndPrompt.setStyle(activeBoxStyle);
                mainRow.setEffect(activeShadow);

                // Show the raw text field, hide the rendered overlay
                inputBox.setOpacity(1);
                mathDisplay.setVisible(false);
            } else {
                appState.setFocusedEquationIndex(-1);
                appState.getTemporaryPoints().clear();

                javafx.animation.ScaleTransition scaleDown = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), mainRow);
                scaleDown.setToX(1.0); scaleDown.setToY(1.0);
                scaleDown.play();
                fieldAndPrompt.setStyle(normalBoxStyle);
                mainRow.setEffect(normalShadow);

                // Show pretty math display when not editing; hide the raw text field
                if (!inputBox.getText().trim().isEmpty()) {
                    MathRenderer.update(mathDisplay, EquationHandler.reverseAutoFormat(inputBox.getText()), Color.WHITE);
                    inputBox.setOpacity(0);
                    mathDisplay.setVisible(true);
                } else {
                    inputBox.setOpacity(1);
                    mathDisplay.setVisible(false);
                }
            }
            redrawCallback.run();
        });

        inputBox.setOnKeyPressed(event -> {
            var rows = functionContainer.getChildren();
            int currentIndex = rows.indexOf(mainRow);
            if (event.getCode() == KeyCode.UP) {
                if (currentIndex > 0) { focusTextFieldInRow(rows.get(currentIndex - 1)); event.consume(); }
            } else if (event.getCode() == KeyCode.DOWN) {
                if (currentIndex < rows.size() - 1) { focusTextFieldInRow(rows.get(currentIndex + 1)); event.consume(); }
            } else if (event.getCode() == KeyCode.ENTER) {
                addFunctionInputBox(currentIndex + 1);
                event.consume();
            }
        });

        HBox promptBox = new HBox(8);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.setPadding(new Insets(0, 10, 10, 35));
        promptBox.setVisible(false);
        promptBox.setManaged(false);

        fieldAndPrompt.getChildren().addAll(inputInner, promptBox);

        StackPane inputWrapper = new StackPane();
        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor);
        StackPane.setAlignment(colorDot, Pos.TOP_LEFT);
        StackPane.setMargin(colorDot, new Insets(14, 0, 0, 12));
        colorDot.setCursor(javafx.scene.Cursor.HAND);
        colorDot.setOnMouseClicked(event -> showColorPopup(colorDot, mainRow, event));

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setMaxWidth(70);
        buttonBox.setPickOnBounds(false);
        StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT);
        StackPane.setMargin(buttonBox, new Insets(3, 5, 0, 0));
        String EYE_OPEN = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
        String EYE_CLOSED = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92C21.42 14.97 22.52 13.56 23 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.06 7.13 11 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";

        mainRow.getProperties().put("isHidden", false); // ডিফল্টভাবে Visible থাকবে

        Button eyeBtn = createIconButton(EYE_OPEN, "gray", 12);
        HBox.setMargin(eyeBtn, new Insets(1, 2, 0, 0));

        eyeBtn.setOnMouseEntered(e -> ((SVGPath) eyeBtn.getGraphic()).setFill(Color.WHITE));
        eyeBtn.setOnMouseExited(e -> ((SVGPath) eyeBtn.getGraphic()).setFill(Color.GRAY));

        eyeBtn.setOnAction(e -> {
            boolean isHidden = (boolean) mainRow.getProperties().getOrDefault("isHidden", false);
            mainRow.getProperties().put("isHidden", !isHidden);

            if (!isHidden) {
                // বন্ধ করার আইকন এবং ইনপুট বক্স হালকা করে দেওয়া
                ((SVGPath) eyeBtn.getGraphic()).setContent(EYE_CLOSED);
                inputInner.setOpacity(0.4);
            } else {
                // খোলার আইকন এবং ইনপুট বক্স স্বাভাবিক করে দেওয়া
                ((SVGPath) eyeBtn.getGraphic()).setContent(EYE_OPEN);
                inputInner.setOpacity(1.0);
            }
            redrawCallback.run(); // Graph Canvas আপডেট করবে
        });

        Button closeBtn = createIconButton(
                "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z",
                "gray", 10);
        HBox.setMargin(closeBtn, new Insets(2, 5, 0, 0));
        closeBtn.setOnMouseEntered(e -> ((SVGPath) closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((SVGPath) closeBtn.getGraphic()).setFill(Color.GRAY));
        closeBtn.setOnAction(e -> deleteAction.run());

        buttonBox.getChildren().addAll(eyeBtn, closeBtn);
        inputWrapper.getChildren().addAll(fieldAndPrompt, colorDot, buttonBox);

        javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
        debounce.setOnFinished(evt -> redrawCallback.run());
        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            Set<String> foundVars = EquationHandler.extractVariables(newVal);
            for (javafx.scene.Node sliderRow : new ArrayList<>(sliderContainer.getChildren())) {
                String vName = (String) sliderRow.getProperties().get("varName");
                if (vName != null && !foundVars.contains(vName)) {
                    if (sliderRow.getUserData() instanceof Runnable) {
                        ((Runnable) sliderRow.getUserData()).run();
                    }
                }
            }
            updateSliderPrompt(newVal, promptBox, sliderContainer, inputBox);
            debounce.playFromStart();
        });

        mainRow.getChildren().addAll(inputWrapper, sliderContainer);

        if (insertIndex >= 0 && insertIndex <= functionContainer.getChildren().size()) {
            functionContainer.getChildren().add(insertIndex, mainRow);
        } else {
            functionContainer.getChildren().add(mainRow);
        }
        // No focus stealing — caller drives focus after all rows are loaded.
    }

    /** Updates both DropShadow effects when the user picks a new color. */
    private void updateRowShadowColor(VBox mainRow, Color newColor) {
        Object ns = mainRow.getProperties().get("normalShadow");
        Object as = mainRow.getProperties().get("activeShadow");
        if (ns instanceof javafx.scene.effect.DropShadow) {
            ((javafx.scene.effect.DropShadow) ns).setColor(
                    Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 0.25));
        }
        if (as instanceof javafx.scene.effect.DropShadow) {
            ((javafx.scene.effect.DropShadow) as).setColor(
                    Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 0.8));
        }
    }

    // ── Real-time input pretty-printer ────────────────────────────────────────
    /**
     * Converts raw equation sequences to Unicode as the user types.
     * The back-end parser always receives the reversed (ASCII) form via
     * EquationHandler.reverseAutoFormat(), so this is purely cosmetic.
     *
     * Conversions applied (longest patterns first to avoid partial matches):
     *   sqrt(  →  √(
     *   <=     →  ≤
     *   >=     →  ≥
     *   pi     →  π   (only when not part of a longer word)
     *   ^0-9   →  ⁰¹²³…  (only single-digit exponents not followed by another digit)
     */
    private static String autoFormatEquation(String text) {
        if (text == null || text.isEmpty()) return text;

        // sqrt( → √(  (case-insensitive)
        text = text.replaceAll("(?i)sqrt\\(", "√(");

        // Operators
        text = text.replace("<=", "≤");
        text = text.replace(">=", "≥");

        // pi → π  (not when it is part of a longer word, e.g. "spin" is safe)
        text = text.replaceAll("(?i)(?<![a-zA-Z])pi(?![a-zA-Z])", "π");

        // ^n → superscript digit — only when the digit is NOT followed by another digit
        // This prevents ^10 from being half-converted to ¹0 while the user is still typing.
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\^([0-9])(?![0-9])").matcher(text);
        StringBuffer sb = new StringBuffer();
        String[] supers = {"⁰","¹","²","³","⁴","⁵","⁶","⁷","⁸","⁹"};
        while (m.find()) {
            m.appendReplacement(sb, supers[Integer.parseInt(m.group(1))]);
        }
        m.appendTail(sb);
        text = sb.toString();

        return text;
    }
}