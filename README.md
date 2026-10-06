# Mocha Mod

A NeoForge 1.21.1 mod that adds **Mocha**, a tameable, GeckoLib-animated companion.

## Features

- **MochaEntity** — a small, tameable `TamableAnimal` companion. A Mocha placed from the
  item is yours straight away; one spawned any other way (e.g. `/summon`) can be tamed with
  a bone (33% chance, like the vanilla wolf). Once tamed she follows you, sits on command, and
  defends you with wolf-style combat AI. 20 HP, never despawns, and her tamed state and
  owner persist through save/load.
- **Mocha item** — a craftable item (not a spawn egg) that spawns Mocha when
  right-clicked on the ground, already tamed to whoever placed her. Recipe: 4 brown wool + 4 white wool + 1 bone meal (shapeless).
- **Feeding** — right-click a tamed Mocha with cooked chicken or cooked beef to heal her 4 HP.
- **Creative tab** — a "Mocha Mod" tab with the Mocha item as its icon.

### Animations (GeckoLib 4)

- Idle, walk (speed-matched so her feet don't slide), gallop, swim, airborne, and landing.
- Sits up when told, settles into a lower rest after ~5 seconds, then dozes off with her eyes
  closed after ~20 seconds; plays a get-up transition when released.
- Begs (head tilt) when you hold a bone or healing food, ears pinned back while fighting,
  plus one-shot tame, eat, shake, stretch, attack, and hurt reactions.
- Head tracking, and a tail that droops lower the more hurt she is.
- Shakes water off after a swim, with splashes and a darker wet coat until she's dry.

### Sounds

Mocha has her own sound events (`mochamod:entity.mocha.*`) with their own subtitles
("Mocha barks", "Mocha snores", ...). `assets/mochamod/sounds.json` currently points them at
the vanilla wolf sounds (and the fox snore), and the entity pitches them up to suit a small dog.
To use real recordings, drop `.ogg` files into `assets/mochamod/sounds/` and point the
entries in `sounds.json` at them; no code changes needed.

## Assets

| Asset | Path |
| --- | --- |
| Model | `src/main/resources/assets/mochamod/geo/entity/mocha.geo.json` (identifier `geometry.mocha`) |
| Textures | `src/main/resources/assets/mochamod/textures/entity/mocha.png`, `mocha_sleeping.png` (64×64, identical UVs) |
| Animations | `src/main/resources/assets/mochamod/animations/mocha.animation.json` |
| Sounds | `src/main/resources/assets/mochamod/sounds.json` |

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
