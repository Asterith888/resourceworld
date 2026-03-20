# Fabric Example Mod
Created from this template available under the CC0 license (see where I forked it from).

Coded with Copilot (because I didn't have the time atm, but I will do my best to turn this into a real mod later on).

# Player Commands
These commands can be used by all players (no OP required):
/resourceworld rtp overworld
Randomly teleports the player to a safe location inside the Resource Overworld (10k radius).
/resourceworld rtp nether
Randomly teleports the player to a safe location inside the Resource Nether (10k radius).
/resourceworld home
Teleports the player back to their spawn point:
- Bed / respawn anchor if set
- Otherwise, the main overworld spawn

# Admin Commands
These commands require operator permission.
/resourceworld reset overworld
Deletes the Resource Overworld folder.
The world will regenerate automatically on the next server restart.
/resourceworld reset nether
Deletes the Resource Nether folder.
Regenerates on next restart.
/resourceworld delete overworld
Deletes the Resource Overworld folder without regenerating until the next restart.
/resourceworld delete nether
Deletes the Resource Nether folder without regenerating until the next restart.
