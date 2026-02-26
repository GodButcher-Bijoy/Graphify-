import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class IntroScene {

    public static Scene create(Stage stage, Runnable onComplete) {
        Pane root = new Pane();
        double width = 1000;
        double height = 700;
        root.setStyle("-fx-background-color: #002366;");

        Line hLine = new Line(0, height - 100, width, height - 100);
        hLine.setStroke(Color.WHITE);
        hLine.setStrokeWidth(5);
        hLine.setScaleX(0);

        Line vLine = new Line(width / 2, 0, width / 2, height);
        vLine.setStroke(Color.WHITE);
        vLine.setStrokeWidth(5);
        vLine.setScaleY(0);

        Text title = new Text("Graphify");
        title.setFont(Font.font("Pristina", FontWeight.BOLD, 85));
        title.setStroke(Color.WHITE);
        title.setStrokeWidth(0.3);
        title.setFill(Color.TRANSPARENT);
        title.setOpacity(0);
        title.setX((width / 2) - 120);
        title.setY(height / 2);

        root.getChildren().addAll(hLine, vLine, title);

        ScaleTransition hAnim = new ScaleTransition(Duration.seconds(1.5), hLine);
        hAnim.setFromX(0); hAnim.setToX(1);

        ScaleTransition vAnim = new ScaleTransition(Duration.seconds(1), vLine);
        vAnim.setFromY(0); vAnim.setToY(1);

        TranslateTransition slideLine = new TranslateTransition(Duration.seconds(1), vLine);
        slideLine.setToX(-(width / 2) + 50);

        TranslateTransition moveTitleUp = new TranslateTransition(Duration.seconds(1), title);
        moveTitleUp.setByY(-100);

        FadeTransition textFade = new FadeTransition(Duration.seconds(1.5), title);
        textFade.setFromValue(0); textFade.setToValue(1);

        FillTransition textFill = new FillTransition(Duration.seconds(2), title);
        textFill.setFromValue(Color.TRANSPARENT);
        textFill.setToValue(Color.WHITE);

        ParallelTransition sequence2 = new ParallelTransition(slideLine, textFade, textFill);
        SequentialTransition sequence = new SequentialTransition(hAnim, vAnim, sequence2);

        sequence.play();

        // স্ক্রিনে ক্লিক করলে অন-কমপ্লিট ইভেন্ট ট্রিগার হবে
        root.setOnMouseClicked(event -> {
            onComplete.run();
        });

        return new Scene(root, width, height);
    }
}