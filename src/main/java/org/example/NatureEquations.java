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
                Tulip(),
                Volcano(),
                Drawing(),
                sunflower(),
                kawaiiCloud()
        );
    }

    private EquationPreset Tulip() {
        return new EquationPreset(
                "Tulip \uD83C\uDF37", "tulip curve", 20.0,
                EquationEntry.of("y=6/25(x-8)^2+7{3<= x<=13}", "#fc0b03"),
                EquationEntry.of("y<=(-3/4)(x-8)^2+15{6<= x<=10,y>=6/25(x-8)^2+7}", "#fc0b03"),
                EquationEntry.of("y<=(-1/9)(x-3)^2+13{3<=x<=6,y>=6/25(x-8)^2+7}", "#fc0b03"),
                EquationEntry.of("y<=(-1/9)(x-13)^2+13{10<=x<=13,y>=6/25(x-8)^2+7}", "#fc0b03"),
                EquationEntry.of("y<=(-1/4)(x-11)^2+15{9.098<=x<=11,y>=(-3/4)(x-8)^2+15,y>=(-1/9)(x-13)^2+13}", "#fc0b03"),
                EquationEntry.of("y<=(-3)(x-11)^2+15{11<=x<=11.846,y>=(-1/9)(x-13)^2+13}", "#fc0b03"),
                EquationEntry.of("y<=(-3)(x-5)^2+15{4.154<=x<=5,y>=(-3/4)(x-8)^2+15,y>=(-1/9)(x-3)^2+13}", "#fc0b03"),
                EquationEntry.of("y<=(-1/4)(x-5)^2+15{5<=x<=6.902,y>=(-3/4)(x-8)^2+15,y>=(-1/9)(x-3)^2+13}", "#fc0b03"),
                EquationEntry.of("x=8{0<= y<=7}", "#03fc14"),
                EquationEntry.of("y<=(-7/36)(x-2)^2+7{2<=x<=8,0<=y>=(-7/9)(x-2)^2+7}", "#03fc14"),
                EquationEntry.of("y=(-7/9)(x-2)^2+7{2<=x<=5}", "#03fc14"),
                EquationEntry.of("y<=(-5/16)(x-12)^2+5{8<=x<=12,0<=y>=(-5/4)(x-12)^2+5}", "#03fc14"),
                EquationEntry.of("y=(-5/4)(x-12)^2+5{10<=x<=12}", "#03fc14")
        );
    }


    private EquationPreset Volcano() {
        return new EquationPreset(
                "Volcano \uD83C\uDF0B", "Volcano", 13.0,
                EquationEntry.of("y=.2*(x-24)^2+2{16<x<24}", "#000000"),
                EquationEntry.of("y=.2*(x+2)²+2{-2<x<6}", "#000000"),
                EquationEntry.of("y=-.4*√(169-(x-11)²)+.5*sin(x)+2.5", "#000000"),
                EquationEntry.of("y=-.2*√(25-(x-11)²)+15", "#ff5500"),
                EquationEntry.of("-.7*(x-15)²+19.5≤y≤-(x-15)²+22{12.2<x<17.8}", "#ff5500"),
                EquationEntry.of("-.45*(x-6.3)²+19.5≤y≤-.7*(x-6)²+24{1.16<x<9.76}", "#ff5500"),
                EquationEntry.of("-(x-16)²+30≤y≤.1*√(25-(x-11)²)+30{12<x<16}", "#ff5500"),
                EquationEntry.of("-(x-6)²+30≤y≤.1*√(25-(x-11)²)+30{6<x<10}", "#ff5500"),
                EquationEntry.of("-.9*√(169-(x-11.9)²)+4*sin(2*x)+21≤y≤-.2*√(25-(x-11)²)+15{4.73<x<16.33}", "#ff5500"),
                EquationEntry.of("y=.2*√(25-(x-11)²)+15{x<9.12}", "#000000"),
                EquationEntry.of("y=.2*√(25-(x-11)²)+15{x>12.74}", "#000000"),
                EquationEntry.of("-.2*√(25-(x-11)²)+15≤y≤.1*√(25-(x-11)²)+30{10<x<12}", "#ff5500"),
                EquationEntry.of("-.9*√(169-(x-11.9)²)+4*sin(2*x)+21≤y≤.2*(x-24)²+2{16<x<16.33}", "#ff5500"),
                EquationEntry.of("-.9*√(169-(x-11.9)²)+4*sin(2*x)+21≤y≤.2*(x+2)²+2{4.73<x<6}", "#ff5500")
        );
    }

    private EquationPreset Drawing() {
        return new EquationPreset(
                "Drawing Scenary \uD83C\uDFDE\uFE0F", "Logarithmic spiral r = e^(0.2θ)", 5.0,
                EquationEntry.of("40>y>-40{40>x>-40}", "#ff0000"),
                EquationEntry.of("40>y>-40{40>x>-40}", "#fc8403"),
                EquationEntry.of("y<-20*cos(0.09*x+.1)-6{-40<x<40,y>-40}", "#ff0000"),
                EquationEntry.of("y<-1.9*cos(0.1*x+5.1)-6{-40<x<40,y>-40}", "#178527"),
                EquationEntry.of("y<-1.9*cos(0.1*x+5.1)-6{-40<x<40,y>-40}", "#15a129"),
                EquationEntry.of("(x+17)²+(y-25)²<36", "#ff0000"),
                EquationEntry.of("(x+17)²+(y-25)²<36", "#fc8403"),
                EquationEntry.of("x>3*y+10{-40<x<40,-40<y<-1.9*cos(0.1*x+5.1)-6}", "#0acad1"),
                EquationEntry.of("x<2.5*y+40{x>-40,-40<y<-1.9*cos(0.1*x+5.1)-6}", "#0000ff"),
                EquationEntry.of("y<-1.9*cos(0.1*x+5.1)-6{-40<x<40,y>-40}", "#115c1c")
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

    private EquationPreset kawaiiCloud() {
        return new EquationPreset(
                "Kawaii Cloud ☁", "Cute kawaii smiling cloud with blue tummy", 45.0,

                EquationEntry.of("x² +(y-1.5)² = 9.6{y≥2.97}", "#FF69B4"),
                EquationEntry.of("(x+3.5)² + (y- 0.5)² = 6.7{0≤y,x≤-2.73}", "#FF69B4"),
                EquationEntry.of("(x+3.5)² + (y- 0.5)² = 6.7{0≥y,x≤-2.94}", "#FF69B4"),
                EquationEntry.of("(x-3.5)² + (y- 0.5)² =6.7{0≤y,x≥2.73}", "#FF69B4"),
                EquationEntry.of("(x-3.5)² + (y- 0.5)² = 6.7{0≥y,x≥2.94}", "#FF69B4"),
                EquationEntry.of("(x+1.5)² + (y+0.5)² = 4.4{-2.94≤x≤0,y≤0}", "#FF69B4"),
                EquationEntry.of("(x-1.5)² + (y+0.5)² = 4.4{y≤-1.97}", "#FF69B4")

        );
    }
}
