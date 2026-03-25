package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * CollegeEquations: College / university abbreviations drawn with math equations.
 * Every letter is constructed from line-segment equations (with boundary conditions)
 * and semicircles so they fit neatly on the standard graph view.
 *
 * Each college uses its real institutional color(s). Every equation in a preset
 * carries its own individually assigned color via EquationEntry.
 *
 * Letter geometry uses a 3-unit-tall, 2-unit-wide grid per character,
 * separated by 1-unit gaps. Characters are centred as a group on the origin.
 */
public class CollegeEquations extends EquationCategory {

    private static final Color[] PALETTE = {
            Color.web("#003087"), Color.web("#FFC72C"), Color.web("#8B0000"),
            Color.web("#1A5276"), Color.web("#117A65"), Color.web("#6C3483"),
            Color.web("#A93226"), Color.web("#1F618D"),
    };

    @Override public String getCategoryName() { return "College"; }
    @Override public String getCategoryEmoji() { return "🎓"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                ndc(), hcc(), vnc(), mit(), iit(), buet(), du()
        );
    }

    // =========================================================================
    // NDC — Navy Blue (institutional color)
    // =========================================================================
    private EquationPreset ndc() {
        return new EquationPreset(
                "NDC", "NDC college sign drawn with equations", 35.0,
                // --- N ---
                EquationEntry.of("x=-8{-1.5<=y<=1.5}",            "#003087"),
                EquationEntry.of("y=-1.5*x-10.5{-8<=x<=-6}",      "#003087"),
                EquationEntry.of("x=-6{-1.5<=y<=1.5}",            "#003087"),
                // --- D ---
                EquationEntry.of("x=-1{-1.5<=y<=1.5}",            "#1A5276"),
                EquationEntry.of("(x+1)^2+y^2=2.25{x>=-1}",       "#1A5276"),
                // --- C ---
                EquationEntry.of("(x-6)^2+y^2=2.25{x<=6}",        "#0055A5")   // lighter blue C
        );
    }

    // =========================================================================
    // HCC — Maroon (classic college maroon)
    // =========================================================================
    private EquationPreset hcc() {
        return new EquationPreset(
                "HCC", "HCC college sign drawn with equations", 35.0,
                // --- H ---
                EquationEntry.of("x=-8{-1.5<=y<=1.5}",            "#8B0000"),
                EquationEntry.of("y=0{-8<=x<=-6}",                "#A52A2A"),
                EquationEntry.of("x=-6{-1.5<=y<=1.5}",            "#8B0000"),
                // --- C1 ---
                EquationEntry.of("x^2+y^2=2.25{x<=0}",            "#C0392B"),
                // --- C2 ---
                EquationEntry.of("(x-7)^2+y^2=2.25{x<=7}",        "#C0392B")
        );
    }

    // =========================================================================
    // VNC — Forest Green
    // =========================================================================
    private EquationPreset vnc() {
        return new EquationPreset(
                "VNC", "VNC college sign drawn with equations", 35.0,
                // --- V ---
                EquationEntry.of("y=-3*x-22.5{-8<=x<=-7}",        "#117A65"),
                EquationEntry.of("y=3*x+19.5{-7<=x<=-6}",         "#117A65"),
                // --- N ---
                EquationEntry.of("x=-1{-1.5<=y<=1.5}",            "#1E8449"),
                EquationEntry.of("y=-1.5*x{-1<=x<=1}",            "#1E8449"),
                EquationEntry.of("x=1{-1.5<=y<=1.5}",             "#1E8449"),
                // --- C ---
                EquationEntry.of("(x-7)^2+y^2=2.25{x<=7}",        "#27AE60")
        );
    }

    // =========================================================================
    // MIT — Cardinal Red (MIT's actual color #A31F34)
    // =========================================================================
    private EquationPreset mit() {
        return new EquationPreset(
                "MIT", "MIT drawn with line-segment equations", 35.0,
                // --- M ---
                EquationEntry.of("x=-9{-1.5<=y<=1.5}",            "#A31F34"),
                EquationEntry.of("y=-1.5*x-12{-9<=x<=-8}",        "#A31F34"),
                EquationEntry.of("y=1.5*x+12{-8<=x<=-7}",         "#A31F34"),
                EquationEntry.of("x=-7{-1.5<=y<=1.5}",            "#A31F34"),
                // --- I ---
                EquationEntry.of("x=0{-1.5<=y<=1.5}",             "#C0392B"),
                EquationEntry.of("y=1.5{-0.5<=x<=0.5}",           "#C0392B"),
                EquationEntry.of("y=-1.5{-0.5<=x<=0.5}",          "#C0392B"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                "#E74C3C"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",             "#E74C3C")
        );
    }

    // =========================================================================
    // IIT — Deep Blue (IIT blue #003366)
    // =========================================================================
    private EquationPreset iit() {
        return new EquationPreset(
                "IIT", "IIT drawn with line-segment equations", 35.0,
                // --- I1 ---
                EquationEntry.of("x=-7{-1.5<=y<=1.5}",            "#003366"),
                EquationEntry.of("y=1.5{-7.5<=x<=-6.5}",          "#003366"),
                EquationEntry.of("y=-1.5{-7.5<=x<=-6.5}",         "#003366"),
                // --- I2 ---
                EquationEntry.of("x=0{-1.5<=y<=1.5}",             "#1A5276"),
                EquationEntry.of("y=1.5{-0.5<=x<=0.5}",           "#1A5276"),
                EquationEntry.of("y=-1.5{-0.5<=x<=0.5}",          "#1A5276"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                "#2E86C1"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",             "#2E86C1")
        );
    }

    // =========================================================================
    // BUET — Engineering Blue-Green (#00695C)
    // =========================================================================
    private EquationPreset buet() {
        return new EquationPreset(
                "BUET", "BUET drawn with equations", 28.0,
                // --- B ---
                EquationEntry.of("x=-11{-1.5<=y<=1.5}",                          "#00695C"),
                EquationEntry.of("(x+11)^2+(y-0.75)^2=0.5625{x>=-11,y>=0}",     "#00695C"),
                EquationEntry.of("(x+11)^2+(y+0.75)^2=0.5625{x>=-11,y<=0}",     "#00695C"),
                EquationEntry.of("y=0{-11<=x<=-10.25}",                           "#00695C"),
                // --- U ---
                EquationEntry.of("x=-5{0<=y<=1.5}",                               "#00897B"),
                EquationEntry.of("x=-3{0<=y<=1.5}",                               "#00897B"),
                EquationEntry.of("(x+4)^2+y^2=1{y<=0}",                           "#00897B"),
                // --- E ---
                EquationEntry.of("x=1{-1.5<=y<=1.5}",                             "#26A69A"),
                EquationEntry.of("y=1.5{1<=x<=3}",                                "#26A69A"),
                EquationEntry.of("y=0{1<=x<=2.5}",                                "#26A69A"),
                EquationEntry.of("y=-1.5{1<=x<=3}",                               "#26A69A"),
                // --- T ---
                EquationEntry.of("y=1.5{5<=x<=9}",                                "#4DB6AC"),
                EquationEntry.of("x=7{-1.5<=y<=1.5}",                             "#4DB6AC")
        );
    }

    // =========================================================================
    // DU — Dhaka University Green (#006400) and Gold (#DAA520)
    // =========================================================================
    private EquationPreset du() {
        return new EquationPreset(
                "DU", "DU (Dhaka University) drawn with equations", 50.0,
                // --- D ---
                EquationEntry.of("x=-3{-1.5<=y<=1.5}",            "#006400"),  // bar (green)
                EquationEntry.of("(x+3)^2+y^2=2.25{x>=-3}",       "#006400"),  // arc (green)
                // --- U ---
                EquationEntry.of("x=3{0<=y<=1.5}",                "#DAA520"),  // left bar (gold)
                EquationEntry.of("x=5{0<=y<=1.5}",                "#DAA520"),  // right bar (gold)
                EquationEntry.of("(x-4)^2+y^2=1{y<=0}",           "#DAA520")   // bottom arc (gold)
        );
    }
}
