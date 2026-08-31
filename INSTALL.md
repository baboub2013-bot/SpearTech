# Spear Tech — Installation

## Players

1. Install Fabric Loader for Minecraft 26.2.
2. Install Meteor Client 26.2.
3. Download `spear-tech-2.0.0.jar`.
4. Press `Win + R`.
5. Enter `%appdata%\.minecraft\mods`.
6. Drop the Spear Tech JAR into that folder next to Meteor Client.
7. Launch Minecraft.
8. Open Meteor ClickGUI and look for the **Spear Tech** category.

## HUD

Open Meteor's HUD editor, choose **Add**, then select **Spear Status** under the **Spear Tech** group.

## Developers

Requires JDK 25.

On Windows, run:

```bat
BUILD.bat
```

The build script automatically bootstraps the official Gradle wrapper JAR if it is not present in the repository checkout. The compiled file is created in `build\libs`.
