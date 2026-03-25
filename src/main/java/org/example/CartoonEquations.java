package org.example;

import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * CartoonEquations: Famous cartoon / pop-culture icons drawn with math.
 * Every equation carries its own individually chosen color.
 */
public class CartoonEquations extends EquationCategory {

    // Category-level palette kept for backward compatibility / display purposes
    private static final Color[] PALETTE = {
            Color.web("#FF2D55"), Color.web("#007AFF"), Color.web("#FFCC00"),
            Color.web("#34C759"), Color.web("#FF9500"), Color.web("#AF52DE"),
            Color.web("#FF3B30"), Color.web("#5AC8FA"),
    };

    @Override public String getCategoryName() { return "Cartoon"; }
    @Override public String getCategoryEmoji() { return "🎭"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                heart(),
                batman(),
                captain_america_shield(),
                ironManMask(),
                pikachu(),
                pacman()
        );
    }

    // -----------------------------------------------------------------------
    // Heart Curve
    // -----------------------------------------------------------------------
    private EquationPreset heart() {
        return new EquationPreset(
                "Heart ❤", "Classic mathematical heart curve", 60.0,
                EquationEntry.of("x^2+(y-cbrt(x^2))^2=1", "#FF2D55")   // vivid red
        );
    }

    // -----------------------------------------------------------------------
    // Batman Symbol
    // -----------------------------------------------------------------------
    private EquationPreset batman() {
        return new EquationPreset(
                "Batman 🦇", "Batman logo silhouette built from multiple curves", 40.0,
                EquationEntry.of("0<= y <= 3sqrt(1-(x/7)^2){3<=abs(x)<=7}}","#000000"), // body top arc
                EquationEntry.of("0>=y >= -3sqrt(1-(x/7)^2){4<=abs(x)<=7}","#000000"), // body bottom arc
                EquationEntry.of("0<=y<=2.25{-.5<=x<=.5}","#000000"), // right wing
                EquationEntry.of("0<=y<=3abs(x)+.75{.5<x<=.75}","#000000"), // left wing
                EquationEntry.of("0<=y<=3abs(x)+.75{-.75<=x<-.5}","#000000"), // right ear tip
                EquationEntry.of("0<=y<=9-8abs(x){.75<=x<=1}","#000000"), // left ear tip
                EquationEntry.of("0<=y<=9-8abs(x){-1<=x<=-.75}","#000000"), // left ear tip
                EquationEntry.of("0<=y<=6sqrt(10)/7 + (1.5-.5abs(x))-6sqrt(10)/14sqrt(4-(abs(x)-1)^2){1<abs(x)<=3}","#000000"), // left ear tip
                EquationEntry.of("0>=y>=abs(x)/2-((3sqrt(33)-7)x^2)/114-3+sqrt(1-(abs(abs(x)-2)-1)^2){-4<=x<=4}","#000000")  // chin notch
        );
    }

    // -----------------------------------------------------------------------
    // Superman Shield
    // -----------------------------------------------------------------------
    private EquationPreset captain_america_shield() {
        return new EquationPreset(
                "Captain America Shield ⍟", "The iconic captain america-shield", 40.0,
                EquationEntry.of("x^2+y^2 <= 100{64<=x^2+y^2<=102}","#B11E23"),
                EquationEntry.of("x^2+y^2 <= 16","#1b5de0"),
                EquationEntry.of("x^2+y^2 = 64","#c4bbb9"),
                EquationEntry.of("x^2+y^2 <= 36{16<=x^2+y^2 <= 37}","#B11E23"),
                EquationEntry.of("y=1.24{-3.8<=x<=3.8}","#FFFFFF"),
                EquationEntry.of("y = -3.08abs(x)+4{0<=abs(x)<=2.35}","#FFFFFF"),
                EquationEntry.of("y=.73x-1.53{-2.35<=x<=3.8}","#FFFFFF"),
                EquationEntry.of("y=-.73x-1.53{-3.8<=x<=2.35}","#FFFFFF")
        );
    }

    // -----------------------------------------------------------------------
    // Iron Man Mask
    // -----------------------------------------------------------------------
    private EquationPreset ironManMask() {
        return new EquationPreset(
                "Iron Man Mask 🤖", "Iron Man faceplate outline", 45.0,
                EquationEntry.of("y = -x^2/9 + 6 {-3 <= x <= 3}",                "#CC0000"), // outer head (red)
                EquationEntry.of("y = -4abs(x) + 17 {3 < abs(x) <= 4}",    "#FFD700"), // right eye (gold)
                EquationEntry.of("y = 2.5abs(x) - 9 {2 < abs(x) <= 4}",  "#CC0000"), // left eye (gold)
                EquationEntry.of("y = -4 {-2 <= x <= 2}",                "#CC0000"), // nose bridge
                EquationEntry.of("y = -0.3abs(x) + 3.5 {1 <= abs(x) <= 3}",              "#FFD700"), // right cheek line
                EquationEntry.of("y = -0.3abs(x) + 2.5 {1 <= abs(x) <= 3}",              "#FFD700"), // left cheek line
                EquationEntry.of("x = 1 {2.2 <= y <= 3.2}",                 "#FFD700"), // chin horizontal
                EquationEntry.of("x = -1 {2.2 <= y <= 3.2}",                 "#FFD700"), // right jaw angle
                EquationEntry.of("x = 3 {1.6 <= y <= 2.6}",              "#FFD700"),  // left jaw angle
                EquationEntry.of("x = -3 {1.6 <= y <= 2.6}",              "#FFD700"),  // left jaw angle
                EquationEntry.of("y = -2 {-1.5 <= x <= 1.5}",              "#FFD700")  // left jaw angle
        );
    }

    // -----------------------------------------------------------------------
    // Pikachu (simplified face)
    // -----------------------------------------------------------------------
    private EquationPreset pikachu() {
        return new EquationPreset(
                "Pikachu 😊", "Pikachu face outline with ears and cheeks", 45.0,
                EquationEntry.of("x^2+y^2=9",                            "#FFD700"), // head (yellow)
                EquationEntry.of("y=-2*x-6{-4<=x<=-2}",                  "#2D2D2D"), // left ear (black)
                EquationEntry.of("y=2*x+10{-4<=x<=-2}",                  "#2D2D2D"),
                EquationEntry.of("y=2{-2<=x<=-2}",                        "#2D2D2D"),
                EquationEntry.of("y=2*x-6{2<=x<=4}",                     "#2D2D2D"), // right ear (black)
                EquationEntry.of("y=-2*x+10{2<=x<=4}",                   "#2D2D2D"),
                EquationEntry.of("(x-1.2)^2+(y-0.8)^2=0.15",            "#1A1A1A"), // right eye
                EquationEntry.of("(x+1.2)^2+(y-0.8)^2=0.15",            "#1A1A1A"), // left eye
                EquationEntry.of("(x-2)^2/0.8+(y+0.5)^2/0.25=1",        "#FF4444"), // right cheek (red)
                EquationEntry.of("(x+2)^2/0.8+(y+0.5)^2/0.25=1",        "#FF4444"), // left cheek (red)
                EquationEntry.of("y=-0.3*x^2-1{-1<=x<=1}",               "#CC8800")  // mouth
        );
    }

    // -----------------------------------------------------------------------
    // Pac-Man
    // -----------------------------------------------------------------------
    private EquationPreset pacman() {
        return new EquationPreset(
                "Pac-Man 👾", "Classic Pac-Man munching shape", 50.0,
                EquationEntry.of("x^2+y^2=9{y>=x,y>=-x}",       "#FFCC00"), // body (yellow)
                EquationEntry.of("y=x{0<=x<=3}",                  "#FFCC00"), // mouth top
                EquationEntry.of("y=-x{0<=x<=3}",                 "#FFCC00"), // mouth bottom
                EquationEntry.of("(x-0.5)^2+(y-2)^2=0.1",        "#1A1A1A"), // eye (black)
                EquationEntry.of("(x-7)^2+y^2=4{y>=-1}",         "#00BFFF"), // ghost body (blue)
                EquationEntry.of("y=-1{5<=x<=9}",                 "#00BFFF"), // ghost bottom
                EquationEntry.of("(x-5)^2+(y+2)^2=1{y<=-1}",     "#00BFFF"), // ghost foot L
                EquationEntry.of("(x-7)^2+(y+2)^2=1{y<=-1}",     "#00BFFF"), // ghost foot M
                EquationEntry.of("(x-9)^2+(y+2)^2=1{y<=-1}",     "#00BFFF")  // ghost foot R
        );
    }
}
