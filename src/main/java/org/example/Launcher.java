package org.example;

/**
 * Launcher — Plain main-class wrapper required for JavaFX fat JARs.
 *
 * WHY THIS EXISTS:
 * When the main class directly extends javafx.application.Application, the
 * JVM tries to load JavaFX bootstrap classes before the fat-JAR class-loader
 * is fully initialised. This causes a runtime crash:
 *   "Error: JavaFX runtime components are missing"
 *
 * The fix is to put the real launch call in a class that does NOT extend
 * Application. Maven Shade sets this class as the JAR entry-point; it then
 * calls MainApp1.main() normally, and JavaFX initialises without issues.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp1.main(args);
    }
}
