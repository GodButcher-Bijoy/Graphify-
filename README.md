# 📈 Graphify

<div align="center">

![Graphify Banner](https://img.shields.io/badge/Graphify-Mathematical%20Grapher-9D00FF?style=for-the-badge)

[![Platform macOS](https://img.shields.io/badge/macOS-000000?style=flat-square&logo=apple&logoColor=white)](https://www.apple.com/macos/)
[![Platform Windows](https://img.shields.io/badge/Windows-0078D4?style=flat-square&logo=windows&logoColor=white)](https://www.microsoft.com/windows)
[![JavaFX](https://img.shields.io/badge/Powered%20by-JavaFX%2021-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjfx.io/)

**A stunning, feature-rich mathematical graphing application.**  
Plot equations, explore curves, and visualize mathematics like never before.

[Features](#-features) · [Installation](#-installation) · [Usage](#-usage) · [Library](#-equation-library) · [Building from Source](#-building-from-source) · [Troubleshooting](#-troubleshooting)

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
- **Implicit multiplication** — write `2x` or `2(x+1)` without the `*`

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
Over 20 hand-crafted presets across 5 categories — Batman, Pikachu, Mickey Mouse, Captain America Shield, and much more.

---

## 📦 Installation

> **No Java installation required.**  
> The app is fully self-contained — just download, extract, and run.

---

### 📥 Step 1 — Download

1. Go to the [**Releases**](https://github.com/GodButcher-Bijoy/Graphify-/releases) page
2. Download **`Graphify-Installers.zip`**
3. **Extract the ZIP** — you will find two files inside:

```
Graphify-Installers/
├── Graphify.dmg      ← for macOS
└── Graphify.exe      ← for Windows
```

Use the file that matches your operating system.

---

### 🍎 macOS — Install from `.dmg`

**Step 2 — Open the disk image**

Double-click `Graphify.dmg`. A window opens showing the Graphify app icon.

**Step 3 — Drag to Applications**

Drag the **Graphify** icon into the **Applications** folder shortcut shown in the window:

```
  ┌─────────────────────────────────────┐
  │                                     │
  │   🟣 Graphify   →→→   Applications  │
  │                                     │
  └─────────────────────────────────────┘
```

**Step 4 — Eject the disk image**

Right-click the mounted drive on your Desktop and select **Eject**.

**Step 5 — Launch Graphify**

Open **Launchpad** or your **Applications** folder and click **Graphify**.

---

> ⚠️ **First-launch security prompt on macOS**
>
> macOS may show: *"Graphify cannot be opened because it is from an unidentified developer."*
>
> **Fix — allow it in one of two ways:**
>
> **Option A (easier):**
> Right-click `Graphify.app` → click **Open** → click **Open** in the dialog.
>
> **Option B:**
> 1. Open **System Settings → Privacy & Security**
> 2. Scroll down to the Security section
> 3. Click **"Open Anyway"** next to the Graphify message
> 4. Confirm with **"Open"**
>
> You only need to do this once.

---

### 🪟 Windows — Install from `.exe`

**Step 2 — Run the installer**

Double-click `Graphify.exe`.

**Step 3 — Handle the SmartScreen prompt** *(if it appears)*

If you see *"Windows protected your PC"*:
1. Click **"More info"**
2. Click **"Run anyway"**

**Step 4 — Follow the setup wizard**

| Step | What to do |
|---|---|
| Welcome screen | Click **Next** |
| Choose install location | Keep default or pick your own, then click **Next** |
| Install | Click **Install** |
| Finish | Click **Finish** — Graphify launches automatically |

**Step 5 — Launch Graphify anytime**

Use the **Desktop shortcut** or find **Graphify** in your **Start Menu**.

---

## 🚀 Usage

### Typing Equations

| What you type | What it means |
|---|---|
| `y = x^2 + 2x - 1` | Standard explicit (implicit `*` supported) |
| `x^2 + y^2 = 25` | Implicit circle |
| `y <= sin(x)` | Inequality shading |
| `y = x^2 { -3 <= x <= 3 }` | Bounded curve |
| `(cos(t), sin(t))` | Parametric |
| `r = 1 + cos(t)` | Polar *(use Polar mode)* |
| `ax + by = c` | Free variables → add sliders |

> 💡 **Implicit multiplication is supported.** You can write `2x`, `3(x+1)`, or `(x+1)(x-1)` without typing `*`. The engine automatically inserts the multiplication sign.

### Keyboard Shortcuts

| Key | Action |
|---|---|
| `Enter` | Add a new equation row below |
| `↑ / ↓` | Navigate between equation rows |
| `Scroll` | Zoom in / out on the graph |
| `Drag` | Pan the graph |

### Using Slider Variables

1. Type an equation with free letters — e.g. `y = a * sin(b * x) + c`
2. A prompt appears below: **"add slider: a  b  c"**
3. Click each variable or **"all"** to create all sliders at once
4. Drag the sliders to change values, or press **▶** to animate
5. Click **✕** on a slider to remove it

---

## 📚 Equation Library

Access the library via the **📚 Library ▾** button in the sidebar, or from the **Experience Curves** screen on the home page.

### 🎭 Super-Heroes
| Preset                   | Description               |
|--------------------------|---------------------------|
| Batman 🦇                | The iconic bat-symbol     |
| Captain America Shield ⍟ | Marvel Superhero's Weapon |
| Pikachu 😊               | Famous Pokémon character  |

### 🎓 College
| Preset  | Description                            |
|---------|----------------------------------------|
| NDC 👑  | College crest with decorative elements |
| HCC 👸  | Queens crown college emblem            |
| VNC 🐍  | Snake pattern college sign             |
| DCC 🐔  | Chicken drawing college sign           |
| DC 💀   | Baseball bat as weapon                 |

### 🌿 Nature
| Preset                  | Description                     |
|-------------------------|---------------------------------|
| Tulip 🌷                | Full tulip with stem and leaves |
| Volcano 🌋              | Erupting volcano with lava flow |
| Drawing Scenery 🏞️      | Landscape with sky and terrain  |
| Sunflower 🌻            | Parametric petal pattern        |
| Kawaii Cloud ☁          | Smiling cloud with rosy cheeks  |

### 🐼 Cartoons
| Preset                  | Description                       |
|-------------------------|-----------------------------------|
| Mickey Mouse 🐭         | Classic Mickey Mouse face         |
| Halloween Pumpkin 🎃    | Jack-o'-lantern with carved face  |
| Among Us ඞ              | Angel halo crewmate character     |
| Yin Yang ☯              | Classic balance symbol            |

### 💎 Cool Shapes
| Preset           | Description                |
|------------------|----------------------------|
| Star ⭐           | Parametric 5-pointed star  |
| Checkerboard ♟   | sin inequality pattern     |
| Sine Wave 〰      | Fourier harmonic wave      |

---

## 🔨 Building from Source

If you want to build Graphify yourself from the source code in IntelliJ.

### Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 17+ | Must be on your PATH |
| Apache Maven | 3.8+ | Bundled with IntelliJ |
| WiX Toolset | 3.x only | Windows .exe only — [download here](https://wixtoolset.org/releases/) |

> ⚠️ WiX **3.x** is required. Version 4.x is not yet supported by jpackage. Restart your PC after installing WiX so the PATH updates.

### Step 1 — Clone and open

```bash
git clone https://github.com/GodButcher-Bijoy/Graphify-.git
```

Open the folder as a Maven project in IntelliJ. Right-click `pom.xml` → **Add as Maven Project** if not auto-detected.

### Step 2 — Add the Launcher wrapper

Create `src/main/java/org/example/Launcher.java` with this content:

```java
package org.example;

public class Launcher {
    public static void main(String[] args) {
        MainApp1.main(args);
    }
}
```

> This class is required. JavaFX fat JARs crash at startup if the entry point directly extends `Application` — this wrapper prevents that.

### Step 3 — Build the fat JAR

In IntelliJ's **Maven** panel (right side) run `clean` then `package`, or use the Terminal:

```bash
mvn clean package
```

This produces `target/Graphify-1.0-fat.jar`. Test it first:

```bash
java -jar target/Graphify-1.0-fat.jar
```

### Step 4 — Package as a native installer

**Windows `.exe`** (run in the IntelliJ Terminal):
```cmd
jpackage ^
  --input target ^
  --main-jar Graphify-1.0-fat.jar ^
  --main-class org.example.Launcher ^
  --name Graphify ^
  --app-version 1.0 ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser ^
  --type exe ^
  --dest installer
```

**macOS `.dmg`** (run on a Mac):
```bash
jpackage \
  --input target \
  --main-jar Graphify-1.0-fat.jar \
  --main-class org.example.Launcher \
  --name Graphify \
  --app-version 1.0 \
  --type dmg \
  --dest installer
```

Output is placed in the `installer/` folder.

> 💡 **Portable build (no installer):** Replace `--type exe` / `--type dmg` with `--type app-image` to get a self-contained folder you can ZIP and share without an installer.

---

## 🔧 Troubleshooting

### macOS — "The application is damaged and can't be opened"
The download picked up a quarantine attribute. Fix it with one Terminal command:
```bash
xattr -cr /Applications/Graphify.app
```
Then try launching again.

### macOS — App won't open even after Privacy & Security approval
Try the right-click method instead:
```
Right-click Graphify.app → Open → click "Open" in the dialog
```

### Windows — Installer flagged by antivirus
This is a common false positive for new, unsigned apps. To resolve:
- Temporarily pause real-time protection in your antivirus
- Run `Graphify.exe` → complete the installation
- Re-enable protection

### Windows — App crashes immediately on launch
Ensure you are on **Windows 10 or later**. If the issue persists, try right-clicking the desktop shortcut and selecting **Run as administrator**.

### Graph appears blank after opening
Click the **🔍 re-center button** (top-right corner of the graph) to reset the view to the origin at the default zoom level.

### An equation doesn't plot and shows no error
- Boundary conditions must use curly braces: `y = sin(x) { -pi <= x <= pi }`
- Polar equations require **Polar mode** — select it from the home screen before graphing
- For functions like `sec`, `csc`, `cot`, `cosec` — these are fully supported alongside standard trig
- Check for unmatched parentheses; the parser silently skips malformed expressions

---

## 🛠️ Tech Stack

| Technology | Role |
|---|---|
| [Java 17](https://openjdk.org/projects/jdk/17/) | Core language |
| [JavaFX 21](https://openjfx.io/) | UI, Canvas rendering, 3D intro scene |
| [exp4j 0.4.8](https://www.objecthunter.net/exp4j/) | Math expression evaluator |
| [Apache Maven](https://maven.apache.org/) | Build & packaging |
| [jpackage](https://docs.oracle.com/en/java/javase/17/jpackage/) | Native installer generation (bundled with JDK 17) |

---

## 🙏 Acknowledgements

- [exp4j](https://www.objecthunter.net/exp4j/) by Frank Asseg — powering all equation evaluation
- [Desmos](https://www.desmos.com/) — inspiration for the graphing UX
- The JavaFX community for Canvas API documentation

---

<div align="center">

Made with ❤️ and mathematics

⭐ **If you find Graphify useful, give it a star!** ⭐

</div>