# ICMaze

### The game

ICMaze is a dungeon crawler game where the player must navigate through a series of procedurally generated labyrinthine areas. The objective is to survive, collect resources, and reach the final challenge.
The game features a single player who must manage their health, collect items, and solve environmental puzzles involving signals and keys to progress.
There are various hazards: physical damage from enemies like the LogMonster, and fire damage from the Boss and its minions. Strategy and careful movement are key to survival.

### Controls

The player controls the character using the following keys:
-   **Move**: `Arrow Keys` (Up, Down, Left, Right)
-   **Interact**: `E` (Used to trigger levers, enter portals, etc.)
-   **Attack / Use Item**: `Space` (Used to swing the Pickaxe to break rocks or hit enemies)

Global game controls:
-   **Next Dialog**: `Enter` (Advance text in dialog boxes)
-   **Reset Game**: `R` (Restart the entire game from the beginning)
-   **Pause Game**: `P` (Pause/Unpause the game loop)

### Gameplay

At the beginning, the player spawns in a safe **Spawn Area**. A tutorial dialog may appear to explain the basics. The player starts with basic stats and no items.
The goal is to traverse a linear sequence of levels. To leave an area, the player must find a **Portal**.
Most forward-progressing portals are **Locked**. The player must explore the current maze to find the specific **Key** that matches the portal (e.g., specific ID).

**Exploration & Combat:**
The maze is populated by **LogMonsters**. These enemies wander randomly but will chase the player if spotted.
-   The player can find a **Pickaxe** to defend themselves. Using `Space` with the pickaxe kills enemies and destroys **Rocks** blocking paths.
-   **Signals**: Some areas contain **Logic Signals** (controlled by invisible triggers or game events).
    -   If a signal activates, nearby **LogMonsters** will fall **asleep**, becoming harmless static obstacles.
    -   Active signals can also destroy specific **Rocks** instantly, opening new paths.

**Items & Power-ups:**
Scattered throughout the levels are useful items:
-   **Coins**: Collect them to increase your score (displayed in the HUD).
-   **Kills**: Defeating enemies increments your **Monster Kill Count**, another metric for your score.
-   **Hearts**: Restore lost health points.
-   **SpeedBalls**: Special magical orbs that grant temporary effects.
    -   **Blue SpeedBall**: Grants a speed boost for 10 seconds.
    -   **Red SpeedBall**: Slowness curse for 10 seconds.

**The Final Challenge:**
After clearing the procedural levels, the player reaches the **Boss Area**.
Here, the **Boss** awaits, guarded by its **Lieutenants**.
-   The Boss is a powerful entity that teleports around the arena, raining down fire upon the player.
-   The **Lieutenants** patrol the area. They may seem idle at first, wandering aimlessly, but the moment they spot an intruder, they become aggressive, launching fireballs to stop you.
-   Agility is key. You must weave through their attacks while finding the perfect moment to strike back with your Pickaxe.
-   The Lieutenants are fragile but dangerous. A few well-placed hits will take them down, but don't underestimate their numbers.

**Victory & Rewards:**
Defeating the Boss is not the end!
once the threat is eliminated, the path back is safe. You must **backtrack** through the previous dungeon areas.
Now is the time to explore every corner you missed and collect the **Coins** that were previously too dangerous to reach. Your final score depends on how many riches you can bring back after your victory!

