package org.example;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class IntroScene {

    private static final String APP_NAME = "GRAPHIFY";

    public static Scene create(Stage stage, Runnable onComplete) {

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:#4B5563;");

        Group content3D = new Group();
        // Set height to 700
        SubScene subScene = new SubScene(content3D, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(root.widthProperty());
        subScene.heightProperty().bind(root.heightProperty());

        // --- 1. Centered Ground (Middle of the screen) ---
        double groundLevel = 350;
        Rectangle ground = new Rectangle(4000, 1500);
        ground.setTranslateX(-2000);
        ground.setTranslateY(groundLevel);

        LinearGradient groundGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#CCCCCC", 0.3)),
                new Stop(1, Color.web("#000000", 0.0))
        );
        ground.setFill(groundGradient);

        // --- 2. 3D Perspective Grid ---
        int spacing   = 80;
        int halfW     = 1200;
        int gridDepth = 1200;

        Group floorGrid   = new Group();
        List<Box> zLines  = new ArrayList<>();
        List<Box> xLines  = new ArrayList<>();

        // Z-axis lines
        for (int z = spacing; z <= gridDepth; z += spacing) {
            boolean isAxis = (z == 0);
            double opacity = isAxis ? 0.6 : 0.15;

            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(Color.web("#FFFFFF", opacity));
            mat.setSpecularColor(Color.web("#FFFFFF", opacity * 0.5));

            Box line = new Box(halfW * 2, 1.5, 1.5);
            line.setTranslateY(groundLevel);
            line.setTranslateZ(z);
            line.setMaterial(mat);
            line.setOpacity(0.0);
            zLines.add(line);
            floorGrid.getChildren().add(line);
        }

        // X-axis lines
        for (int x = -halfW; x <= halfW; x += spacing) {
            boolean isAxis = (x == 0);
            double opacity = isAxis ? 0.6 : 0.15;

            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(Color.web("#FFFFFF", opacity));
            mat.setSpecularColor(Color.web("#FFFFFF", opacity * 0.5));

            Box line = new Box(1.5, 1.5, gridDepth);
            line.setTranslateY(groundLevel);
            line.setTranslateX(x);
            line.setTranslateZ(gridDepth / 2.0);
            line.setMaterial(mat);
            line.setOpacity(0.0);
            xLines.add(line);
            floorGrid.getChildren().add(line);
        }

        // Glowing horizon baseline
        PhongMaterial horizonMat = new PhongMaterial(Color.web("#FFFFFF", 0.8));
        Box horizonBar = new Box(halfW * 2, 3, 3);
        horizonBar.setTranslateY(groundLevel - 1);
        horizonBar.setTranslateZ(0);
        horizonBar.setMaterial(horizonMat);
        horizonBar.setOpacity(1.0);
        floorGrid.getChildren().add(horizonBar);

        Rectangle floorLine = new Rectangle(4000, 2);
        floorLine.setTranslateX(-2000);
        floorLine.setTranslateY(groundLevel);
        floorLine.setFill(Color.web("#FFFFFF", 0.5));

        // --- 3. Cube Settings (Shifted Upwards) ---
        double cubeSize = 70;
        double startX = -350;

        // এখানে shiftUp ভ্যালু দিয়ে কিউবটাকে কতটা উপরে তুলবো তা নির্ধারণ করেছি
        double shiftUp = 100;
        double restY = groundLevel - shiftUp - (cubeSize / 2.0);

        Group cubeGroup = createCustomColoredCube(cubeSize);
        cubeGroup.setTranslateX(startX);
        cubeGroup.setTranslateY(-600);
        cubeGroup.setRotationAxis(Rotate.Z_AXIS);

        // --- 4. Text Setup (Shifted Upwards) ---
        HBox textContainer = new HBox(10);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setMaxWidth(800);

        // টেক্সটগুলোকেও shiftUp পরিমাণ উপরে তুলে দেওয়া হয়েছে
        textContainer.setTranslateY(groundLevel - 440 - shiftUp);
        textContainer.setTranslateX(0);

        List<Label> letters = new ArrayList<>();
        for (char c : APP_NAME.toCharArray()) {
            Label l = new Label(String.valueOf(c));
            l.setFont(Font.font("Montserrat", FontWeight.BOLD, 85));
            l.setTextFill(Color.WHITE);
            l.setOpacity(0.0);
            letters.add(l);
            textContainer.getChildren().add(l);
        }

        // Blinking "Click to continue" Label
        Label clickLabel = new Label("Click to continue");
        clickLabel.setFont(Font.font("Montserrat", FontWeight.NORMAL, 20));
        clickLabel.setTextFill(Color.web("#FFFFFF", 0.8));
        clickLabel.setOpacity(0.0);
        StackPane.setAlignment(clickLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(clickLabel, new javafx.geometry.Insets(0, 0, 100, 0));

        content3D.getChildren().addAll(ground, floorGrid, floorLine, cubeGroup);
        root.getChildren().addAll(subScene, textContainer, clickLabel);

        // --- 5. Camera ---
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateZ(-1200);
        camera.setTranslateY(-50);
        camera.getTransforms().addAll(
                new Rotate(-10, Rotate.X_AXIS),
                new Rotate(0, Rotate.Y_AXIS)
        );
        subScene.setCamera(camera);

        content3D.getChildren().add(new AmbientLight(Color.web("#FFFFFF", 0.8)));

        // --- 6. Animation ---

        // 3D Graph Paper Reveal Sequence (প্রথম অ্যানিমেশন)
        SequentialTransition gridReveal = new SequentialTransition();

        FadeTransition horizonFade = new FadeTransition(Duration.millis(150), horizonBar);
        horizonFade.setToValue(1.0);
        gridReveal.getChildren().add(horizonFade);

        ParallelTransition zLineSweep = new ParallelTransition();
        for (int idx = 0; idx < zLines.size(); idx++) {
            Box line = zLines.get(idx);
            FadeTransition ft = new FadeTransition(Duration.millis(150), line);
            ft.setDelay(Duration.millis(idx * 75));
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            zLineSweep.getChildren().add(ft);
        }
        gridReveal.getChildren().add(zLineSweep);

        ParallelTransition xLineFan = new ParallelTransition();
        int mid = xLines.size() / 2;
        for (int i = 0; i < xLines.size(); i++) {
            int dist  = Math.abs(i - mid);
            Box line  = xLines.get(i);
            FadeTransition ft = new FadeTransition(Duration.millis(150), line);
            ft.setDelay(Duration.millis(dist * 75));
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            xLineFan.getChildren().add(ft);
        }
        gridReveal.getChildren().add(xLineFan);

        // Cube Drop & Roll Sequence (দ্বিতীয় অ্যানিমেশন)
        TranslateTransition drop = new TranslateTransition(Duration.seconds(.8), cubeGroup);
        drop.setToY(restY);
        drop.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition bounce = new TranslateTransition(Duration.seconds(0.2), cubeGroup);
        bounce.setByY(-30);
        bounce.setCycleCount(2);
        bounce.setAutoReverse(true);

        SequentialTransition rollingSequence = new SequentialTransition();
        double currentX = startX;
        double currentAngle = 0;
        int totalRolls = letters.size() + 6;

        for (int i = 0; i < totalRolls; i++) {
            final double cX = currentX;
            final double cA = currentAngle;

            Transition rollStep = new Transition() {
                {
                    setCycleDuration(Duration.seconds(0.4));
                    setInterpolator(Interpolator.LINEAR);
                }
                @Override
                protected void interpolate(double frac) {
                    double theta = frac * 90;
                    double rad = Math.toRadians(theta);
                    double r = cubeSize / 2.0;
                    double px = cX + r;
                    double py = restY + r;
                    double nx = px - r * Math.cos(rad) + r * Math.sin(rad);
                    double ny = py - r * Math.sin(rad) - r * Math.cos(rad);
                    cubeGroup.setTranslateX(nx);
                    cubeGroup.setTranslateY(ny);
                    cubeGroup.setRotate(cA + theta);
                }
            };

            if (i < letters.size()) {
                FadeTransition reveal = new FadeTransition(Duration.seconds(0.25), letters.get(i));
                reveal.setToValue(1.0);
                ParallelTransition sync = new ParallelTransition(rollStep, reveal);
                rollingSequence.getChildren().add(sync);
            } else {
                rollingSequence.getChildren().add(rollStep);
            }
            currentX += cubeSize;
            currentAngle += 90;
        }

        // সিকোয়েন্স চেঞ্জ করা হয়েছে: আগে gridReveal, তারপর drop, bounce ও rollingSequence
        SequentialTransition masterSequence = new SequentialTransition(
                gridReveal,
                drop, bounce, new PauseTransition(Duration.seconds(0.1)),
                rollingSequence
        );

        masterSequence.setOnFinished(e -> {
            // Blink the "Click to continue" text
            FadeTransition blink = new FadeTransition(Duration.seconds(0.8), clickLabel);
            blink.setFromValue(0.2);
            blink.setToValue(1.0);
            blink.setCycleCount(Animation.INDEFINITE);
            blink.setAutoReverse(true);
            blink.play();

            // Setup click listener to proceed
            root.setOnMouseClicked(event -> {
                if(onComplete != null) onComplete.run();
            });
        });

        masterSequence.play();

        return new Scene(root, 1000, 700, true);
    }

    private static Group createCustomColoredCube(double size) {
        Group group = new Group();
        double r = size / 2.0;
        PhongMaterial sideMat = new PhongMaterial(Color.BLACK);
        PhongMaterial frontMat = new PhongMaterial(Color.web("#4169E1"));
        Box front = new Box(size, size, 1); front.setTranslateZ(-r); front.setMaterial(frontMat);
        Box back = new Box(size, size, 1); back.setTranslateZ(r); back.setMaterial(frontMat);
        Box left = new Box(1, size, size); left.setTranslateX(-r); left.setMaterial(sideMat);
        Box right = new Box(1, size, size); right.setTranslateX(r); right.setMaterial(sideMat);
        Box top = new Box(size, 1, size); top.setTranslateY(-r); top.setMaterial(sideMat);
        Box bottom = new Box(size, 1, size); bottom.setTranslateY(r); bottom.setMaterial(sideMat);
        group.getChildren().addAll(front, back, left, right, top, bottom);
        return group;
    }
}