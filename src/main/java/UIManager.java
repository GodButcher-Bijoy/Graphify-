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

    public UIManager(AppState appState, Runnable redrawCallback) {
        this.appState = appState;
        this.redrawCallback = redrawCallback;
        this.functionContainer = new VBox(20);
        this.functionContainer.setStyle("-fx-background-color: transparent;");
    }

    public VBox getFunctionContainer() {
        return functionContainer;
    }

    public VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(400);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #121212; -fx-border-color: Purple; -fx-border-width: 4px; -fx-border-style: solid inside;");

        Label inputLabel = new Label("Enter Functions:");
        inputLabel.setTextFill(Color.DEEPPINK);
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        addFunctionInputBox(0);

        ScrollPane scrollPane = new ScrollPane(functionContainer);
        VBox.setMargin(scrollPane, new Insets(20, 0, 0, 0));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        sidebar.getChildren().addAll(inputLabel, scrollPane);

        return sidebar;
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

        HBox buttonBox = new HBox(8);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setMaxWidth(70);

        // --- FIX ADDED HERE ---
        buttonBox.setPickOnBounds(false); // Faka jaygay click pass korbe
        StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT); // Sothik bhabe right e align korbe
        // ----------------------

        StackPane.setMargin(buttonBox, new Insets(10, 10, 0, 0));

        Button closeBtn = createIconButton("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z", "gray", 18);
        closeBtn.setOnMouseEntered(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.RED));
        closeBtn.setOnMouseExited(e -> ((SVGPath)closeBtn.getGraphic()).setFill(Color.GRAY));

        buttonBox.getChildren().add(closeBtn);
        inputWrapper.getChildren().addAll(fieldAndPrompt, colorDot, buttonBox);

        VBox sliderContainer = new VBox(5);
        sliderContainer.setPadding(new Insets(5, 0, 0, 20));

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

        inputBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int currentIndex = functionContainer.getChildren().indexOf(mainRow);
                addFunctionInputBox(currentIndex + 1);
            }
            if (event.getCode() == KeyCode.BACK_SPACE && inputBox.getText().isEmpty()) {
                int index = functionContainer.getChildren().indexOf(mainRow);
                if (index > 0) {
                    VBox prevRow = (VBox) functionContainer.getChildren().get(index - 1);
                    StackPane prevWrapper = (StackPane) prevRow.getChildren().get(0);
                    VBox prevFieldWrapper = (VBox) prevWrapper.getChildren().get(0);
                    ((TextField) prevFieldWrapper.getChildren().get(0)).requestFocus();
                    deleteAction.run();
                }
            }
        });

        mainRow.getChildren().addAll(inputWrapper, sliderContainer);

        if (insertIndex >= 0 && insertIndex <= functionContainer.getChildren().size()) {
            functionContainer.getChildren().add(insertIndex, mainRow);
        } else {
            functionContainer.getChildren().add(mainRow);
        }

        inputBox.requestFocus();
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
}