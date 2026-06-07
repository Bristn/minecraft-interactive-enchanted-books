![Icon header](./doc/headers/icon_header.gif)

![Tags header](./doc/headers/tags_header.png)

![Client header](./doc/headers/client_header.png)

![Openable Enchanted Books](./doc/headers/openable_header.png)

Using an Enchanted Book shows information about the enchantments. This includes a detailed description, the list of mutually exclusive enchantments, the comparator signal and the list of supported items.

![Preview when right-clicking with Enchanted Book](./doc/game/item_preview.gif)

Besides the enchantment details, the book screen also includes information panels showing the requirements for spawning particles and for cloning Enchanted Books. **These panels are only shown if the mod is also installed on the server**

![Preview for info panels](./doc/game/info_preview.gif)

<details>
<summary>Intentions/Reasoning</summary>

- Seeing the enchantment details without having to leave the game.
- In comparison to other similar mods the book menu is more diegetic than simply showing the enchantment details in the tooltip of the item.

</details>

![Server header](./doc/headers/server_header.png)

![Placeable Enchanted Books in lecterns](./doc/headers/placeable_header.png)

Enchanted Books can be placed in lecterns. Opening the lectern will show the same interface as opening the book regularly.

![Preview when placing the Enchanted Book in a lectern](./doc/game/lectern_preview.gif)

<details>
<summary>Intentions/Reasoning</summary>

- Parity with written books
- Allows for the other features related to Enchanted Books in lecterns

</details>

<hr></hr>

![Comparator signals](./doc/headers/comparator_header.png)

The enchantments are separated into groups, each emitting a different comparator signal strength. This signal strength is also shown in the book screen.

![Comparator signal preview](./doc/game/comparator_preview.gif)

<details>
<summary>List of all comparator signal strengths</summary>

| Power | Description             | Enchantments                                                                                                                                                                        |
| ----- | ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1     | Cover page              |                                                                                                                                                                                     |
| 2     | Universal               | <ul><li> Mending </li><li> Unbreaking </li></ul>                                                                                                                                    |
| 3     | Curses                  | <ul><li> Curse of Binding </li><li> Curse of Vanishing</li></ul>                                                                                                                    |
| 4     | Universal armor         | <ul><li> Protection </li><li> Blast Protection </li><li> Fire Protection </li><li> Projectile Protection </li></ul>                                                                 |
| 5     | Helmets                 | <ul><li> Aqua Affinity </li><li> Respiration</li></ul>                                                                                                                              |
| 6     | Chestplates             | <ul><li> Thorns <ul><li> Not chestplate exclusive </li><li> Ensures category has one entry </li></ul> </li></ul>                                                                    |
| 7     | Leggings                | <ul><li> Swift Sneak </li></ul>                                                                                                                                                     |
| 8     | Boots                   | <ul><li> Depth Strider</li><li> Feather Falling</li><li> Frost Walker </li><li> Soul Speed</li></ul>                                                                                |
| 9     | Mining tools            | <ul><li> Efficiency </li><li> Fortune </li><li> Silk Touch</li></ul>                                                                                                                |
| 10    | Melee weapons damaging  | <ul><li> Sharpness </li><li> Smite </li><li> Bane of Arthropods </li><li> Impaling </li><li> Density </li><li> Breach </li></ul>                                                    |
| 11    | Melee weapons utility   | <ul><li> Fire Aspect </li><li> Sweeping Edge </li><li> Knockback </li><li> Looting </li><li> Wind Burst </li><li> Lunge </li></ul>                                                  |
| 12    | Ranged weapons damaging | <ul><li> Power </li></ul>                                                                                                                                                           |
| 13    | Ranged weapons utility  | <ul><li> Punch </li><li> Infinity </li><li> Flame </li><li> Multishot </li><li> Quick Charge </li><li> Piercing </li><li> Channeling </li><li> Loyalty </li><li> Riptide </li></ul> |
| 14    | Other tools             | <ul><li> Lure </li><li> Luck of the Sea</li></ul>                                                                                                                                   |
| 15    | None/unused             |                                                                                                                                                                                     |

</details>

<details>
<summary>Intentions/Reasoning</summary>

- Enables basic sorting of Enchanted Books based on their enchantments
- Allows encoding of a redstone signal strength sequence based on the enchantments of the book

</details>

<hr></hr>

![Particle header](./doc/headers/particle_header.png)

Lecterns emit particle effects based on the enchantments of the currently selected page. If the cover page is selected, the particles will cycle randomly through the contained enchantments.

The particle lifetime is based on the count of books contained in nearby chiseled bookshelves. If there is no bookshelf or it is empty, no particles will be emitted.

![Preview for particle requirements](./doc/game/particle_preview.gif)

|                                                                                                      |                                                                                                               |
| ---------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| <img src="./doc/game/mending_preview.gif" alt="Mending particle"><p>Mending</p></img>                | <img src="./doc/game/feather_falling_preview.gif" alt="Feather Falling particle"><p>Feather Falling</p></img> |
| <img src="./doc/game/frost_walker_preview.gif" alt="Frost Walker particle"><p>Frost Walker</p></img> | <img src="./doc/game/combined_particle_preview.gif" alt="All 3 enchantments"><p>All 3 particles</p></img>     |

<details>
<summary>List of all particle textures</summary>

Admittedly most of the particle textures can be seen as programmer art

| Texture                                                                                                                                                                                                | Name               | Texture                                                                                                                                                                                                         | Name                  | Texture                                                                                                                                                                                               | Name             |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/aqua_affinity.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Aqua affinity"></img>        | Aqua affinity      | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/bane_of_arthropods.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Bane of arthropods"></img>       | Bane of arthropods    | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/blast_protection.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Blast protection"></img> | Blast protection |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/breach.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Breach"></img>                      | Breach             | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/channeling.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Channeling"></img>                       | Channeling            | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/binding_curse.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Curse of Binding"></img>    | Curse of Binding |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/vanishing_curse.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Curse of Vanishing"></img> | Curse of Vanishing | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/density.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Density"></img>                             | Density               | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/depth_strider.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Depth strider"></img>       | Depth strider    |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/efficiency.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Efficiency"></img>              | Efficiency         | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/feather_falling.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Feather falling"></img>             | Feather falling       | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/fire_aspect.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Fire aspect"></img>           | Fire aspect      |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/fire_protection.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Fire protection"></img>    | Fire protection    | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/flame.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Flame"></img>                                 | Flame                 | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/fortune.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Fortune"></img>                   | Fortune          |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/frost_walker.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Frost walker"></img>          | Frost walker       | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/impaling.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Impaling"></img>                           | Impaling              | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/infinity.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Infinity"></img>                 | Infinity         |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/knockback.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Knockback"></img>                | Knockback          | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/looting.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Looting"></img>                             | Looting               | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/loyalty.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Loyalty"></img>                   | Loyalty          |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/luck_of_the_sea.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Luck of the sea"></img>    | Luck of the sea    | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/lunge.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Lunge"></img>                                 | Lunge                 | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/lure.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Lure"></img>                         | Lure             |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/mending.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Mending"></img>                    | Mending            | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/multishot.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Multishot"></img>                         | Multishot             | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/piercing.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Piercing"></img>                 | Piercing         |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/power.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Power"></img>                        | Power              | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/projectile_protection.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Projectile protection"></img> | Projectile protection | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/protection.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Protection"></img>             | Protection       |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/punch.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Punch"></img>                        | Punch              | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/quick_charge.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Quick charge"></img>                   | Quick charge          | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/respiration.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Respiration"></img>           | Respiration      |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/riptide.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Riptide"></img>                    | Riptide            | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/sharpness.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Sharpness"></img>                         | Sharpness             | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/silk_touch.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Silk touch"></img>             | Silk touch       |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/smite.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Smite"></img>                        | Smite              | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/soul_speed.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Soul speed"></img>                       | Soul speed            | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/sweeping_edge.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Sweeping edge"></img>       | Sweeping edge    |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/swift_sneak.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Swift sneak"></img>            | Swift sneak        | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/thorns.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Thorns"></img>                               | Thorns                | <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/unbreaking.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Unbreaking"></img>             | Unbreaking       |
| <img src="./src/main/resources/assets/interactive_enchanted_books/textures/particle/wind_burst.png" width="64px" height="64px" style="image-rendering: pixelated" alt="Wind burst"></img>              | Wind burst         |                                                                                                                                                                                                                 |                       |                                                                                                                                                                                                       |                  |

</details>

<details>
<summary>Intentions/Reasoning</summary>

- Allows an easy way of spawning a variety of particle effects.
- The other redstone related features enabling a dynamic way of changing particle textures and lifetime.

</details>

<hr></hr>

![Hopper integration](./doc/headers/hopper_header.png)

Disable with: `/gamerule interactive_enchanted_books:hopper_interacts_with_lectern false`

Hoppers are able to place written or Enchanted Books in lecterns. Additionally they are able to remove the active book of a lectern.

![Preview of the hopper interactions](./doc/game/hopper_preview.gif)

<details>
<summary>Intentions/Reasoning</summary>

- Parity with other block entities and behaviour. Not being able to place books in lecterns using hoppers seems like an oversight in vanilla
- Allows for greater control over the enchantment particles of this mod

</details>

<hr></hr>

![Signal changing page](./doc/headers/signal_header.png)

Disable with: `/gamerule interactive_enchanted_books:signal_changes_lectern_page false`

Sending a redstone signal to the lectern increments the shown page. If the last page is reached, the lectern will loop back to the first page. **This disables the default behaviour of page changes emitting a redstone signal. Observers will still detect the page change**

![Signal changing page preview](./doc/game/signal_preview.gif)

<details>
<summary>Intentions/Reasoning</summary>

- As lecterns with Enchanted Books emit particles based on the current page, changing the page with a redstone signal allows dynamic control over which particle is currently shown
- In combination with the chiseled bookshelves, this allows for flexible particle effects

</details>

<hr></hr>

![Echo header](./doc/headers/echo_header.png)

Disable with: `/gamerule interactive_enchanted_books:craftable_enchantment_echo false`

The Enchantment Echo clones the enchantments of the original item **without** destroying it and can be interacted with just as a regular Enchanted Book (see previous features). Combining the Enchantment Echo with an Echo Shard converts it into an Enchanted Book.

|                                                                       |                                                                                  |
| --------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| <img src="./doc/recipes/echo_crafting.gif" alt="Echo crafting"></img> | <img src="./doc/recipes/enchanted_book.png" alt="Enchanted Book crafting"></img> |

![Echo crafting in game](./doc/game/echo_crafting_preview.gif)

<details>
<summary>Intentions/Reasoning</summary>

- The Enchantment Echos allow easier usage of the other mod features, such as comparator signal and particles, without having to combine multiple Enchanted Books
- The ability to create an Enchantment Echo from an already enchanted item enables easier insight into the enchantment details. When playing a modpack it's easy to find items with tons of enchantments without knowing most of them. Enchantment Echos provide an easy way to preview the details in game
- The conversion to a real Enchanted Book is intended to reduce the need for villagers while not being too overpowered, as Echo Shards are a finite end game resource.

</details>

<hr></hr>

![Integration header](./doc/headers//integration_header.png)

See the [example repository](https://github.com/Bristn/minecraft-interactive-enchanted-books-example) for more details on how to integrate custom enchantments.
