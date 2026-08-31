# Spear Tech

Spear Tech is a Meteor Client addon for Minecraft 26.2 focused on spear combat, movement and visualization.

The repository is intentionally small and review-friendly: the behavior is split into named modules, the Minecraft hooks are isolated in two Mixins, and there is no obfuscation or generated source.

## Modules

| Module | Purpose |
| --- | --- |
| Spear Swap | Selects an appropriate spear from the hotbar before an attack. |
| Lunge Boost | Adds a configurable impulse after a Lunge piercing attack. |
| Spear Reach | Adjusts entity interaction range while using a spear. |
| One Shot | Adjusts spear attack damage using a minimum or multiplier mode. |
| Spear Cooldown | Prevents attacks below a configurable attack-charge threshold. |
| Spear Velocity | Applies a configurable horizontal movement multiplier. |
| Spear Range Preview | Draws the current interaction range in-world. |
| Spear Status HUD | Shows spear charge, reach and Lunge level. |

## Code map

```text
src/main/java/babou/speartech/
├── SpearTechAddon.java        # Registration only
├── modules/                   # One Meteor module per file
├── mixin/                     # Minecraft hooks used by Reach, Damage and Lunge
├── hud/                       # Visual-only Meteor HUD element
└── util/WorldGuard.java       # Environment checks for gameplay modifiers
```

The two Mixins are deliberately small:

- `PlayerMixin` forwards `entityInteractionRange()` to `SpearReach`.
- `LivingEntityMixin` forwards attack damage to `OneShot` and completed piercing attacks to `LungeBoost`.

## Build

Requirements:

- JDK 25
- Gradle 9.6.1
- Internet access for Fabric/Meteor dependencies

```bash
gradle build
```

The compiled JAR is written to `build/libs/`.

## Target

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Meteor Client 26.2
- Java 25

## License

GPL-3.0-only.
