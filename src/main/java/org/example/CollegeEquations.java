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
                ndc(), hcc(), vnc(), DCC(), DC()
        );
    }

    // =========================================================================
    // NDC — Navy Blue (institutional color)
    // =========================================================================
    private EquationPreset ndc() {
        return new EquationPreset(
                "NDC \uD83D\uDC51", "NDC college sign drawn with equations", 35.0,
                // --- N ---
                EquationEntry.of("y = 0 {-3 <= x <= 3}",            "#fca90f"),
                EquationEntry.of("y = 0.5 {-3 <= x <= 3}",      "#fca90f"),
                EquationEntry.of("3>=x >= -3 {0 <= y <= 0.5}",            "#fca90f"),
                // --- D ---
                EquationEntry.of("x = 3 {0 <= y <= 0.5}",            "#fca90f"),
                EquationEntry.of("y = -7*x - 20.5 {-3.5 <= x <= -3}",       "#fca90f"),
                // --- C ---
                EquationEntry.of("y = 7*x - 20.5 {3 <= x <= 3.5}",        "#fca90f"),
                EquationEntry.of(".5<=y <= -x + 0.5 {-3.5 <= x <= -1.5,y >= -7*x - 20.5}",        "#fca90f"),
                EquationEntry.of(".5<=y <= x + 0.5 {1.5 <= x <= 3.5, y >= 7*x - 20.5 }",        "#fca90f"),
                EquationEntry.of("0.5<=y <= 2*x + 5 {-1.5 <= x <= 0}",        "#fca90f"),
                EquationEntry.of("0.5<=y <= -2*x + 5 {0 <= x <= 1.5}",        "#fca90f"),
                EquationEntry.of("x^2 + (y - 5.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("(x + 3.5)^2 + (y - 4.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("(x - 3.5)^2 + (y - 4.3)^2 <= 0.1",        "#fca90f"),
                EquationEntry.of("x^2 + (y - 0.25)^2 <= 0.04",        "#54bf41"),
                EquationEntry.of("(x - 1.5)^2 + (y - 0.25)^2 <= 0.04",        "#0000ff"),
                EquationEntry.of("(x + 1.5)^2 + (y - 0.25)^2 <= 0.04",        "#0000ff"),
                EquationEntry.of("x^2/.25+(y-2)^2/.5 <= 1",        "#ff0000")
        );
    }

    // =========================================================================
    // HCC — Maroon (classic college maroon)
    // =========================================================================
    private EquationPreset hcc() {
        return new EquationPreset(
                "HCC \uD83D\uDC78", "HCC college sign drawn with equations", 55.0,
                // --- H ---
                EquationEntry.of("x^2/(5^2)+(y+1.2)^2/(2^2)>= 1{0<=y,x^2/(5^2)+(y+1.8)^2/(3^2)<= 1}","#fc9403"),
                EquationEntry.of("x^2/(5^2)+(y+1.8)^2/(3^2)= 1{0<=y}","#fc9403"),
                EquationEntry.of("(y-2)^2 = 4.9x+4{1.2<=y<=4}","#fc9403"),
                EquationEntry.of("-(y-2)^2 = 4.9x-4{1.2<=y<=4}","#fc9403"),
                EquationEntry.of("(y-2)^2 <= 4.9x+6{1.15<=y<=4.45,(y-2)^2 >= 4.9x+4,x<0}","#fc9403"),
                EquationEntry.of("-(y-2)^2 >= 4.9x-6{1.15<=y<=4.45,-(y-2)^2 <= 4.9x-4,x>0}","#fc9403"),
                EquationEntry.of("(x-y+3)^2/2^2+(x+y-.1)^2/1.5^2 <= 1{.87<=y,x<=-1.14,(x-y+2.4)^2/2^2+(x+y-.1)^2/1.5^2 >= 1}","#fc9403"),
                EquationEntry.of("(x-y+2.4)^2/2^2+(x+y-.1)^2/1.5^2 = 1{x<=-1.18,y>=.89}","#fc9403"),
                EquationEntry.of("(-x-y+3)^2/2^2+(-x+y-.1)^2/1.5^2 <= 1{.87<=y,x>=1.14,(-x-y+2.4)^2/2^2+(-x+y-.1)^2/1.5^2 >= 1}","#fc9403"),
                EquationEntry.of("(-x-y+2.4)^2/2^2+(-x+y-.1)^2/1.5^2 = 1{x>=1.18,y>=.89}","#fc9403"),
                EquationEntry.of("(x+3)^2+(y-1)^2=.3{x^2/(5^2)+(y+1.8)^2/(3^2)>= 1,x<=-2.58}","#fc9403"),
                EquationEntry.of("(x+3)^2+(y-1)^2<=.8{x^2/(5^2)+(y+1.8)^2/(3^2)>= 1,x<=-2.63,(x+3)^2+(y-1)^2>=.3}","#fc9403"),
                EquationEntry.of("(-x+3)^2+(y-1)^2<=.8{x^2/(5^2)+(y+1.8)^2/(3^2)>= 1,x>=2.69,(-x+3)^2+(y-1)^2>=.3}","#fc9403"),
                EquationEntry.of("(-x+3)^2+(y-1)^2=.3{x^2/(5^2)+(y+1.8)^2/(3^2)>= 1,x>=2.58}","#fc9403"),
                EquationEntry.of(".5x^2 + (y - 1.9 - sqrt(abs(.5x)))^2 <= 0.3","#ed15b4"),
                EquationEntry.of(".3x^2 + (y - 1.9 - sqrt(abs(.5x)))^2 <= 0.3{.5x^2 + (y - 1.9 - sqrt(abs(.5x)))^2 >= 0.3}","#fc9403")
        );
    }

    // =========================================================================
    // VNC — Forest Green
    // =========================================================================
    private EquationPreset vnc() {
        return new EquationPreset(
                "VNC \uD83D\uDC0D", "VNC college sign drawn with equations", 20.0,
                EquationEntry.of("y=-x+16{12<y<13}",            "#27AE60"),
                EquationEntry.of("x=4{8<y<12}",            "#27AE60"),
                EquationEntry.of("y=-x+6{1<x<2}",             "#27AE60"),
                EquationEntry.of("y=x+14{11<y<13}",        "#27AE60"),
                EquationEntry.of("y=13{-1<x<3}",        "#27AE60"),
                EquationEntry.of("y=x+4{1<x<4}",        "#27AE60"),
                EquationEntry.of("x=-2{4<y<6}",        "#27AE60"),
                EquationEntry.of("y=x+8{-2<x<1}",        "#27AE60"),
                EquationEntry.of("y=10{-2<x<1}",        "#27AE60"),
                EquationEntry.of("y=-x+8{10<y<11}",        "#27AE60"),
                EquationEntry.of("x=1{9<y<10}",        "#27AE60"),
                EquationEntry.of("(x-1)^2/.75+(y-11.5)^2/.25<=1",        "#f74402"),
                EquationEntry.of("(x-1)^2/.03+(y-11.5)^2/.25<=1",        "#000000"),
                EquationEntry.of("x^2/33 + (y-3.8)^2/3.25 = 1 {y <= 5.5}","#27AE60"),
                EquationEntry.of("x^2/50 + (y-1.5)^2/5 = 1 {y <= 3}",        "#27AE60"),
                EquationEntry.of("x^2/68 + (y+1)^2/5= 1 {y <= .5}",        "#27AE60"),
                EquationEntry.of("y=11{-4<x<-3}",        "#000000"),
                EquationEntry.of("y=-x+7{-4.5<x<-4}",        "#000000"),
                EquationEntry.of("y=x+15{-4.5<x<-4}",        "#000000")
        );
    }

    private EquationPreset DCC() {
        return new EquationPreset(
                "DCC \uD83D\uDC14", "DCC drawn with line-segment equations", 55.0,
                EquationEntry.of("x^2 = -4*0.5(y-2.4){y>=.9}",            "#000000"),
                EquationEntry.of("x^2/4+y^2/3=1{y<=.9}",        "#000000"),
                EquationEntry.of("y+4.7 <= -8(x+.4)^2 + 8{-.79<=x<=-.13, x^2 >= -4*0.5(y-2.4)}","#ff0000"),
                EquationEntry.of("y+6.9 <= -7.33*(x-0.1)^2 + 10{-.13<=x<=.4, x^2 >= -4*0.5(y-2.4)} ","#ff0000"),
                EquationEntry.of("y+6.4 <= -11.67*(x-.6)^2 + 9.27 {.4<=x<=.87,x^2 >= -4*0.5(y-2.4)}","#ff0000"),
                EquationEntry.of("y <= .5 - x^2{y>=.16}",           "#ff8d03"),
                EquationEntry.of("y >= 2abs(x) - 1 {y<=.16,}",          "#ff8d03"),
                EquationEntry.of("(x-1)^2+(y-1)^2 <= .05",                "#000000"),
                EquationEntry.of("(x+1)^2+(y-1)^2 <= .05",                "#000000"),
                EquationEntry.of("y = .5 - x^2{y>=.16}",             "#000000"),
                EquationEntry.of("y = 2abs(x) - 1 {y<=.16,}",             "#000000")
        );
    }


    private EquationPreset DC() {
        return new EquationPreset(
                "DC \uD83D\uDC80", "DC drawn with line-segment equations", 65.0,
                // --- I1 ---
                EquationEntry.of("((x+y)/sqrt(2))^2 <= (0.2 + 0.4/(1 + exp(-1.5*(x-y)/sqrt(2))))^2 {-7 <= (x-y)/sqrt(2) <= 5}","#d67302"),
                EquationEntry.of("(((x-y)/sqrt(2)-5)/0.4)^2 + (((x+y)/sqrt(2))/0.6)^2 <= 1 {(x-y)/sqrt(2) >= 5}","#d67302"),
                EquationEntry.of("(((x-y)/sqrt(2)+7.2)/0.2)^2 + (((x+y)/sqrt(2))/0.35)^2 <= 1","#3b3631")
        );
    }

}
