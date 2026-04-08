# Readable enchanted books

Using an enchanted book opens a lectern like screen showing information about the contained enchantments. This includes a description for each enchantment, the list of mutually exclusive enchantments, the comparator signal and the list of supported items.

![Preview when right-clicking with enchanted book](./doc/item_preview.gif)

# Enchanted books in lecterns

The mod allows placing enchanted books in lecterns. Right-clicking the lectern will open the same interface as using the enchanted book itself.

![Preview when placing the enchanted book in a lectern](./doc/lectern_preview.gif)

## Comparator signal

The enchantments are separated into different groups, each emitting a different comparator signal strength. This signal strength is also shown when opening the books gui.

## Lectern particles

If a lectern contains an enchanted book, the book will emit particles based on the enchantment level of the current page. Lower enchantment levels emit fewer particles compared to higher levels. Curses emit a different particle. When viewing the cover page, the different effects are mixed accordingly.

|                                                                                                                |                                                                                                                                            |
| -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| ![Particle preview with unbreaking 1](./doc/particle_low.gif) <p style="text-align: center;">Unbreaking I</p>  | ![Particle preview with unbreaking 3](./doc/particle_high.gif) <p style="text-align: center;">Unbreaking III</p>                           |
| ![Particle preview with a curse](./doc/particle_curse.gif) <p style="text-align: center;">Curse of Binding</p> | ![Particle preview a curse and unbreaking 3](./doc/particle_both.gif) <p style="text-align: center;">Unbreaking III + Curse of Binding</p> |

# Hopper functionality for lectern

Hoppers are able to input written or enchanted books into lecterns. Additionally they are able to remove the active book of a lectern.

![Preview of the hopper interactions](./doc/hopper_preview.gif)

# Developer

This briefly shows how a custom enchantment can be integrated with this mod.

# Using a different particle effect

The mod allows using a unique particle effect per enchantment. The particle can be set in the `resources/assets/lectern-enchanted-books/data/enchantment_setting.json` file. The default particle effect is `lectern-enchanted-books:enchant_particle`. The particle used for curses is `lectern-enchanted-books:curse_particle`. This can be changed to any custom particle identifier.

# Adding translations

There are 3 ways to add translations to newly added enchantments. Each of them offering different tradeoffs.

## Single descriptive translation

Is compatible with Enchantment Descriptions (https://modrinth.com/mod/enchantment-descriptions). This expects the translation key to follow the format `enchantment.<mod_id>.<enchantment_id>.desc`. Has the lowest priority, can be overwritten if there is a per level or named parameter key.

## Translation per level

To allow adding more detail to the translation, each enchantment level may have a separate translation. For this the translation key is expected to be `enchantment.<mod_id>.<enchantment_id>.desc.level-<level>`. Can be overwritten if there is a named parameter key.

## Translation using named parameters

To allow a more dynamic way of providing per level translations, named parameters may be used. For this the translation key `enchantment.<mod_id>.<enchantment_id>.desc.level-x` is expected. When using this key, the function determines the current values of all effects LevelBasedValues. These are logged to the console with the prefix `LevelBasedValues: ...`. Defining the json file `resources/assets/lectern-enchanted-books/data/enchantment_setting.json` allows assigning names to these LevelBasedValues.

In the following example, the first LevelBasedValue is assigned the name `damage` and uses a custom `transformer` to convert the absolute damage into hearts. See the code for more detail on the `ValueTransformer`. The ordering of the `parameters` must match the ordering of the LevelBasedValues. If the 4-th LevelBasedValue should receive a name, then the 4-th `parameters` element is the one that is used. The last LevelBasedValue is always the enchantment level. This ensure that there is at least one LevelBasedValue, even if the enchantment does not use the default effects logic.

The named parameter can then be used in the translation. In the example: `"Increases damage dealt to spiders by {damage} hearts and applies up to {maxDuration} seconds of slowness IV"`

```json
{
    "enchantment": "enchantment.minecraft.bane_of_arthropods",
    "priority": 0, // Optional to overwrite existing settings
    "parameters": [
      {
        "name": "damage",
        "transformer": "lectern-enchanted-books:damage_to_hearts"
      },
      { "name": "minDuration" }, // No need for transformer, as the value is already in seconds
      { "name": "maxDuration" }
    ]
  },
```
