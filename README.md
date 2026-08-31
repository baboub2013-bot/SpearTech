# Spear Tech 2.0

A polished spear-focused addon built against **Meteor Client 26.2** for Minecraft 26.2.

Spear Tech uses Meteor's native module, settings and HUD systems and keeps the UI intentionally close to Meteor's own style.

## Included modules

### Combat
- **Spear Swap** — smart hotbar swapping between Lunge and reach-focused spears.
- **Spear Reach** — configurable entity interaction range while a spear is selected.
- **One Shot** — configurable spear damage modifier with Minimum and Multiplier modes.
- **Spear Cooldown** — blocks weak early swings until the configured charge is reached.

### Movement
- **Lunge Boost** — configurable horizontal and vertical impulse after a Lunge spear attack.
- **Spear Velocity** — configurable movement momentum while moving with a spear.

### Visuals
- **Spear Range Preview** — renders a live 3D line for the current entity interaction range.
- **Spear Status HUD** — shows attack charge, effective reach and Lunge level in Meteor's HUD editor.

## Quick setup

1. Install **Fabric Loader 0.19.3+**.
2. Install **Meteor Client 26.2**.
3. Put `spear-tech-2.0.0.jar` in your Minecraft `mods` folder.
4. Start Minecraft and open Meteor's ClickGUI.
5. Open the **Spear Tech** category.
6. For the HUD: open Meteor HUD editor → add **Spear Status** from the **Spear Tech** group.

## Recommended starter config

**Normal training**
- Spear Swap: ON
- Spear Cooldown: 92%
- Lunge Boost: 0.75 horizontal / 0 vertical
- Spear Range Preview: ON
- Spear Status HUD: ON

**Movement testing**
- Lunge Boost: 1.25 horizontal
- Spear Velocity: 1.08x / max speed 0.55

**Damage testing**
- One Shot: Minimum mode / 100 damage

## Scope

Gameplay-changing modifiers are intentionally scoped to worlds hosted and controlled by the user. Visual-only features such as the range preview do not alter game state.

## Build from source

Requires **JDK 25** and Internet access for Gradle dependencies.

On Windows, the easiest method is:

```bat
BUILD.bat
```

`BUILD.bat` validates JDK 25, bootstraps the official Gradle 9.6.1 wrapper JAR when needed, and runs a clean build.

Output:

```text
build/libs/spear-tech-2.0.0.jar
```

## Target versions

- Minecraft: **26.2**
- Meteor Client: **26.2-SNAPSHOT**
- Fabric Loader: **0.19.3**
- Java: **25**
- Loom: **1.17-SNAPSHOT**

## License

GPL-3.0-only.
