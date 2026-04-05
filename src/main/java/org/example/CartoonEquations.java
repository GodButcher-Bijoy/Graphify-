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

    @Override public String getCategoryName() { return "Super-Heroes"; }
    @Override public String getCategoryEmoji() { return "🎭"; }
    @Override public Color[] getColors()       { return PALETTE; }

    @Override
    public List<EquationPreset> getPresets() {
        return Arrays.asList(
                batman(),
                captain_america_shield(),
                pikachu()
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
    // Pikachu (simplified face)
    // -----------------------------------------------------------------------
    private EquationPreset pikachu() {
        return new EquationPreset(
                "Pikachu 😊", "Pikachu face outline with ears and cheeks", 45.0,
                EquationEntry.of("x^2/12 + y^2/11 <= 1",                            "#FFD700"), // head (yellow)
                EquationEntry.of("(y-10)^2 = 18*x + 110 {2.12<=y<=6.88}",                  "#000000"), // left ear (black)
                EquationEntry.of("(y-1)^2 = -8*x - 10 {2.89<=y<=6.88}",                  "#000000"),
                EquationEntry.of("(x+3)^2+(y-5.1)^2/5 = 1{y<=5.66,x<=-3.5}",                        "#000000"),
                EquationEntry.of("-(y-7)^2= 4.5*x - 37 {2.03<=y<=4.92}",                     "#000000"), // right ear (black)
                EquationEntry.of("(y-2.2)^2 = 1.2*x - 1.3{2.96<=y<=4.91}",                   "#000000"),
                EquationEntry.of("(x-3)^2/5+(y-4.1)^2/3 = 1{x>=4.8,y<=4.42}",            "#000000"), // right eye
                EquationEntry.of("(x+1.5)^2 + (y-0.5)^2 <= 0.3",            "#000000"), // left eye
                EquationEntry.of("(x-1.5)^2 + (y-0.5)^2 <= 0.3",        "#000000"), // right cheek (red)
                EquationEntry.of("(x+1.3)^2 + (y-0.7)^2 <= 0.03",        "#FFFFFF"), // left cheek (red)
                EquationEntry.of("(x-1.7)^2 + (y-0.7)^2 <= 0.03",               "#FFFFFF"),
                EquationEntry.of("(x+2.5)^2 + (y+1.2)^2 <= 0.4",               "#ff0000"),
                EquationEntry.of("(x-2.5)^2 + (y+1.2)^2 <= 0.4",               "#ff0000"),
                EquationEntry.of("x^2/0.03 + (y+0.2)^2/0.02 = 1",               "#000000"),
                EquationEntry.of("y = 1.2*(x+0.5)^2 - 1.2 {-1 <= x <= 0}",               "#000000"),
                EquationEntry.of("y = 1.2*(x-0.5)^2 - 1.2 {0 <= x <= 1}",               "#000000"),
                EquationEntry.of("x^2/12 + y^2/11 = 1",                            "#000000")
        );
    }
}
