# UnF Server Tweaks
Very cool 👍

<hr>

## Why?
I made this mod initially for my community Minecraft server, but after a while decided to put some more work into it and share it with the world.

<hr>

### How long will this be kept updated?
As long as the two Minecraft servers I moderate live. So pretty long I guess.

### Support for other mod loaders or/and older versions?
No. This mod is only for Fabric servers and will not receive updates for older Minecraft versions as the mod's development continues.

<hr>

### Features list

--- Config ---
You can enable or disable basically any feature, command etc. that this mod provides.

--- Commands ---
<details><summary>/scale | Fun & Administrative</summary>
Allows the players to change their height (scale attribute) to e.g. their irl height to be height-accurate in-game. Players can not change their height again and are limited to choose between being 1-3 meters tall.

Command also features some admin tools for unlocking, resetting etc. players' heights.
</details>
<details><summary>/duel (If Enabled) | Fun</summary>
The command is enabled in the config, all PvP will be disabled, but players can still fight via the duel command.

It exists to prevent unwanted PvP on servers that aren't too keen on it
</details>
<details>
<summary>/daycount | Informative</summary>
To get the tick-exact day count and game time of the server
</details>
<details>
<summary>/afk | Helpful</summary>
Puts the player in a AFK state, indicating their status in tab list, making the afk player unable to move, attack or be attacked.

Configurable. Will despawn hostile mobs around the player by default.
</details>
<details>
<summary>/damagetoggle | Administrative</summary>
Allows players with Operator permissions disabling individual damage types from vanilla Minecraft. Everything goes back to normal after a server restart.
</details>
<details>
<summary>/banish | Administrative</summary>
Allows players with Operator permissions to get rid of people in a way that doesn't require banning so you can laugh at their misery in the endless void just because they broke the rules.
</details>

--- Gameplay Changes ---
<details>
<summary>Removed Anvil Expense Limit</summary>
A mixin to get rid of Anvil's "Too Expensive" limit.
</details>
<details>
<summary>Never Too Fast</summary>
Removed player movement limiters, essentially getting rid of "Player moved too fast!" warn
</details>
<details>
<summary>Better Push Limit</summary>
Allows the admin/owner to change the piston push limit in the mod config
</details>

<hr>

### Usage Permission
You are not allowed to modify or/and redistribute the original code. You are allowed to distribute the mod in modpacks.<br>
The mod will soon move its resources, util classes and Libs/APIs to MysticLib so this mod will no longer be usable as a util library.