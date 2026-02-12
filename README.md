# UnF Server Tweaks
Very cool 👍

<hr>

## Why?
I made this mod initially for my community Minecraft server, but after a while decided to put some more work into it and share it with the world.

<hr>

### How long will this be kept updated?
As long as the two Minecraft servers I moderate live. So pretty long I guess.

### Support for other mod loaders or/and older versions?
No. This mod is only for Fabric servers and will not recive updates for older Minecraft versions as the mod's development continues.

<hr>

### Features list

--- Commands ---
<details><summary>/scale | Fun & Administrative</summary>
Allows the players to change their height (scale attribute) to e.g. their irl height to be height-accurate in-game. Players can not change their height again and are limited to choose between being 1-3 meters tall.

Command also faetures some admin tools for unlocking, resetting etc players' heights.
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
<summary>/banish | Administartive</summary>
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

<hr>

### API/Util
The mod has a bit of pretty useful and open tools you can use if you decide to make your own addons.

### Usage Permission
You are not allowed to modify or redistribute the original code, but you can make your own addons that use the mod's internal Util classes which you can redistribute etc just fine. You can also add the mod to server-side modpacks redistributed online.

### Disclaimers
This mod uses Fabric Data Attachment API which is marked as Depricated - It might prove to be unstable or just not work. (Worked in testing tho)