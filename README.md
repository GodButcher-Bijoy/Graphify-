# 📈 Graphify

<div align="center">

![Graphify Banner](https://img.shields.io/badge/Graphify-Mathematical%20Grapher-9D00FF?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xOSAzSDVjLTEuMSAwLTIgLjktMiAydjE0YzAgMS4xLjkgMiAyIDJoMTRjMS4xIDAgMi0uOSAyLTJWNWMwLTEuMS0uOS0yLTItMnptLTcgM2MuNTUgMCAxIC40NSAxIDF2Mmgyd' />

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-0078D4?style=flat-square)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey?style=flat-square)]()

**A stunning, feature-rich mathematical graphing application built with JavaFX.**
Plot equations, explore curves, and visualize mathematics like never before.

[Features](#-features) · [Installation](#-installation) · [Usage](#-usage) · [Library](#-equation-library) · [Troubleshooting](#-troubleshooting)

</div>

---

## ✨ Features

### 🖊️ Equation Engine
- **Explicit equations** — `y = sin(x)`, `y = x²`
- **Implicit equations** — `x² + y² = 25`, `x²/9 + y²/4 = 1`
- **Inverse equations** — `x = f(y)`
- **Parametric curves** — `(cos(t), sin(t))`
- **Polar equations** — `r = 1 + cos(θ)`
- **Inequality shading** — `y ≤ x²`, `x² + y² ≤ 9`
- **Boundary conditions** — `y = x² { -3 ≤ x ≤ 3 }`

### 🎛️ Interactive Controls
- **Smooth pan & zoom** — drag to pan, scroll to zoom
- **Variable sliders** — type `ax + b` and instantly get sliders for `a` and `b`
- **Animated sliders** — press ▶ to animate any variable
- **Re-center button** — instantly snap back to origin
- **Eye toggle** — hide/show individual equations
- **Color picker** — assign any color to each graph line

### 🔢 Math Display
- **Live pretty-printing** — `x^2` auto-renders as `x²` as you type
- **Full on-screen keypad** — with trig, log, and special functions
- **Intersection points** — auto-detected when you focus an equation
- **Extrema detection** — local maxima and minima are highlighted
- **Pinned points** — click any intersection to permanently label it

### 🎨 Visual Modes
- **Standard Cartesian** mode with smart adaptive grid
- **Polar** mode with concentric circles and degree labels
- **20-color palette** per graph with full custom color support

### 📚 Equation Library
Over 20 hand-crafted presets across 5 categories, including Batman, Pikachu, Mickey Mouse, Captain America Shield, and much more. See [Equation Library](#-equation-library) for the full list.

---

## 🖥️ Screenshots

| Intro Screen | Main Graph View |
|:---:|:---:|
| *3D animated intro with rolling cube* | *Dark sidebar + white graph canvas* |

| Equation Library | Polar Mode |
|:---:|:---:|
| *Browse preset art and shapes* | *Concentric grid with radial labels* |

---

## 📦 Installation

### Prerequisites

Before installing Graphify, make sure you have the following:

| Requirement | Version | Download |
|---|---|---|
| Java JDK | 17 or higher | [adoptium.net](https://adoptium.net/) |
| Apache Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Git *(optional)* | Any | [git-scm.com](https://git-scm.com/) |

> 💡 **Tip:** On macOS with Homebrew, run `brew install openjdk@17 maven`

---

### 🪟 Windows

**Step 1 — Install Java 17**
1. Go to [https://adoptium.net](https://adoptium.net/)
2. Download **Temurin 17 (LTS)** for Windows x64
3. Run the installer and check **"Set JAVA_HOME"** during setup
4. Verify: open Command Prompt and run:
   ```
   java -version
   ```
   You should see `openjdk version "17.x.x"`

**Step 2 — Install Maven**
1. Download the binary zip from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Maven`
3. Add `C:\Program Files\Maven\bin` to your `PATH` environment variable
4. Verify:
   ```
   mvn -version
   ```

**Step 3 — Get the project**
```bat
:: Option A — unzip the downloaded ZIP into a folder, then:
cd path\to\Graphify

:: Option B — if using git:
git clone https://github.com/yourusername/Graphify.git
cd Graphify
```

**Step 4 — Run**
```bat
mvn javafx:run
```

---

### 🍎 macOS

**Step 1 — Install Java 17**

*Option A — Homebrew (recommended):*
```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

*Option B — Manual installer:*
1. Download Temurin 17 from [https://adoptium.net](https://adoptium.net/)
2. Open the `.pkg` file and follow the installer

Verify:
```bash
java -version
```

**Step 2 — Install Maven**

*Option A — Homebrew:*
```bash
brew install maven
```

*Option B — Manual:*
```bash
curl -O https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar xzvf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
echo 'export PATH="/opt/maven/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Verify:
```bash
mvn -version
```

**Step 3 — Get the project**
```bash
# Unzip the downloaded ZIP:
unzip Graphify.zip
cd Graphify

# OR clone with git:
git clone https://github.com/yourusername/Graphify.git
cd Graphify
```

**Step 4 — Run**
```bash
mvn javafx:run
```

---

### 🐧 Linux (Ubuntu / Debian)

**Step 1 — Install Java 17**
```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
java -version
```

**Step 2 — Install Maven**
```bash
sudo apt install maven -y
mvn -version
```

**Step 3 — Get the project**
```bash
unzip Graphify.zip && cd Graphify
# OR
git clone https://github.com/yourusername/Graphify.git && cd Graphify
```

**Step 4 — Run**
```bash
mvn javafx:run
```

---

### ⚡ IntelliJ IDEA (All platforms)

1. Open IntelliJ IDEA
2. Click **File → Open** and select the `Graphify` folder
3. IntelliJ will detect the `pom.xml` and import the Maven project automatically
4. Wait for dependencies to download (first run only)
5. Open the **Maven** panel (right sidebar) → **Plugins → javafx → javafx:run**

   **OR** open `MainApp1.java` and click the green ▶ **Run** button

---

## 🚀 Usage

### Typing Equations

| What you type | What it means |
|---|---|
| `y = x^2 + 2*x - 1` | Standard explicit |
| `x^2 + y^2 = 25` | Implicit circle |
| `y <= sin(x)` | Inequality shading |
| `y = x^2 { -3 <= x <= 3 }` | Bounded curve |
| `(cos(t), sin(t))` | Parametric |
| `r = 1 + cos(t)` | Polar (use Polar mode) |
| `ax + by = c` | Then add sliders for a, b, c |

### Keyboard Shortcuts

| Key | Action |
|---|---|
| `Enter` | Add a new equation row below |
| `↑ / ↓` | Navigate between equation rows |
| `Scroll` | Zoom in / out on the graph |
| `Drag` | Pan the graph |

### Slider Variables

1. Type an equation using free letters, e.g. `y = a*sin(b*x) + c`
2. A prompt appears: **"add slider: a b c"**
3. Click each variable or **"all"** to create sliders
4. Drag sliders or press ▶ to animate
5. Click ✕ on a slider to remove it

---

## 📚 Equation Library

Access the library from the **📚 Library ▾** button in the sidebar or via the **Experience Curves** screen.

### 🎭 Super-Heroes
| Preset | Description |
|---|---|
| Batman 🦇 | The iconic bat-symbol |
| Captain America Shield ⍟ | Concentric ring shield |
| Pikachu 😊 | Face with ears and cheeks |

### 🎓 College
| Preset | Description |
|---|---|
| NDC 👑 | College sign with decorative elements |
| HCC 👸 | Ornate college emblem |
| VNC 🐍 | Letter art with symbols |
| DCC 🐔 | Stylized college design |
| DC 💀 | Abstract diagonal lettering |

### 🌿 Nature
| Preset | Description |
|---|---|
| Tulip 🌷 | Full tulip with stem and leaves |
| Volcano 🌋 | Erupting volcano with lava |
| Drawing Scenery 🏞️ | Landscape with sky and terrain |
| Sunflower 🌻 | Parametric petal pattern |
| Kawaii Cloud ☁ | Smiling cloud with rosy cheeks |

### 🐼 Cartoons *(Art category)*
| Preset | Description |
|---|---|
| Mickey Mouse 🐭 | Classic mouse face |
| Halloween Pumpkin 🎃 | Jack-o'-lantern with face |
| Among Us ඞ | Angel halo character |
| Yin Yang ☯ | Classic balance symbol |

### 💎 Cool Shapes
| Preset | Description |
|---|---|
| Star ⭐ | Parametric 5-pointed star |
| Checkerboard ♟ | sin inequality pattern |
| Sine Wave 〰 | Fourier harmonic wave |

---

## 🗂️ Project Structure

```
Graphify/
├── src/
│   └── main/
│       └── java/
│           └── org/example/
│               ├── MainApp1.java          ← App entry point
│               ├── AppState.java          ← Global state (scale, offset, vars)
│               ├── GraphRenderer.java     ← Canvas drawing + pan/zoom
│               ├── FunctionPlotter.java   ← All equation plotting logic
│               ├── AxesRenderer.java      ← Grid, axes, polar grid
│               ├── UIManager.java         ← Sidebar, sliders, keypad
│               ├── EquationHandler.java   ← Expression parsing (exp4j)
│               ├── MathRenderer.java      ← Pretty-print overlay
│               ├── IntroScene.java        ← 3D animated intro
│               ├── SelectionScene.java    ← Mode selection screen
│               ├── LibraryScene.java      ← Preset library browser
│               ├── EquationLibrary.java   ← Category registry
│               ├── EquationCategory.java  ← Abstract category base
│               ├── EquationPreset.java    ← Named preset with entries
│               ├── EquationEntry.java     ← Single equation + color
│               ├── IntersectionCalculator.java ← Sign-change bisection
│               ├── CartoonEquations.java  ← Super-heroes presets
│               ├── CollegeEquations.java  ← College sign presets
│               ├── NatureEquations.java   ← Nature curve presets
│               ├── ArtEquations.java      ← Art/cartoon presets
│               └── CoolShapesEquations.java ← Geometric presets
└── pom.xml                                ← Maven build config
```

---

## 🔧 Troubleshooting

### `java: error: release version 17 not supported`
Your Maven is using the wrong Java version.
```bash
# Check which Java Maven is using:
mvn -version

# Force Maven to use Java 17 (macOS/Linux):
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk       # Linux
```

### `Error: JavaFX runtime components are missing`
Do **not** run Graphify using `java -jar`. Always use:
```bash
mvn javafx:run
```

### App window is blank / black on macOS
This is a known JavaFX issue on Apple Silicon with some GPU drivers.
```bash
# Add this flag to your run command:
mvn javafx:run -Djavafx.verbose=true
```
Or in IntelliJ: **Run → Edit Configurations → VM Options** → add:
```
-Dprism.order=sw
```

### Slow graph rendering on first launch
Maven downloads all dependencies on the first run. This is normal and only happens once. Subsequent launches are fast.

### `BUILD FAILURE` — dependencies not found
```bash
# Force re-download all dependencies:
mvn dependency:resolve -U
mvn javafx:run
```

---

## 🛠️ Built With

| Technology | Purpose |
|---|---|
| [Java 17](https://openjdk.org/projects/jdk/17/) | Core language |
| [JavaFX 21](https://openjfx.io/) | UI framework, Canvas, 3D |
| [exp4j 0.4.8](https://www.objecthunter.net/exp4j/) | Math expression evaluator |
| [Apache Maven](https://maven.apache.org/) | Build & dependency management |

---

## 📝 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgements

- [exp4j](https://www.objecthunter.net/exp4j/) by Frank Asseg — powering all equation evaluation
- [Desmos](https://www.desmos.com/) — for inspiration on the graphing UX
- The JavaFX community for Canvas API documentation

---

<div align="center">

Made with ❤️ and mathematics

⭐ **If you find Graphify useful, give it a star!** ⭐

</div>
