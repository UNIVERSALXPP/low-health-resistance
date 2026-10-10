# Low Health Resistance

Fabric mod for Minecraft **1.21.11**.

- A hit that would kill you (fall damage, mobs, players, explosions...) **does not kill you**. You are left at **1 heart** instead.
- At 1 heart you get **infinite Resistance 99** (full damage immunity).
- When you heal up to **2 hearts (4 HP) or more**, the effect is removed.
- If you drop to 1 heart again, it comes back automatically. If milk removes it at 1 heart, it is re-applied.
- The **void** and **/kill** still kill you normally (otherwise you would be saved forever while falling out of the world).

Needs Fabric Loader and Fabric API. Works in singleplayer and on servers (install on the server; clients do not need it).

## Build the JAR with GitHub

1. Create a new GitHub repository and upload everything from this folder (keep the `.github` folder).
2. Open the **Actions** tab. The build runs on every push (or press **Run workflow**).
3. When it finishes, open the run and download **LowHealthResistance-jar** from Artifacts.
4. Put `low-health-resistance-1.1.0.jar` (not the `-sources` one) into your `mods` folder.

## Settings

Edit `LowHealthResistance.java`:

- `AMPLIFIER` - effect level (`99` = Resistance 99; `4` or higher is already full immunity)
- `REMOVE_AT_OR_ABOVE` - health (in HP) at which the effect is removed (4 HP = 2 hearts)
