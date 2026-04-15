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

TODO: Link

See the example repository for more details on how to integrate custom enchantments.
