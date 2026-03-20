# Fabric Example Mod

## Setup

For setup instructions please see the [fabric documentation page](https://docs.fabricmc.net/develop/getting-started/setting-up) that relates to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

Commands
Root command: /resourceworld

Player Commands
/resourceworld home: Teleport to your spawn point. This is the only way to exit resource worlds without other mods.
/resourceworld tp <world id>: Random teleport to resource worlds.
Admin Commands
/resourceworld create <world id> <mirror/flat> ... [<seed>]: Create a resource world with specific dimension options. Seed is optional, leave blank for random seed.
/resourceworld delete <world id>: Permanently delete a resource world.
/resourceworld reset <world id>: Reset specific resource world. (Delete previous one and create new with different seed.)
/resourceworld enable/disable <world id>: Enable/Disable specific resource world.
/resourceworld settings <world id> <option> [<new value>]: Configure resource world settings.
