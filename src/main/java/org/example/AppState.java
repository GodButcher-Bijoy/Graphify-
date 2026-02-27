package org.example;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppState {
    // --- Graphing State Variables ---
    private double scale = 40; // Pixels per unit
    private double offsetX = 0;
    private double offsetY = 0;

    // --- NEW: Intersection Tracking ---
    // 1. Tracks which equation's input box is currently active (-1 means none)
    private int focusedEquationIndex = -1;
    // 2. Holds temporary intersections (axes and other graphs) for the focused equation
    private List<Point2D> temporaryPoints = new ArrayList<>();
    // 3. Holds points that the user has clicked on (persistently visible)
    private Set<Point2D> pinnedPoints = new HashSet<>();

    // --- Global Variables & Tracking ---
    private Map<String, Double> globalVariables = new HashMap<>();
    private Set<String> activeSliderVars = new HashSet<>();

    // Colors for different graphs
    // 20 ta distinct and beautiful colors
    private final Color[] graphColors = {
            Color.web("#FF3B30"), Color.web("#007AFF"), Color.web("#34C759"), Color.web("#FF9500"), Color.web("#AF52DE"),
            Color.web("#5AC8FA"), Color.web("#5856D6"), Color.web("#FF2D55"), Color.web("#8E8E93"), Color.web("#E5C07B"),
            Color.web("#D19A66"), Color.web("#98C379"), Color.web("#61AFEF"), Color.web("#C678DD"), Color.web("#56B6C2"),
            Color.web("#E06C75"), Color.web("#F0E68C"), Color.web("#FF6347"), Color.web("#7FFFD4")
    };

    private int globalColorIndex = 0;

    // --- Getters & Setters ---

    public Color[] getGraphColors() {
        return graphColors;
    }

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

    // --- Intersection Trackers Getters & Setters ---
    public int getFocusedEquationIndex() { return focusedEquationIndex; }
    public void setFocusedEquationIndex(int focusedEquationIndex) { this.focusedEquationIndex = focusedEquationIndex; }

    public List<Point2D> getTemporaryPoints() { return temporaryPoints; }
    public void setTemporaryPoints(List<Point2D> points) { this.temporaryPoints = points; }

    public Set<Point2D> getPinnedPoints() { return pinnedPoints; }

    public void togglePinnedPoint(Point2D p) {
        // Find if a point is already pinned nearby (threshold check: 0.2 tolerance)
        Point2D existing = pinnedPoints.stream()
                .filter(pinned -> pinned.distance(p) < 0.2)
                .findFirst().orElse(null);

        if (existing != null) {
            pinnedPoints.remove(existing); // Unpin if already pinned
        } else {
            pinnedPoints.add(p); // Pin the new point
        }
    }
}