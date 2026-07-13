# Mocha Mod

A NeoForge 1.21.1 mod that adds **Mocha**, a tameable, GeckoLib-animated companion.

## Features

- **MochaEntity** — a `TamableAnimal` that sits, follows her owner, and can be tamed
  by feeding her a bone (33% chance, like the vanilla wolf). 20 HP, gentle movement,
  never despawns, and her tamed state/owner persist through save/load.
- **Mocha item** — a craftable item (not a spawn egg) that spawns Mocha when
  right-clicked on the ground. Recipe: 4 brown wool + 4 white wool + 1 bone meal (shapeless).
- **Feeding** — right-click Mocha with cooked chicken or cooked beef to heal her 4 HP.
- **Creative tab** — a "Mocha Mod" tab with the Mocha item as its icon.
- **GeckoLib 4** — `MochaEntity` implements `GeoEntity` with an idle/walk animation controller.

## Blockbench asset slots

The mod ships with placeholder assets so it compiles and loads before the real art exists.
Replace these files with your Blockbench exports (keep the same paths and identifiers):

| Asset | Path | Notes |
| --- | --- | --- |
| Model | `src/main/resources/assets/mochamod/geo/entity/mocha.geo.json` | identifier `geometry.mocha` |
| Texture | `src/main/resources/assets/mochamod/textures/entity/mocha.png` | placeholder is 64×64 |
| Animations | `src/main/resources/assets/mochamod/animations/mocha.animation.json` | keep the names `animation.mocha.idle` and `animation.mocha.walk` |

## Building & running

Requires Java 21.

```bash
./gradlew build        # build the mod jar into build/libs/
./gradlew runClient    # launch a dev client
```

## Versions

- Minecraft 1.21.1
- NeoForge 21.1.77
- GeckoLib 4.7
