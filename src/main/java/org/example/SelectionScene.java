package org.example;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SelectionScene {

    // =========================================================================
    // SHARED 3D BACKGROUND BUILDER
    // Builds the neon grid + popping wireframe cube SubScene and vignette.
    // Returns a list with [SubScene, vignetteRectangle] — add both to your root.
    // The SubScene's width/height are auto-bound to the given StackPane root.
    // =========================================================================
    private static List<javafx.scene.Node> create3DBackground(StackPane root) {
        Group group3D = new Group();

        AmbientLight light = new AmbientLight(Color.WHITE);
        group3D.getChildren().add(light);

        double gridSize = 6000;
        double step     = 150;
        double lineW    = 2.5;
        double lineH    = 1.5;

        PhongMaterial gridMat = new PhongMaterial(Color.web("#AAAAAA", 0.9));

        for (double i = -gridSize / 2; i <= gridSize / 2; i += step) {
            Box hLine = new Box(gridSize, lineH, lineW);
            hLine.setTranslateZ(i);
            hLine.setMaterial(gridMat);

            Box vLine = new Box(lineW, lineH, gridSize);
            vLine.setTranslateX(i);
            vLine.setTranslateY(lineH);
            vLine.setMaterial(gridMat);

            group3D.getChildren().addAll(hLine, vLine);
        }

        double cubeSize = step - 20;
        List<Group> cubePool = new ArrayList<>();
        Color[] neonColors = {
                Color.web("#00FFFF"),
                Color.web("#FF00FF"),
                Color.web("#39FF14"),
                Color.web("#B026FF")
        };

        for (int i = 0; i < 60; i++) {
            PhongMaterial edgeMat = new PhongMaterial(Color.TRANSPARENT);
            Group wireframe = createWireframeCube(cubeSize, edgeMat);
            wireframe.setScaleY(0.01);
            wireframe.setTranslateY(99999);
            wireframe.getProperties().put("available", true);
            wireframe.getProperties().put("edgeMat", edgeMat);
            cubePool.add(wireframe);
            group3D.getChildren().add(wireframe);
        }

        Timeline spawner = new Timeline(new KeyFrame(Duration.millis(80), e -> {
            Group availableCube = cubePool.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getProperties().get("available")))
                    .findFirst().orElse(null);
            if (availableCube == null) return;

            availableCube.getProperties().put("available", false);

            double x = Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2;
            double z = Math.floor((Math.random() * gridSize - gridSize / 2) / step) * step + step / 2;
            availableCube.setTranslateX(x);
            availableCube.setTranslateZ(z);
            availableCube.setTranslateY(0);

            Color glowColor = neonColors[(int) (Math.random() * neonColors.length)];
            PhongMaterial edgeMat = (PhongMaterial) availableCube.getProperties().get("edgeMat");
            edgeMat.setDiffuseColor(glowColor);
            edgeMat.setSpecularColor(glowColor.brighter());

            double targetHeight = 40 + Math.random() * 400;
            double targetScaleY = targetHeight / cubeSize;

            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1.4), availableCube);
            scaleUp.setFromY(0.01);
            scaleUp.setToY(targetScaleY);
            scaleUp.setAutoReverse(true);
            scaleUp.setCycleCount(2);

            TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1.4), availableCube);
            moveUp.setFromY(0);
            moveUp.setToY(-targetHeight / 2.0);
            moveUp.setAutoReverse(true);
            moveUp.setCycleCount(2);

            scaleUp.setOnFinished(ev -> {
                edgeMat.setDiffuseColor(Color.TRANSPARENT);
                edgeMat.setSpecularColor(Color.TRANSPARENT);
                availableCube.setScaleY(0.01);
                availableCube.setTranslateY(99999);
                availableCube.getProperties().put("available", true);
            });

            double animDur = 1.4;
            Timeline glowAnim = new Timeline(
                    new KeyFrame(Duration.ZERO, ev2 ->
                            edgeMat.setDiffuseColor(glowColor.deriveColor(0, 1, 0.2, 1))),
                    new KeyFrame(Duration.seconds(animDur), ev2 -> {
                        edgeMat.setDiffuseColor(glowColor.brighter().brighter());
                        edgeMat.setSpecularColor(Color.WHITE);
                    })
            );
            glowAnim.setAutoReverse(true);
            glowAnim.setCycleCount(2);
            glowAnim.play();

            scaleUp.play();
            moveUp.play();
        }));
        spawner.setCycleCount(Animation.INDEFINITE);
        spawner.play();

        SubScene subScene3D = new SubScene(group3D, 1920, 1080, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.TRANSPARENT);
        subScene3D.widthProperty().bind(root.widthProperty());
        subScene3D.heightProperty().bind(root.heightProperty());

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(15000);
        camera.setTranslateZ(-2500);
        camera.setTranslateY(-800);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-20);
        subScene3D.setCamera(camera);

        Rectangle edgeFade = new Rectangle();
        edgeFade.widthProperty().bind(root.widthProperty());
        edgeFade.heightProperty().bind(root.heightProperty());
        edgeFade.setMouseTransparent(true);
        RadialGradient fadeGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.7, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.web("#050505", 1.0))
        );
        edgeFade.setFill(fadeGradient);

        List<javafx.scene.Node> layers = new ArrayList<>();
        layers.add(subScene3D);
        layers.add(edgeFade);
        return layers;
    }

    // =========================================================================
    // MAIN SELECTION SCREEN
    // =========================================================================
    public static Parent createView(Runnable onStandard, Runnable onPolar, Runnable onLibrary, Runnable onAbout) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;");

        // 3D background
        root.getChildren().addAll(create3DBackground(root));

        // UI Layer
        VBox buttonsBox = new VBox(40);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsBox.setPadding(new Insets(0, 100, 0, 0));
        buttonsBox.setPickOnBounds(false);

        Button btnStandard = new Button("CARTESIAN");
        Button btnPolar    = new Button("POLAR");
        Button btnLibrary  = new Button("EXPERIENCE CURVES");

        setupFloatingButton(btnStandard, "#00FFFF", onStandard);
        setupFloatingButton(btnPolar,    "#FF003C", onPolar);
        setupFloatingButton(btnLibrary,  "#B026FF", onLibrary);

        buttonsBox.getChildren().addAll(btnStandard, btnPolar, btnLibrary);

        VBox leftBox = new VBox(-15);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 100));
        leftBox.setPickOnBounds(false);

        String fontName = "Arial Black";
        String baseTextStyle = "-fx-font-family: '" + fontName + "'; -fx-font-size: 95px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.2), 15, 0.4, 0, 0);";

        Label word1 = new Label("SHAPE");
        Label word2 = new Label("YOUR");
        Label word3 = new Label("CURIOSITY");

        word1.setStyle(baseTextStyle);
        word2.setStyle(baseTextStyle);
        word3.setStyle(baseTextStyle.replace("#FFFFFF", "#00FFFF"));

        TranslateTransition floatAnim = new TranslateTransition(Duration.millis(2000), word3);
        floatAnim.setByY(-12);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();

        leftBox.getChildren().addAll(word1, word2, word3);

        Button btnAbout = new Button("ABOUT US");
        setupFloatingButton(btnAbout, "#FFFFF0", onAbout);
        btnAbout.setPrefWidth(80);
        btnAbout.setStyle(btnAbout.getStyle() + "-fx-font-size: 9px; -fx-padding: 10 15;");

        StackPane bottomPane = new StackPane(btnAbout);
        bottomPane.setAlignment(Pos.BOTTOM_RIGHT);
        bottomPane.setPadding(new Insets(0, 50, 50, 0));
        bottomPane.setPickOnBounds(false);

        BorderPane uiLayer = new BorderPane();
        uiLayer.setRight(buttonsBox);
        uiLayer.setLeft(leftBox);
        uiLayer.setBottom(bottomPane);
        uiLayer.setPickOnBounds(false);

        root.getChildren().add(uiLayer);
        return root;
    }

    // =========================================================================
    // ABOUT US SCENE
    // =========================================================================
    public static Parent createAboutScene(Runnable onBack) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;");
        root.getChildren().addAll(create3DBackground(root));

        // ── Scrollable content so nothing gets cut off ──
        VBox page = new VBox(50);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(30, 80, 30, 80));

        // ── "ABOUT US" title ──
        Label title = new Label("ABOUT  US");
        title.setStyle(
                "-fx-text-fill: #00FFFF; -fx-font-size: 52px; -fx-font-weight: bold;" +
                        " -fx-font-family: 'Arial Black';"
                        );

        // ── Developer cards — SAME ROW ──
        HBox devRow = new HBox(40);
        devRow.setAlignment(Pos.CENTER);

        VBox card1 = createDevCard(
                "Md. Ashraf Hossain ",
                "Core Developer & Systems Logic",
                "https://github.com/ASHFOX474");

        VBox card2 = createDevCard(
                "Lokonath Basak Bijoy",
                "UI Designer & 3D Visual Developer",
                "https://github.com/GodButcher-Bijoy");

        devRow.getChildren().addAll(card1, card2);

        // ── Project description ──
        VBox descBox = createProjectDescriptionBox();

        // ── Back button ──
        Button btnBack = new Button("← BACK TO MENU");
        setupFloatingButton(btnBack, "#00FFFF", onBack);
        btnBack.setPrefWidth(180);
        btnBack.setStyle(btnBack.getStyle() + "-fx-font-size: 14px; -fx-padding: 10 20;");
        HBox backRow = new HBox(btnBack);
        backRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(backRow, new Insets(-80, 0, 0, 0));
        page.getChildren().addAll(title, devRow, descBox, backRow);

        javafx.scene.control.ScrollPane scroll =
                new javafx.scene.control.ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        root.getChildren().add(scroll);
        return root;
    }

    // ── Single developer card with clickable GitHub link ──────────────────────
    private static VBox createDevCard(String name, String role, String githubUrl) {
        VBox box = new VBox(14);
        box.setStyle(
                "-fx-background-color: rgba(12,12,12,0.82);" +
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 30;" +
                        "-fx-border-color: rgba(0,255,255,0.25);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1.5;");
        box.setPrefWidth(380);

        // Name
        Label nameLbl = new Label(name);
        nameLbl.setStyle(
                "-fx-text-fill: #00FFFF; -fx-font-size: 22px;" +
                        "-fx-font-weight: bold; -fx-font-family: 'Arial';");
        nameLbl.setWrapText(true);

        // Thin divider
        javafx.scene.shape.Rectangle divider = new javafx.scene.shape.Rectangle(320, 1);
        divider.setFill(Color.web("#00FFFF", 0.3));

        // Role
        Label roleLbl = new Label(role);
        roleLbl.setStyle(
                "-fx-text-fill: #CCCCCC; -fx-font-size: 15px; -fx-font-family: 'Arial';");

        // GitHub row: logo + clickable link
        javafx.scene.shape.SVGPath githubLogo = new javafx.scene.shape.SVGPath();
        // Official GitHub mark path (24×24 viewBox)
        githubLogo.setContent(
                "M12 0.297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 " +
                        "11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338" +
                        ".724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7" +
                        "c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 " +
                        "1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305" +
                        ".76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38" +
                        " 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 " +
                        "3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405" +
                        " 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 " +
                        "3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475" +
                        " 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286" +
                        " 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297" +
                        "c0-6.627-5.373-12-12-12");
        // Scale from 24px viewBox down to ~20px display
        double logoScale = 20.0 / 24.0;
        githubLogo.setScaleX(logoScale);
        githubLogo.setScaleY(logoScale);
        githubLogo.setFill(Color.web("#AAAAAA"));

        Label linkLbl = new Label(githubUrl);
        linkLbl.setStyle(
                "-fx-text-fill: #7EC8E3; -fx-font-size: 14px;" +
                        "-fx-font-family: 'Consolas', monospace;" +
                        "-fx-underline: true; -fx-cursor: hand;");
        linkLbl.setWrapText(true);

        HBox githubRow = new HBox(10, githubLogo, linkLbl);
        githubRow.setAlignment(Pos.CENTER_LEFT);
        githubRow.setCursor(javafx.scene.Cursor.HAND);

        // Hover: brighten logo + link
        githubRow.setOnMouseEntered(e -> {
            githubLogo.setFill(Color.web("#FFFFFF"));
            linkLbl.setStyle(linkLbl.getStyle().replace("#7EC8E3", "#00FFFF"));
        });
        githubRow.setOnMouseExited(e -> {
            githubLogo.setFill(Color.web("#AAAAAA"));
            linkLbl.setStyle(linkLbl.getStyle().replace("#00FFFF", "#7EC8E3"));
        });

        // Click: open in system browser
        githubRow.setOnMouseClicked(e -> {
            try {
                java.awt.Desktop.getDesktop()
                        .browse(new java.net.URI(githubUrl));
            } catch (Exception ex) {
                System.err.println("Cannot open browser: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(nameLbl, divider, roleLbl, githubRow);
        return box;
    }

    // ── Project description card ──────────────────────────────────────────────
    private static VBox createProjectDescriptionBox() {
        VBox box = new VBox(16);
        box.setStyle(
                "-fx-background-color: rgba(12,12,12,0.82);" +
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 35;" +
                        "-fx-border-color: rgba(176,38,255,0.35);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1.5;");
        box.setMaxWidth(800);

        Label heading = new Label("⬡  ABOUT GRAPHIFY");
        heading.setStyle(
                "-fx-text-fill: #B026FF; -fx-font-size: 22px;" +
                        "-fx-font-weight: bold; -fx-font-family: 'Arial';");

        javafx.scene.shape.Rectangle divider = new javafx.scene.shape.Rectangle(730, 1);
        divider.setFill(Color.web("#B026FF", 0.3));

        String desc =
                "Graphify is an interactive mathematical graphing application built with JavaFX. " +
                        "It lets you plot any combination of Cartesian, polar, parametric, implicit, and " +
                        "inequality equations in real time — all rendered on a smooth, pannable, zoomable " +
                        "canvas.\n\n" +
                        "The app ships with a curated Curves Library containing hand-crafted presets ranging " +
                        "from cartoon characters and college logos to nature-inspired spirals and mathematical " +
                        "art. Each equation supports boundary conditions, animated sliders for free variables, " +
                        "and a live pretty-print overlay that formats raw input into clean mathematical notation " +
                        "as you type.\n\n" +
                        "Graphify was developed as a semester project at BUET, " +
                        "with a focus on combining rigorous numerical methods with a polished " +
                        "user experience.";

        Label descLbl = new Label(desc);
        descLbl.setStyle(
                "-fx-text-fill: #DDDDDD; -fx-font-size: 14px;" +
                        "-fx-font-family: 'Arial'; -fx-line-spacing: 4;");
        descLbl.setWrapText(true);

        // Tech stack row
        Label techHead = new Label("BUILT WITH");
        techHead.setStyle(
                "-fx-text-fill: #888888; -fx-font-size: 12px;" +
                        "-fx-font-weight: bold; -fx-font-family: 'Arial';");

        HBox techRow = new HBox(12);
        techRow.setAlignment(Pos.CENTER_LEFT);
        for (String tag : new String[]{"Java 17", "JavaFX 21", "exp4j", "CSS / FX-CSS"}) {
            Label chip = new Label(tag);
            chip.setStyle(
                    "-fx-background-color: rgba(176,38,255,0.18);" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 12;" +
                            "-fx-text-fill: #CC88FF;" +
                            "-fx-font-size: 12px; -fx-font-family: 'Arial';");
            techRow.getChildren().add(chip);
        }

        box.getChildren().addAll(heading, divider, descLbl, techHead, techRow);
        return box;
    }

    private static void setupFloatingButton(Button btn, String neonHex, Runnable action) {
        String baseStyle =
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + neonHex + "; " +
                        "-fx-alignment: center; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-color: #080808; " +
                        "-fx-padding: 20 40;";

        btn.setStyle(baseStyle);
        btn.setPrefWidth(260);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(neonHex, 0.4));
        glow.setRadius(15);
        glow.setSpread(0.1);
        btn.setEffect(glow);

        TranslateTransition bob = new TranslateTransition(Duration.millis(1200 + Math.random() * 600), btn);
        bob.setByY(8);
        bob.setAutoReverse(true);
        bob.setCycleCount(Animation.INDEFINITE);
        bob.setInterpolator(Interpolator.EASE_BOTH);
        bob.play();

        ScaleTransition sink = new ScaleTransition(Duration.millis(100), btn);
        sink.setToX(0.85);
        sink.setToY(0.85);

        ScaleTransition rise = new ScaleTransition(Duration.millis(200), btn);
        rise.setToX(1.0);
        rise.setToY(1.0);

        btn.setOnMousePressed(e -> {
            sink.playFromStart();
            if (btn.getText() == "ABOUT US") {
                btn.setStyle("-fx-font-size: 9px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + neonHex + "; " +
                        "-fx-alignment: center; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-color: #080808; " +
                        "-fx-padding: 10 15;");
            }
            else if (btn.getText() == "← BACK TO MENU") {
                btn.setStyle("-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-letter-spacing: 2px; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + neonHex + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + neonHex + "; " +
                        "-fx-alignment: center; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-color: #080808; " +
                        "-fx-padding: 10 20;");
            }
            else {
                btn.setStyle(baseStyle.replace("#080808", "#000000"));
            }
            glow.setRadius(5);
        });

        btn.setOnMouseReleased(e -> {
            rise.playFromStart();
            btn.setStyle(baseStyle);
            glow.setRadius(15);
            if (action != null) action.run();
        });

        btn.setOnMouseEntered(e -> {
            glow.setRadius(25);
            glow.setColor(Color.web(neonHex, 0.7));
        });

        btn.setOnMouseExited(e -> {
            glow.setRadius(15);
            glow.setColor(Color.web(neonHex, 0.4));
        });
    }

    private static Group createWireframeCube(double size, PhongMaterial edgeMat) {
        Group cube = new Group();
        double t  = 4;
        double hs = size / 2;

        PhongMaterial blackMat = new PhongMaterial(Color.web("#050505"));
        Box fill = new Box(size - t * 2, size, size - t * 2);
        fill.setMaterial(blackMat);
        cube.getChildren().add(fill);

        for (int dx : new int[]{-1, 1}) {
            for (int dz : new int[]{-1, 1}) {
                Box e = new Box(t, size, t);
                e.setTranslateX(dx * (hs - t / 2));
                e.setTranslateZ(dz * (hs - t / 2));
                e.setMaterial(edgeMat);
                cube.getChildren().add(e);
            }
        }

        Box tx1 = new Box(size, t, t); tx1.setTranslateY(-hs + t/2); tx1.setTranslateZ(-hs + t/2); tx1.setMaterial(edgeMat);
        Box tx2 = new Box(size, t, t); tx2.setTranslateY(-hs + t/2); tx2.setTranslateZ( hs - t/2); tx2.setMaterial(edgeMat);
        Box tz1 = new Box(t, t, size); tz1.setTranslateY(-hs + t/2); tz1.setTranslateX(-hs + t/2); tz1.setMaterial(edgeMat);
        Box tz2 = new Box(t, t, size); tz2.setTranslateY(-hs + t/2); tz2.setTranslateX( hs - t/2); tz2.setMaterial(edgeMat);

        Box bx1 = new Box(size, t, t); bx1.setTranslateY( hs - t/2); bx1.setTranslateZ(-hs + t/2); bx1.setMaterial(edgeMat);
        Box bx2 = new Box(size, t, t); bx2.setTranslateY( hs - t/2); bx2.setTranslateZ( hs - t/2); bx2.setMaterial(edgeMat);
        Box bz1 = new Box(t, t, size); bz1.setTranslateY( hs - t/2); bz1.setTranslateX(-hs + t/2); bz1.setMaterial(edgeMat);
        Box bz2 = new Box(t, t, size); bz2.setTranslateY( hs - t/2); bz2.setTranslateX( hs - t/2); bz2.setMaterial(edgeMat);

        cube.getChildren().addAll(tx1, tx2, tz1, tz2, bx1, bx2, bz1, bz2);
        return cube;
    }
}