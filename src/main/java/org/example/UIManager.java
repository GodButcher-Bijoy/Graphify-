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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UIManager {

    private final AppState appState;
    private final VBox functionContainer;
    private final Runnable redrawCallback;
    private ScrollPane scrollPane;

    public UIManager(AppState appState, Runnable redrawCallback) {
        this.appState = appState;
        this.redrawCallback = redrawCallback;
        this.functionContainer = new VBox(20);
        this.functionContainer.setStyle("-fx-background-color: transparent;");
    }

    public VBox getFunctionContainer() {
        return functionContainer;
    }

    public HBox createSidebar() {
        // ১. মূল সাইডবার সেটআপ
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(400);
        sidebar.setMinWidth(400); // ⚠️ Important: স্লাইড হওয়ার সময় কন্টেন্ট চ্যাপ্টা হওয়া আটকাবে
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #121212; -fx-background-radius: 20;-fx-border-color: Purple; -fx-border-width: 4px;-fx-border-radius:15;-fx-border-style: solid inside;");// ডানপাশে হালকা বর্ডার

        Label inputLabel = new Label("Enter Functions:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        addFunctionInputBox(0);

        scrollPane = new ScrollPane(functionContainer);
        VBox.setMargin(scrollPane, new Insets(20, 0, 0, 0));
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Black & Grey Scrollbar CSS Magic
        String css = ".scroll-pane { -fx-background: transparent; -fx-background-color: transparent; } " +
                ".scroll-bar:vertical { -fx-background-color: #121212; } " +
                ".scroll-bar:vertical .track { -fx-background-color: #1A1A1A; -fx-border-color: transparent; } " +
                ".scroll-bar:vertical .thumb { -fx-background-color: #555555; -fx-background-radius: 5; } " +
                ".scroll-bar:vertical .thumb:hover { -fx-background-color: #777777; } " +
                ".scroll-bar .increment-button, .scroll-bar .decrement-button { -fx-background-color: transparent; -fx-padding: 0; } " +
                ".scroll-bar .increment-arrow, .scroll-bar .decrement-arrow { -fx-shape: ' '; -fx-padding: 0; }";

        String b64Css = "data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(css.getBytes());
        scrollPane.getStylesheets().add(b64Css);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        sidebar.getChildren().addAll(inputLabel, scrollPane);

        // ২. স্লাইডিং মেকানিজম এবং ক্লিপিং মাস্ক
        Pane slideContainer = new Pane(sidebar);
        slideContainer.setPrefWidth(400);
        slideContainer.setMinWidth(0);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(slideContainer.prefWidthProperty());
        clip.heightProperty().bind(slideContainer.heightProperty());
        slideContainer.setClip(clip);

        sidebar.prefHeightProperty().bind(slideContainer.heightProperty());

        // --- NEW: টগল (Toggle) বাটন (Updated Design) ---
        // আইকন: << (ডাবল শেভরন লেফট), কালার: গ্রে (#AAAAAA)
        Button toggleBtn = createIconButton("M17.59 7.41L16.17 6l-6 6 6 6 1.41-1.41L13 12l4.59-4.59zM11.59 7.41L10.17 6l-6 6 6 6 1.41-1.41L7 12l4.59-4.59z", "#AAAAAA", 24);

        // বেস স্টাইল (কালো ব্যাকগ্রাউন্ড, ডানদিকে রাউন্ড কর্নার)
        String baseStyle = "-fx-background-color: #111111; -fx-background-radius: 0 8 8 0; -fx-cursor: hand; -fx-border-color: #333333; -fx-border-width: 1 1 1 0; -fx-border-radius: 0 8 8 0;";
        // হোভার স্টাইল (একটু উজ্জ্বল কালো)
        String hoverStyle = "-fx-background-color: #252525; -fx-background-radius: 0 8 8 0; -fx-cursor: hand; -fx-border-color: #444444; -fx-border-width: 1 1 1 0; -fx-border-radius: 0 8 8 0;";

        toggleBtn.setStyle(baseStyle);
        toggleBtn.setPrefHeight(60);
        toggleBtn.setMaxHeight(60);
        toggleBtn.setPrefWidth(32); // ডাবল অ্যারোর জন্য একটু চওড়া

        // হোভার ইফেক্ট: ব্যাকগ্রাউন্ড চেঞ্জ হবে এবং আইকন সাদা হবে
        toggleBtn.setOnMouseEntered(e -> {
            toggleBtn.setStyle(hoverStyle);
            ((SVGPath)toggleBtn.getGraphic()).setFill(Color.WHITE);
        });
        toggleBtn.setOnMouseExited(e -> {
            if (slideContainer.getPrefWidth() > 0) { // শুধু যদি ওপেন থাকে তবেই বেস স্টাইলে ফিরবে
                toggleBtn.setStyle(baseStyle);
                ((SVGPath)toggleBtn.getGraphic()).setFill(Color.web("#AAAAAA"));
            }
        });

        // অ্যানিমেশন লজিক
        toggleBtn.setOnAction(e -> {
            boolean isExpanded = slideContainer.getPrefWidth() > 0;
            javafx.animation.Timeline timeline = new javafx.animation.Timeline();

            if (isExpanded) {
                // ১. স্লাইড আউট (বন্ধ হচ্ছে) -> আইকন হবে >>
                ((SVGPath)toggleBtn.getGraphic()).setContent("M6.41 6L5 7.41 9.58 12 5 16.59 6.41 18l6-6-6-6zM12.41 6L11 7.41 15.58 12 11 16.59 12.41 18l6-6-6-6z");
                // বন্ধ অবস্থায় বাটনটা একটু ট্রান্সপারেন্ট করে দেওয়া যায় (অপশনাল)
                toggleBtn.setStyle("-fx-background-color: #11111199; -fx-background-radius: 0 8 8 0; -fx-cursor: hand;");

                timeline.getKeyFrames().add(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                                new javafx.animation.KeyValue(slideContainer.prefWidthProperty(), 0, javafx.animation.Interpolator.EASE_BOTH),
                                new javafx.animation.KeyValue(sidebar.translateXProperty(), -400, javafx.animation.Interpolator.EASE_BOTH)
                        )
                );
            } else {
                // ২. স্লাইড ইন (খুলছে) -> আইকন হবে <<
                ((SVGPath)toggleBtn.getGraphic()).setContent("M17.59 7.41L16.17 6l-6 6 6 6 1.41-1.41L13 12l4.59-4.59zM11.59 7.41L10.17 6l-6 6 6 6 1.41-1.41L7 12l4.59-4.59z");
                toggleBtn.setStyle(baseStyle); // খোলার সময় আবার সলিড কালার

                timeline.getKeyFrames().add(
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                                new javafx.animation.KeyValue(slideContainer.prefWidthProperty(), 400, javafx.animation.Interpolator.EASE_BOTH),
                                new javafx.animation.KeyValue(sidebar.translateXProperty(), 0, javafx.animation.Interpolator.EASE_BOTH)
                        )
                );
            }
            timeline.play();
        });

        // ৩. র‍্যাপার (Wrapper)
        HBox wrapper = new HBox(slideContainer, toggleBtn);
        wrapper.setAlignment(Pos.CENTER_LEFT);

        return wrapper;
    }

    private void addFunctionInputBox(int insertIndex) {
        VBox mainRow = new VBox(5);
        mainRow.setStyle("-fx-background-color: transparent;");

        Color assignedColor = appState.getNextColor();
        mainRow.setUserData(assignedColor);

        VBox fieldAndPrompt = new VBox(0);
        fieldAndPrompt.setStyle("-fx-background-color: White; -fx-background-radius: 10; -fx-border-color: #9D00FF; -fx-border-width: 2; -fx-border-radius: 10;");

        TextField inputBox = new TextField();
        inputBox.setPromptText("Ex: ax + b");
        inputBox.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 15px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        inputBox.setPadding(new Insets(15, 80, 15, 35));

        // --- NEW: Up / Down Arrow ও Enter দিয়ে নেভিগেশন (Fixed Version) ---
        inputBox.setOnKeyPressed(event -> {
            var rows = functionContainer.getChildren();
            int currentIndex = rows.indexOf(mainRow); // সরাসরি mainRow (VBox) এর ইনডেক্স বের করছি
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
                // Enter চাপলে বর্তমান বক্সের ঠিক নিচে নতুন বক্স আসবে
                addFunctionInputBox(currentIndex + 1);
                event.consume();
            } else if (event.getCode() == KeyCode.BACK_SPACE && inputBox.getText().isEmpty()) {
                // ব্যাকস্পেস চাপলে এবং বক্স খালি থাকলে আগের বক্সে যাবে এবং এটা ডিলিট হবে
                if (currentIndex > 0) {
                    focusTextFieldInRow(rows.get(currentIndex - 1));
                    // এখানে ডিলিট করার লজিকটা কল হবে (নিচে ডিফাইন করা আছে)
                    rows.remove(mainRow);
                    redrawCallback.run();
                    event.consume();
                }
            }
        });
        inputBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                int currentIndex = functionContainer.getChildren().indexOf(mainRow);
                appState.setFocusedEquationIndex(currentIndex);
            } else {
                appState.setFocusedEquationIndex(-1);
                appState.getTemporaryPoints().clear();
            }
            redrawCallback.run();
        });
        HBox promptBox = new HBox(8);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.setPadding(new Insets(0, 10, 10, 35));
        promptBox.setVisible(false);
        promptBox.setManaged(false);

        fieldAndPrompt.getChildren().addAll(inputBox, promptBox);

        StackPane inputWrapper = new StackPane();
        javafx.scene.shape.Circle colorDot = new javafx.scene.shape.Circle(6, assignedColor);

        StackPane.setAlignment(colorDot, Pos.TOP_LEFT);
        StackPane.setMargin(colorDot, new Insets(20, 0, 0, 15));

        colorDot.setCursor(javafx.scene.Cursor.HAND);
        colorDot.setOnMouseClicked(event -> showColorPopup(colorDot, mainRow, event));

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setMaxWidth(70);
        buttonBox.setPickOnBounds(false);

        StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT);
        StackPane.setMargin(buttonBox, new Insets(10, 10, 0, 0));

        Button closeBtn = createIconButton("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", "gray", 10);
        HBox.setMargin(closeBtn, new Insets(5, 8, 0, 0));
        closeBtn.setOnMouseEntered(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().add(closeBtn);
        inputWrapper.getChildren().addAll(fieldAndPrompt, colorDot, buttonBox);

        VBox sliderContainer = new VBox(5);
        sliderContainer.setPadding(new Insets(5, 0, 0, 20));

        // ডিলিট অ্যাকশন
        Runnable deleteAction = () -> {
            if (functionContainer.getChildren().size() > 1) {
                functionContainer.getChildren().remove(mainRow);
                redrawCallback.run();
            } else {
                inputBox.clear();
                sliderContainer.getChildren().clear();
                redrawCallback.run();
            }
        };
        closeBtn.setOnAction(e -> deleteAction.run());

        inputBox.textProperty().addListener((obs, oldVal, newVal) -> {
            updateSliderPrompt(newVal, promptBox, sliderContainer, inputBox);
            redrawCallback.run();
        });

        mainRow.getChildren().addAll(inputWrapper, sliderContainer);

        // ইনসার্ট লজিক
        if (insertIndex >= 0 && insertIndex <= functionContainer.getChildren().size()) {
            functionContainer.getChildren().add(insertIndex, mainRow);
        } else {
            functionContainer.getChildren().add(mainRow);
        }

        // ফোকাস ও স্ক্রল অ্যানিমেশন
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


        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #222; -fx-background-radius: 5; -fx-padding: 8; -fx-border-color: #444; -fx-border-radius: 5;");

        Label nameLbl = new Label(varName + " =");
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 14));

        TextField valInput = new TextField(String.format("%.2f", appState.getGlobalVariables().get(varName)));
        valInput.setPrefWidth(60);
        valInput.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 12px;");

        Button closeBtn = createIconButton("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", "gray", 14);

        Slider slider = new Slider(-10, 10, appState.getGlobalVariables().get(varName));
        slider.setPrefWidth(120);

        Runnable updateRange = () -> {
            try {
                double val = Double.parseDouble(valInput.getText());
                appState.getGlobalVariables().put(varName, val);
                double rangeSpan = 10;
                slider.setMin(val - rangeSpan);
                slider.setMax(val + rangeSpan);
                slider.setValue(val);
                redrawCallback.run();
            } catch (NumberFormatException ex) {
                valInput.setText(String.format("%.2f", appState.getGlobalVariables().get(varName)));
            }
        };

        valInput.setOnAction(e -> updateRange.run());
        valInput.focusedProperty().addListener((obs, o, n) -> { if(!n) updateRange.run(); });

        slider.valueProperty().addListener((obs, o, n) -> {
            if (!slider.isValueChanging() && !valInput.isFocused()) {
                valInput.setText(String.format("%.2f", n));
                appState.getGlobalVariables().put(varName, n.doubleValue());
                redrawCallback.run();
            }
        });

        slider.valueProperty().addListener((obs, o, n) -> {
            appState.getGlobalVariables().put(varName, n.doubleValue());
            valInput.setText(String.format("%.2f", n));
            redrawCallback.run();
        });

        closeBtn.setOnAction(e -> {
            sliderContainer.getChildren().remove(row);
            appState.getActiveSliderVars().remove(varName);
            updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
            redrawCallback.run();
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(nameLbl, valInput, slider, spacer, closeBtn);
        sliderContainer.getChildren().add(row);

        updateSliderPrompt(inputBox.getText(), promptBox, sliderContainer, inputBox);
        redrawCallback.run();
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
                mainRow.setUserData(c); // Backend er jonno color save korlam
                redrawCallback.run();   // Graph abar draw korlam
                popup.hide();           // Popup bondho kore dilam
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
        if (row instanceof javafx.scene.layout.Pane) {
            for (javafx.scene.Node child : ((javafx.scene.layout.Pane) row).getChildren()) {
                if (child instanceof TextField) {
                    TextField tf = (TextField) child;
                    tf.requestFocus();
                    // কার্সরটিকে টেক্সটের শেষে নিয়ে যাবে
                    javafx.application.Platform.runLater(() -> tf.positionCaret(tf.getText().length()));
                    break;
                }
            }
        }
    }
}