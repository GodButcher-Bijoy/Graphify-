import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppState {
    // --- Graphing State Variables ---
    private double scale = 40; // Pixels per unit
    private double offsetX = 0;
    private double offsetY = 0;

    // --- Global Variables & Tracking ---
    private Map<String, Double> globalVariables = new HashMap<>();
    private Set<String> activeSliderVars = new HashSet<>();

    // Colors for different graphs
    private final Color[] graphColors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
    private int globalColorIndex = 0;

    // --- Getters & Setters ---

    public double getScale() { return scale; }
    public void setScale(double scale) { this.scale = scale; }

    public double getOffsetX() { return offsetX; }
    public void setOffsetX(double offsetX) { this.offsetX = offsetX; }

    public double getOffsetY() { return offsetY; }
    public void setOffsetY(double offsetY) { this.offsetY = offsetY; }

    public Map<String, Double> getGlobalVariables() { return globalVariables; }
    public Set<String> getActiveSliderVars() { return activeSliderVars; }

    // Color Management Logic
    public Color getNextColor() {
        Color color = graphColors[globalColorIndex % graphColors.length];
        globalColorIndex++;
        return color;
    }
}