package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * NatureEquations: Beautiful curves inspired by nature — flowers, spirals, leaves.
 * Every equation carries its own individually chosen color via EquationEntry.
 */
public class NatureEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#2E8B57"), Color.web("#FF69B4"), Color.web("#4682B4"),
            Color.web("#FFD700"), Color.web("#8B4513"), Color.web("#98FB98"),
            Color.web("#FF7F50"), Color.web("#00CED1"),
    };

    @Override public String getCategoryName() { return "Nature"; }
    @Override public String getCategoryEmoji() { return "🌿"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                roseCurve(),
                cardioid(),
                fourLeafClover(),
                nautilus(),
                sunflower(),
                limaçon()
        );
    }

    // 5-petal rose  r = cos(5θ)
    private EquationPreset roseCurve() {
        return new EquationPreset(
                "Rose Curve 🌹", "5-petal rose curve: r = cos(5θ)", 90.0,
                EquationEntry.of("(cos(5*t)*cos(t),cos(5*t)*sin(t))", "#E8093A")  // deep rose red
        );
    }

    // Cardioid  r = 1 - cos(θ)
    private EquationPreset cardioid() {
        return new EquationPreset(
                "Cardioid 💕", "Cardioid: r = 1 - cos(θ), parametric form", 55.0,
                EquationEntry.of("((2*cos(t)-cos(2*t)),(2*sin(t)-sin(2*t)))", "#FF69B4")  // hot pink
        );
    }

    // Four-leaf clover  r = cos(2θ)
    private EquationPreset fourLeafClover() {
        return new EquationPreset(
                "Four-Leaf Clover 🍀", "4-petal rose: r = cos(2θ)", 90.0,
                EquationEntry.of("(cos(2*t)*cos(t),cos(2*t)*sin(t))", "#2E8B57")  // sea green
        );
    }

    // Nautilus / logarithmic spiral  r = e^(0.2θ)
    private EquationPreset nautilus() {
        return new EquationPreset(
                "Nautilus Spiral 🐚", "Logarithmic spiral r = e^(0.2θ)", 20.0,
                EquationEntry.of("(exp(0.2*t)*cos(t),exp(0.2*t)*sin(t))", "#4682B4")  // ocean blue
        );
    }

    // Sunflower petals  r = 1 + 0.5·cos(8θ)
    private EquationPreset sunflower() {
        return new EquationPreset(
                "Sunflower 🌻", "Sunflower petal curve r = 1 + 0.5·cos(8θ)", 40.0,
                EquationEntry.of("((4+1.8*cos(10*t))*cos(t),(4+1.8*cos(10*t))*sin(t))", "#fcbf05"),// sunflower gold
                EquationEntry.of("((4+1.8*cos(10.5*t))*cos(t),(4+1.8*cos(10.5*t))*sin(t))", "#fc8d05"),
                EquationEntry.of("abs(x)<=.2{-12<=y<=-2.1}", "#20b51d"),
                EquationEntry.of("((x-2)^2)/3+((y+8)^2)/1<=1", "#20b51d"),
                EquationEntry.of("x^2+y^2<=4.7", "#000000"),
                EquationEntry.of("x^2+y^2<=1", "#783a16")
        );
    }

    // Limaçon with inner loop  r = 1 + 2cos(θ)
    private EquationPreset limaçon() {
        return new EquationPreset(
                "Limaçon 🐌", "Limaçon with inner loop: r = 1 + 2cos(θ)", 70.0,
                EquationEntry.of("((1+2*cos(t))*cos(t),(1+2*cos(t))*sin(t))", "#FF7F50")  // coral
        );
    }
}
