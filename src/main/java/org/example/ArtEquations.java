package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * ArtEquations: Famous mathematical art curves — Lissajous, spirographs, and more.
 * Every equation carries its own individually chosen color via EquationEntry.
 * Palette theme: deep jewel tones and sophisticated hues.
 */
public class ArtEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#4B0082"), Color.web("#008080"), Color.web("#9400D3"),
            Color.web("#B8860B"), Color.web("#2F4F4F"), Color.web("#8B008B"),
            Color.web("#483D8B"), Color.web("#006400"),
    };

    @Override public String getCategoryName() { return "Math Art"; }
    @Override public String getCategoryEmoji() { return "🎨"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                lemniscate(),
                lissajous(),
                spirograph(),
                astroid(),
                epicycloid(),
                Yin_Yang_simble(),
                hypotrochoid()
        );
    }

    // Lemniscate of Bernoulli  (x²+y²)² = 2(x²−y²)
    private EquationPreset lemniscate() {
        return new EquationPreset(
                "Lemniscate ∞", "Lemniscate of Bernoulli: (x²+y²)² = 2(x²−y²)", 60.0,
                EquationEntry.of("(x^2+y^2)^2=2*(x^2-y^2)", "#4B0082")   // deep indigo
        );
    }

    // Lissajous figure (3:2 ratio)
    private EquationPreset lissajous() {
        return new EquationPreset(
                "Lissajous 🎵", "Lissajous figure with 3:2 frequency ratio", 95.0,
                EquationEntry.of("(sin(3*t),sin(2*t))", "#008080")         // deep teal
        );
    }

    // Spirograph  R=8, r=1  →  7-pointed star
    private EquationPreset spirograph() {
        return new EquationPreset(
                "Spirograph 🌀", "Spirograph (hypotrochoid) R=8, r=1", 12.0,
                EquationEntry.of("(7*cos(t)+cos(7*t),7*sin(t)-sin(7*t))", "#9400D3")  // vivid violet
        );
    }

    // Astroid  x=cos³t, y=sin³t
    private EquationPreset astroid() {
        return new EquationPreset(
                "Astroid ✦", "Astroid: x=cos³(t), y=sin³(t)  (x^(2/3)+y^(2/3)=1)", 95.0,
                EquationEntry.of("(cos(t)^3,sin(t)^3)", "#B8860B")        // dark gold
        );
    }

    // Epicycloid R=3, r=1  (4-cusps)
    private EquationPreset epicycloid() {
        return new EquationPreset(
                "Epicycloid ☀", "Epicycloid with R=3, r=1", 20.0,
                EquationEntry.of("((3+1)*cos(t)-1*cos((3+1)*t),(3+1)*sin(t)-1*sin((3+1)*t))", "#CC0000") // crimson
        );
    }

    // Serpentine  y = x/(x²+1)
    private EquationPreset Yin_Yang_simble() {
        return new EquationPreset(
                "Yin_Yang_simble ࿊", "Yin_Yang_simble curve)", 95.0,
                EquationEntry.of("4.74^2 = (x-0)^2 + (y-0)^2", "#000000"),
                EquationEntry.of(".6^2 = (x-0)^2 + (y-2.4)^2", "#000000"),
                EquationEntry.of(".6^2 = (x-0)^2 + (y+2.4)^2", "#000000"),
                EquationEntry.of("2.37^2 = (x-0)^2 + (y-2.37)^2{x>0}", "#000000"),
                EquationEntry.of("2.37^2 = (x-0)^2 + (y+2.37)^2{x<0}", "#000000")
        );
    }

    // Hypotrochoid  R=5, r=3, d=5
    private EquationPreset hypotrochoid() {
        return new EquationPreset(
                "Hypotrochoid 🌟", "Hypotrochoid: R=5, r=3, d=5", 15.0,
                EquationEntry.of("((5-3)*cos(t)+5*cos((5-3)/3*t),(5-3)*sin(t)-5*sin((5-3)/3*t))", "#483D8B") // slate blue
        );
    }
}
